/*
 * uart_link.c  (refactored + hardened)
 *
 *  Created on: 2025. 10. 26.
 *  Author: SSAFY
 */

#include "uart_link.h"
#include "frame.h"
#include "esp_err.h"
#include "esp_log.h"
#include "esp_check.h"
#include "driver/uart.h"
#include "freertos/FreeRTOS.h"
#include "freertos/queue.h"
#include "freertos/task.h"

#include <string.h>
#include <stdlib.h>
#include <ctype.h>
#include <inttypes.h>

/* ===================== Config ===================== */
#define LINK_UART_PORT    UART_NUM_1        // (선택) DevKitC 관례: UART_NUM_2 + TX17/RX16
#define LINK_UART_BAUD    115200
#define LINK_UART_TX_PIN  17
#define LINK_UART_RX_PIN  16

#define RX_TASK_STACK     4096
#define RX_TASK_PRIO      (tskIDLE_PRIORITY + 5)
#define RX_BUF_BYTES      2048
#define RX_QUEUE_LEN      4
#define USB_ADVERT_MAX_FILES 10

static const char* TAG = "uart_link";

/* ===================== State ===================== */
static QueueHandle_t   s_rxq;         // queue of frame_t*
static frame_parser_t  s_fp;
static volatile bool   s_ready = false;

/* 옵션: 원시 바이트 스니프(디버깅용) */
static bool s_sniff = false;
void uart_link_set_sniff(bool on){ s_sniff = on; } // ← uart_link.h에 프로토타입 추가 권장

/* ===================== Small helpers ===================== */
static inline const void* memmem_simple(const void* h, size_t hlen,
                                        const void* n, size_t nlen)
{
    if (!h || !n || !nlen || nlen > hlen) return NULL;
    const unsigned char* H = (const unsigned char*)h;
    const unsigned char* N = (const unsigned char*)n;
    size_t last = hlen - nlen;
    for (size_t i = 0; i <= last; ++i)
        if (H[i] == N[0] && memcmp(&H[i], N, nlen) == 0) return &H[i];
    return NULL;
}

static inline const char* skip_ws_b(const char* p, const char* end){
    while (p < end && (*p==' ' || *p=='\n' || *p=='\r' || *p=='\t')) p++;
    return p;
}

static inline uint64_t parse_uint_b(const char* p, const char* end){
    uint64_t v = 0;
    while (p < end && *p >= '0' && *p <= '9'){ v = v*10 + (uint64_t)(*p - '0'); p++; }
    return v;
}

/* ===================== UART Low-level ===================== */
static esp_err_t uart_hw_init(void)
{
    const uart_config_t cfg = {
        .baud_rate  = LINK_UART_BAUD,
        .data_bits  = UART_DATA_8_BITS,
        .parity     = UART_PARITY_DISABLE,
        .stop_bits  = UART_STOP_BITS_1,
        .flow_ctrl  = UART_HW_FLOWCTRL_DISABLE,
        .source_clk = UART_SCLK_APB,
    };
    ESP_ERROR_CHECK(uart_param_config(LINK_UART_PORT, &cfg));
    ESP_ERROR_CHECK(uart_set_pin(LINK_UART_PORT, LINK_UART_TX_PIN, LINK_UART_RX_PIN,
                                 UART_PIN_NO_CHANGE, UART_PIN_NO_CHANGE));

    // RX-only 드라이버 설치 (TX 링버퍼 불필요)
    ESP_ERROR_CHECK(uart_driver_install(LINK_UART_PORT, RX_BUF_BYTES, 0, 0, NULL, 0));

    // 부팅/접속 직후 잔여 바이트 제거 (노이즈/부트로그 정리)
    ESP_ERROR_CHECK(uart_flush_input(LINK_UART_PORT));

    // 수신 인터벌이 애매한 스트림에서 파편화 지연 완화(문턱: 약 2문자 시간)
    ESP_ERROR_CHECK(uart_set_rx_timeout(LINK_UART_PORT, 2));

    return ESP_OK;
}

static inline int uart_send_bytes(const uint8_t* data, size_t len){
    if (!data || !len) return -1;
    return uart_write_bytes(LINK_UART_PORT, (const char*)data, len);
}

/* ===================== Frame IO (req/resp) ===================== */
static bool enqueue_frame_copy(const frame_t* f)
{
    frame_t* pf = (frame_t*)malloc(sizeof(frame_t));
    if (!pf) { ESP_LOGE(TAG, "rx: OOM"); return false; }
    *pf = *f;
    if (xQueueSend(s_rxq, &pf, 0) != pdTRUE){ free(pf); return false; }
    return true;
}

static esp_err_t send_frame(uint8_t type, const uint8_t* payload, uint16_t plen)
{
    uint8_t fbuf[FRAME_MAX_WIRE];
    size_t  flen = 0;
    frame_err_t fer = frame_build(type, payload, plen, fbuf, sizeof(fbuf), &flen);
    if (fer != FRAME_OK){ ESP_LOGE(TAG, "frame build fail %d", fer); return ESP_FAIL; }
    int wr = uart_send_bytes(fbuf, flen);
    if (wr < 0){ ESP_LOGE(TAG, "uart write fail"); return ESP_FAIL; }
    return ESP_OK;
}

static esp_err_t recv_frame(frame_t* out, TickType_t to)
{
    frame_t* rx = NULL;
    if (xQueueReceive(s_rxq, &rx, to) != pdTRUE) return ESP_ERR_TIMEOUT;
    if (out) *out = *rx;
    free(rx);
    return ESP_OK;
}

static inline esp_err_t link_req_resp(uint8_t type, const uint8_t* pl, uint16_t plen,
                                      frame_t* resp, TickType_t to)
{
    ESP_RETURN_ON_ERROR(send_frame(type, pl, plen), TAG, "send fail");
    return recv_frame(resp, to);
}

/* ===================== Parser feed ===================== */
static void feed_bytes_and_emit(const uint8_t* data, size_t n)
{
    size_t off = 0;
    frame_t f;
    while (off < n){
        size_t consumed = 0;
        frame_parse_status_t st = frame_parser_feed(&s_fp, &data[off], n - off, &f, &consumed);
        off += consumed;

        if (st == FP_EMIT){
            (void)enqueue_frame_copy(&f);
            continue;
        }
        if (st == FP_MORE){
            // 더 입력 필요: 다음 uart_read_bytes()까지 대기
            break;
        }
        // RESYNC_* 등: 아직 남은 입력이 있으면 한 번 더 시도하여 진행성 확보
        if (off < n) continue;
        break;
    }
}

/* ===================== RX task ===================== */
static void link_rx_task(void* arg)
{
    uint8_t buf[256];
    for(;;){
        int n = uart_read_bytes(LINK_UART_PORT, buf, sizeof(buf), pdMS_TO_TICKS(50));
        if (n > 0) {
            if (s_sniff){
                // 원시 바이트 스니프 (디버깅용)
                for (int i = 0; i < n; ++i) ESP_LOGI(TAG, "RX %02X", buf[i]);
            }
            feed_bytes_and_emit(buf, (size_t)n);
        }
        // 낮은 우선순위 태스크에 양보(쓸모 없는 바쁜 대기 방지)
        taskYIELD();
    }
}

/* ===================== JSON helpers (media index) ===================== */
typedef struct {
    const char* js;
    size_t      js_len;
} json_view_t;

static void parse_files_array(const json_view_t* v, media_index_t* out)
{
    static media_item_t items[USB_ADVERT_MAX_FILES];
    const char key_files[] = "\"files\"";
    const char* js = v->js; const char* js_end = v->js + v->js_len;

    out->count = 0; out->items = NULL;
    const char* kf = (const char*)memmem_simple(js, v->js_len, key_files, sizeof(key_files)-1);
    if (!kf){ out->gen = 1; return; }

    size_t remain = (size_t)(js_end - kf);
    const char* colon = (const char*)memchr(kf, ':', remain);
    if (!colon){ out->gen = 1; return; }

    const char* p = skip_ws_b(colon+1, js_end);
    if (p >= js_end || *p != '['){ out->gen = 1; return; }
    p++; // after '['

    uint32_t count = 0;
    while (p < js_end && count < USB_ADVERT_MAX_FILES){
        p = skip_ws_b(p, js_end);
        if (p >= js_end) break;
        if (*p == ']'){ p++; break; }
        if (*p != '{'){
            const char* next_comma = (const char*)memchr(p, ',', (size_t)(js_end-p));
            const char* next_rb    = (const char*)memchr(p, ']', (size_t)(js_end-p));
            if (!next_comma && !next_rb) break;
            p = next_comma && (!next_rb || next_comma < next_rb) ? next_comma+1 : next_rb;
            continue;
        }
        p++; // inside object

        memset(&items[count], 0, sizeof(items[count]));
        while (p < js_end && *p != '}'){
            p = skip_ws_b(p, js_end);
            if (p >= js_end || *p == '}') break;

            if ((js_end - p) >= 4 && strncmp(p, "\"id\"", 4) == 0){
                const char* c = (const char*)memchr(p, ':', (size_t)(js_end - p)); if (!c) break;
                c = skip_ws_b(c+1, js_end);
                if (c < js_end && *c=='\"'){
                    c++;
                    char* dst = items[count].id;
                    while (c < js_end && *c!='\"' &&
                           (dst - items[count].id) < (ptrdiff_t)sizeof(items[count].id)-1){ *dst++ = *c++; }
                    *dst = 0;
                    if (c < js_end && *c=='\"') c++;
                    p = c;
                } else { p = c; }
            }
            else if ((js_end - p) >= 6 && strncmp(p, "\"name\"", 6) == 0){
                const char* c = (const char*)memchr(p, ':', (size_t)(js_end - p)); if (!c) break;
                c = skip_ws_b(c+1, js_end);
                if (c < js_end && *c=='\"'){
                    c++;
                    char* dst = items[count].name;
                    while (c < js_end && *c!='\"' &&
                           (dst - items[count].name) < (ptrdiff_t)sizeof(items[count].name)-1){ *dst++ = *c++; }
                    *dst = 0;
                    if (c < js_end && *c=='\"') c++;
                    p = c;
                } else { p = c; }
            }
            else if ((js_end - p) >= 6 && strncmp(p, "\"size\"", 6) == 0){
                const char* c = (const char*)memchr(p, ':', (size_t)(js_end - p)); if (!c) break;
                c = skip_ws_b(c+1, js_end);
                items[count].size = (uint32_t)parse_uint_b(c, js_end);
                while (c < js_end && *c >= '0' && *c <= '9') c++;
                p = c;
            }
            else {
                const char* next = p; while (next < js_end && *next != ',' && *next != '}') next++; p = next;
            }

            p = skip_ws_b(p, js_end);
            if (p < js_end && *p == ',') p++;
        }
        if (p < js_end && *p == '}') p++;
        items[count].index = count;
        count++;

        p = skip_ws_b(p, js_end);
        if (p < js_end && *p == ','){ p++; continue; }
        if (p < js_end && *p == ']'){ p++; break; }
    }

    out->count = count;
    if (count == 0){ out->items = NULL; return; }

    out->items = (media_item_t*)malloc(sizeof(media_item_t) * count);
    if (!out->items){ ESP_LOGE(TAG, "media index: oom"); out->count = 0; }
    else memcpy(out->items, items, sizeof(media_item_t) * count);
}

static uint32_t parse_gen_or_default(const json_view_t* v, uint32_t defv)
{
    const char key_gen[] = "\"gen\"";
    const char* js = v->js; const char* js_end = v->js + v->js_len;

    const char* pg = (const char*)memmem_simple(js, v->js_len, key_gen, sizeof(key_gen)-1);
    if (!pg) return defv;

    size_t remain = (size_t)(js_end - pg);
    const char* colon = (const char*)memchr(pg, ':', remain);
    if (!colon) return defv;

    const char* c = skip_ws_b(colon+1, js_end);
    return (uint32_t)parse_uint_b(c, js_end);
}

/* ===================== Public: init ===================== */
esp_err_t uart_link_init(void)
{
    ESP_ERROR_CHECK(uart_hw_init());

    frame_parser_init(&s_fp);

    s_rxq = xQueueCreate(RX_QUEUE_LEN, sizeof(frame_t*));        // (length, item_size)
    if (!s_rxq){ ESP_LOGE(TAG, "queue create fail"); return ESP_ERR_NO_MEM; }

    if (xTaskCreate(link_rx_task, "link_rx", RX_TASK_STACK, NULL, RX_TASK_PRIO, NULL) != pdPASS){
        vQueueDelete(s_rxq); s_rxq = NULL;
        ESP_LOGE(TAG, "rx task create fail");
        return ESP_ERR_NO_MEM;
    }

    s_ready = true;
    ESP_LOGI(TAG, "uart_link ready (U1 TX=%d RX=%d, %d baud)", LINK_UART_TX_PIN, LINK_UART_RX_PIN, LINK_UART_BAUD);
    return ESP_OK;
}

/* ===================== Public: RPCs ===================== */
esp_err_t uart_link_lic_mgr_login(const char *id, const char *pw, bool *ok)
{
    if (!s_ready || !s_rxq || !id || !pw || !ok) return ESP_ERR_INVALID_STATE;

    char buf[96];
    int n = snprintf(buf, sizeof(buf), "%s:%s", id, pw);
    if (n < 0) return ESP_FAIL;

    frame_t resp;
    ESP_RETURN_ON_ERROR(link_req_resp(FRAME_LIC_MGR_LOGIN,
                           (const uint8_t*)buf, (uint16_t)n,
                           &resp, pdMS_TO_TICKS(1000)), TAG, "rpc fail");
    *ok = (resp.len >= 1 && resp.payload[0] == 1);
    return ESP_OK;
}

esp_err_t uart_link_lic_issue(const char *app, const char *to, char *out_lic, size_t out_sz)
{
    if (!s_ready || !s_rxq || !app || !to || !out_lic || out_sz==0) return ESP_ERR_INVALID_STATE;

    char buf[128];
    int n = snprintf(buf, sizeof(buf), "%s:%s", app, to);
    if (n < 0) return ESP_FAIL;

    frame_t resp;
    ESP_RETURN_ON_ERROR(link_req_resp(FRAME_LIC_ISSUE,
                           (const uint8_t*)buf, (uint16_t)n,
                           &resp, pdMS_TO_TICKS(1000)), TAG, "rpc fail");

    if (resp.len < 1 || resp.payload[0] == 0) return ESP_FAIL;

    size_t lic_len = resp.len - 1;
    if (lic_len + 1 > out_sz) lic_len = out_sz - 1;
    memcpy(out_lic, &resp.payload[1], lic_len);
    out_lic[lic_len] = 0;
    return ESP_OK;
}

esp_err_t uart_link_lic_validate(const char *lic, bool *ok)
{
    if (!s_ready || !s_rxq || !lic || !ok) return ESP_ERR_INVALID_STATE;

    frame_t resp;
    ESP_RETURN_ON_ERROR(link_req_resp(FRAME_LIC_VALIDATE,
                           (const uint8_t*)lic, (uint16_t)strlen(lic),
                           &resp, pdMS_TO_TICKS(1000)), TAG, "rpc fail");
    *ok = (resp.len >= 1 && resp.payload[0] == 1);
    return ESP_OK;
}

esp_err_t uart_link_lic_get_jwt(const char *lic, char *out_jwt, size_t out_sz)
{
    if (!s_ready || !s_rxq || !lic || !out_jwt || out_sz==0) return ESP_ERR_INVALID_STATE;

    frame_t resp;
    ESP_RETURN_ON_ERROR(link_req_resp(FRAME_LIC_GET_JWT,
                           (const uint8_t*)lic, (uint16_t)strlen(lic),
                           &resp, pdMS_TO_TICKS(1000)), TAG, "rpc fail");

    if (resp.len < 1 || resp.payload[0] == 0) return ESP_FAIL;

    size_t jn = resp.len - 1;
    if (jn + 1 > out_sz) jn = out_sz - 1;
    memcpy(out_jwt, &resp.payload[1], jn);
    out_jwt[jn] = 0;
    return ESP_OK;
}

esp_err_t uart_link_get_index(media_index_t *out)
{
    if (!out) return ESP_ERR_INVALID_ARG;
    if (!s_ready || !s_rxq) return ESP_ERR_INVALID_STATE;

    frame_t resp;
    ESP_RETURN_ON_ERROR(link_req_resp(FRAME_MEDIA_INDEX_REQ, NULL, 0,
                           &resp, pdMS_TO_TICKS(1000)), TAG, "rpc fail");

    json_view_t jv = { .js = (const char*)resp.payload, .js_len = resp.len };
    out->gen = parse_gen_or_default(&jv, 1);
    parse_files_array(&jv, out);
    return ESP_OK;
}

esp_err_t uart_link_read_chunk(const char *id, uint64_t off, uint32_t len,
                               uint8_t *out, uint32_t *out_len, uint32_t *out_crc)
{
    if (!id || !out || !out_len || !out_crc) return ESP_ERR_INVALID_ARG;
    if (!s_ready || !s_rxq) return ESP_ERR_INVALID_STATE;

    uint8_t pl[1 + 64 + 8 + 4];
    size_t  p = 0;

    size_t idlen = strlen(id);
    if (idlen + 1 > sizeof(pl)) return ESP_ERR_INVALID_ARG;

    memcpy(&pl[p], id, idlen + 1);  p += idlen + 1;      // zero-terminated 포함
    pl[p++] = (uint8_t)(off >> 56); pl[p++] = (uint8_t)(off >> 48);
    pl[p++] = (uint8_t)(off >> 40); pl[p++] = (uint8_t)(off >> 32);
    pl[p++] = (uint8_t)(off >> 24); pl[p++] = (uint8_t)(off >> 16);
    pl[p++] = (uint8_t)(off >> 8 ); pl[p++] = (uint8_t)(off      );
    pl[p++] = (uint8_t)(len >> 24); pl[p++] = (uint8_t)(len >> 16);
    pl[p++] = (uint8_t)(len >> 8 ); pl[p++] = (uint8_t)(len      );

    ESP_RETURN_ON_ERROR(send_frame(FRAME_MEDIA_PULL_REQ, pl, (uint16_t)p), TAG, "send fail");

    frame_t* rx = NULL;
    if (xQueueReceive(s_rxq, &rx, pdMS_TO_TICKS(1000)) != pdTRUE) return ESP_ERR_TIMEOUT;

    if (rx->type != FRAME_MEDIA_CHUNK){ free(rx); return ESP_FAIL; }

    if (rx->len > len) rx->len = len;
    memcpy(out, rx->payload, rx->len);
    *out_len = rx->len;
    *out_crc = 0; // TODO: CRC 전달/검증 필요시 구현
    free(rx);
    return ESP_OK;
}

/* 현재 미지원 */
esp_err_t uart_link_auth_req(const char* ssaid, auth_ssaid_resp_t* out)
{ return ESP_ERR_NOT_SUPPORTED; }

bool uart_link_usb_attached(void) { return true; }
