/*
 * uart_link.c
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */

#include "uart_link.h"
#include "esp_err.h"
#include "frame.h"
#include "esp_log.h"
#include "driver/uart.h"
#include "freertos/FreeRTOS.h"
#include "freertos/projdefs.h"
#include "freertos/queue.h"
#include "freertos/task.h"
#include "mbedtls/pk.h"

#include <string.h>
#include <stdlib.h>
#include <ctype.h>

#define LINK_UART_PORT		UART_NUM_1
#define LINK_UART_BAUD		115200
#define LINK_UART_TX_PIN	41
#define LINK_UART_RX_PIN	40

#define USB_ADVERT_MAX_FILES 10

static const char* TAG = "uart_link";

static uart_event_cb_t s_cb;
static bool s_attached = true;
static media_index_t s_idx;

static QueueHandle_t s_rxq;
static frame_parser_t s_fp;
static volatile bool s_ready = false;


static const void* memmem_simple(const void* h, size_t hlen,
                                 const void* n, size_t nlen)
{
    if (!h || !n || !nlen || nlen > hlen) return NULL;
    const unsigned char* H = (const unsigned char*)h;
    const unsigned char* N = (const unsigned char*)n;
    size_t last = hlen - nlen;
    for (size_t i = 0; i <= last; ++i) {
        if (H[i] == N[0] && memcmp(&H[i], N, nlen) == 0) return &H[i];
    }
    return NULL;
}

static const char* skip_ws_b(const char* p, const char* end){
    while (p < end && (*p==' ' || *p=='\n' || *p=='\r' || *p=='\t')) p++;
    return p;
}

static uint64_t parse_uint_b(const char* p, const char* end){
    uint64_t v = 0;
    while (p < end && *p >= '0' && *p <= '9'){
        v = v*10 + (uint64_t)(*p - '0');
        p++;
    }
    return v;
}

static void link_rx_task(void* arg){
	uint8_t buf[256];
	frame_t f;
	size_t	consumed = 0;
	
	while(1){
		int n = uart_read_bytes(LINK_UART_PORT, buf, sizeof(buf), pdMS_TO_TICKS(50));
		if( n <= 0) continue;
		
		size_t off = 0;
		while(off < (size_t)n){
			frame_parse_status_t st = frame_parser_feed(&s_fp, &buf[off], (size_t)(n - off), &f, &consumed);
			off += consumed;
			
			if(st == FP_EMIT){
				frame_t* pf = malloc(sizeof(frame_t));
				if(!pf){
					ESP_LOGE(TAG, "oom");
					continue;
				}
				*pf =f;
				if(xQueueSend(s_rxq, &pf, 0)!= pdTRUE)
					free(pf);
				ESP_LOGI(TAG, "frame received");
			}
		}
	}
}

static esp_err_t link_req_resp(uint8_t type, const uint8_t* payload, uint16_t plen, frame_t* out, TickType_t to){
	uint8_t fbuf[FRAME_MAX_WIRE];
	size_t flen = 0;
	frame_err_t fer = frame_build(type, payload, plen, fbuf, sizeof(fbuf), &flen);
	if(fer != FRAME_OK){
		ESP_LOGE(TAG, "frame build fail %d", fer);
		return ESP_FAIL;
	}
	
	int wr = uart_write_bytes(LINK_UART_PORT, fbuf, flen);
	if(wr < 0){
		ESP_LOGE(TAG, "uart write fail");
		return ESP_FAIL;
	}
	
	frame_t* rx = NULL;
	if(xQueueReceive(s_rxq, &rx, to) != pdTRUE){
		ESP_LOGE(TAG, "rpc timeout");
		return ESP_ERR_TIMEOUT;
	}
	
	if(out)
		*out = *rx;
		
	free(rx);
	return ESP_OK;
}

esp_err_t uart_link_init(void)
{
    /* UART 드라이버 설치 */
    const uart_config_t cfg = {
        .baud_rate = LINK_UART_BAUD,
        .data_bits = UART_DATA_8_BITS,
        .parity    = UART_PARITY_DISABLE,
        .stop_bits = UART_STOP_BITS_1,
        .flow_ctrl = UART_HW_FLOWCTRL_DISABLE
    };
    ESP_ERROR_CHECK(uart_driver_install(LINK_UART_PORT, 2048, 0, 0, NULL, 0));
    ESP_ERROR_CHECK(uart_param_config(LINK_UART_PORT, &cfg));
    ESP_ERROR_CHECK(uart_set_pin(LINK_UART_PORT,
                                 LINK_UART_TX_PIN,
                                 LINK_UART_RX_PIN,
                                 UART_PIN_NO_CHANGE,
                                 UART_PIN_NO_CHANGE));

    frame_parser_init(&s_fp);
    s_rxq = xQueueCreate(4, sizeof(frame_t*));
    if(!s_rxq) return ESP_ERR_NO_MEM;
    
    xTaskCreate(link_rx_task, "link_rx", 4096, NULL, 5, NULL);
    s_ready = true;
    ESP_LOGI(TAG, "uart_link ready");
    return ESP_OK;
}

/* ===== STM32 라이선스 프레임에 1:1로 대응하는 함수들 ===== */

esp_err_t uart_link_lic_mgr_login(const char *id, const char *pw, bool *ok)
{
	if (!s_ready || !s_rxq) return ESP_ERR_INVALID_STATE;
    char buf[96];
    int n = snprintf(buf, sizeof(buf), "%s:%s", id, pw);
    frame_t resp;
    esp_err_t er = link_req_resp(FRAME_LIC_MGR_LOGIN,
                            (const uint8_t*)buf, (uint16_t)n,
                            &resp,
                            pdMS_TO_TICKS(1000));
    if (er != ESP_OK) return er;
    *ok = (resp.len >= 1 && resp.payload[0] == 1);
    return ESP_OK;
}

esp_err_t uart_link_lic_issue(const char *app, const char *to, char *out_lic, size_t out_sz)
{
	if (!s_ready || !s_rxq) return ESP_ERR_INVALID_STATE;
    char buf[128];
    int n = snprintf(buf, sizeof(buf), "%s:%s", app, to);
    frame_t resp;
    esp_err_t er = link_req_resp(FRAME_LIC_ISSUE,
                            (const uint8_t*)buf, (uint16_t)n,
                            &resp,
                            pdMS_TO_TICKS(1000));
    if (er != ESP_OK) return er;
    if (resp.len < 1 || resp.payload[0] == 0) return ESP_FAIL;
    size_t lic_len = resp.len - 1;
    if (lic_len + 1 > out_sz) lic_len = out_sz - 1;
    memcpy(out_lic, &resp.payload[1], lic_len);
    out_lic[lic_len] = 0;
    return ESP_OK;
}

esp_err_t uart_link_lic_validate(const char *lic, bool *ok)
{
	if (!s_ready || !s_rxq) return ESP_ERR_INVALID_STATE;
    frame_t resp;
    esp_err_t er = link_req_resp(FRAME_LIC_VALIDATE,
                            (const uint8_t*)lic, (uint16_t)strlen(lic),
                            &resp,
                            pdMS_TO_TICKS(1000));
    if (er != ESP_OK) return er;
    *ok = (resp.len >= 1 && resp.payload[0] == 1);
    return ESP_OK;
}

esp_err_t uart_link_lic_get_jwt(const char *lic, char *out_jwt, size_t out_sz)
{
	if (!s_ready || !s_rxq) return ESP_ERR_INVALID_STATE;
    frame_t resp;
    esp_err_t er = link_req_resp(FRAME_LIC_GET_JWT,
                            (const uint8_t*)lic, (uint16_t)strlen(lic),
                            &resp,
                            pdMS_TO_TICKS(1000));
    if (er != ESP_OK) return er;
    if (resp.len < 1 || resp.payload[0] == 0) return ESP_FAIL;
    size_t jn = resp.len - 1;
    if (jn + 1 > out_sz) jn = out_sz - 1;
    memcpy(out_jwt, &resp.payload[1], jn);
    out_jwt[jn] = 0;
    return ESP_OK;
}

// ------- 교체: uart_link_get_index 전체 -------

esp_err_t uart_link_get_index(media_index_t *out)
{
    if (!out) return ESP_ERR_INVALID_ARG;
	if (!s_ready || !s_rxq) return ESP_ERR_INVALID_STATE;
    frame_t resp;
    esp_err_t er = link_req_resp(FRAME_MEDIA_INDEX_REQ, NULL, 0,
                                 &resp, pdMS_TO_TICKS(1000));
    if (er != ESP_OK) return er;

    const char* js     = (const char*)resp.payload;
    size_t      js_len = resp.len;
    const char* js_end = js + js_len;

    static media_item_t items[USB_ADVERT_MAX_FILES];
    uint32_t count = 0;

    // 1) files 배열 위치 찾기  ( "files": [ ... ] )
    const char key_files[] = "\"files\"";
    const char* kf = (const char*)memmem_simple(js, js_len, key_files, sizeof(key_files)-1);
    if (!kf){
        // files가 없으면 빈 인덱스 + gen=1 로 처리
        out->gen = 1;
        out->count = 0;
        out->items = NULL;
    } else {
        size_t remain = (size_t)(js_end - kf);
        const char* colon = (const char*)memchr(kf, ':', remain);
        if (!colon) {
            ESP_LOGW(TAG, "media index: 'files' has no ':'");
            out->gen = 1; out->count = 0; out->items = NULL;
        } else {
            const char* p = skip_ws_b(colon+1, js_end);
            if (p >= js_end || *p != '['){
                ESP_LOGW(TAG, "media index: files is not array");
                out->gen = 1; out->count = 0; out->items = NULL;
            } else {
                p++; // after '['
                while (p < js_end && count < USB_ADVERT_MAX_FILES){
                    p = skip_ws_b(p, js_end);
                    if (p >= js_end) break;
                    if (*p == ']'){ p++; break; }
                    if (*p != '{'){
                        // 다음 요소 전까지 스킵
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

                        // "id"
                        if ((js_end - p) >= 4 && strncmp(p, "\"id\"", 4) == 0){
                            const char* c = (const char*)memchr(p, ':', (size_t)(js_end - p));
                            if (!c) break;
                            c = skip_ws_b(c+1, js_end);
                            if (c < js_end && *c=='\"'){
                                c++;
                                char* dst = items[count].id;
                                while (c < js_end && *c!='\"' && (dst - items[count].id) < (ptrdiff_t)sizeof(items[count].id)-1){
                                    *dst++ = *c++;
                                }
                                *dst = 0;
                                if (c < js_end && *c=='\"') c++;
                                p = c;
                            } else {
                                p = c;
                            }
                        }
                        // "name"
                        else if ((js_end - p) >= 6 && strncmp(p, "\"name\"", 6) == 0){
                            const char* c = (const char*)memchr(p, ':', (size_t)(js_end - p));
                            if (!c) break;
                            c = skip_ws_b(c+1, js_end);
                            if (c < js_end && *c=='\"'){
                                c++;
                                char* dst = items[count].name;
                                while (c < js_end && *c!='\"' && (dst - items[count].name) < (ptrdiff_t)sizeof(items[count].name)-1){
                                    *dst++ = *c++;
                                }
                                *dst = 0;
                                if (c < js_end && *c=='\"') c++;
                                p = c;
                            } else {
                                p = c;
                            }
                        }
                        // "size"
                        else if ((js_end - p) >= 6 && strncmp(p, "\"size\"", 6) == 0){
                            const char* c = (const char*)memchr(p, ':', (size_t)(js_end - p));
                            if (!c) break;
                            c = skip_ws_b(c+1, js_end);
                            items[count].size = (uint32_t)parse_uint_b(c, js_end);
                            while (c < js_end && *c >= '0' && *c <= '9') c++; // 숫자 끝까지
                            p = c;
                        }
                        else {
                            // 모르는 키: 다음 ',' 또는 '}' 까지 스킵
                            const char* next = p;
                            while (next < js_end && *next != ',' && *next != '}') next++;
                            p = next;
                        }

                        p = skip_ws_b(p, js_end);
                        if (p < js_end && *p == ',') p++;
                    }
                    if (p < js_end && *p == '}') p++;
                    items[count].index = count;
                    count++;

                    // 다음 요소로 (',' 또는 ']' 까지 스킵)
                    p = skip_ws_b(p, js_end);
                    if (p < js_end && *p == ','){ p++; continue; }
                    if (p < js_end && *p == ']'){ p++; break; }
                }
                out->count = count;
            }
        }
    }

    // 2) gen 추출 (없으면 1)
    uint32_t gen = 1;
    const char key_gen[] = "\"gen\"";
    const char* pg = (const char*)memmem_simple(js, js_len, key_gen, sizeof(key_gen)-1);
    if (pg){
        size_t remain = (size_t)(js_end - pg);
        const char* colon = (const char*)memchr(pg, ':', remain);
        if (colon){
            const char* c = skip_ws_b(colon+1, js_end);
            gen = (uint32_t)parse_uint_b(c, js_end);
        }
    }

    out->gen = gen;

    // 3) items 옮기기
    if (out->count == 0){
        out->items = NULL;
    } else {
        out->items = (media_item_t*)malloc(sizeof(media_item_t) * out->count);
        if (!out->items){
            ESP_LOGE(TAG, "media index: oom");
            out->count = 0;
            return ESP_ERR_NO_MEM;
        }
        memcpy(out->items, items, sizeof(media_item_t) * out->count);
    }

    return ESP_OK;
}


esp_err_t uart_link_read_chunk(const char *id, uint64_t off, uint32_t len, uint8_t *out, uint32_t *out_len, uint32_t *out_crc){
	if(!id || !out || !out_len || !out_crc) return ESP_ERR_INVALID_ARG;
	if(!s_ready || !s_rxq) return ESP_ERR_INVALID_STATE;	
	uint8_t pl[1 + 64 + 8 + 4];
	size_t p = 0;
	size_t idlen = strlen(id);
	memcpy(&pl[p], id, idlen + 1);
	p += idlen + 1;
	
	// off
	pl[p++] = (uint8_t)(off >> 56);
	pl[p++] = (uint8_t)(off >> 48);
	pl[p++] = (uint8_t)(off >> 40);
	pl[p++] = (uint8_t)(off >> 32);
	pl[p++] = (uint8_t)(off >> 24);
	pl[p++] = (uint8_t)(off >> 16);
	pl[p++] = (uint8_t)(off >> 8);
	pl[p++] = (uint8_t)(off);
	
	// len
	pl[p++] = (uint8_t)(len >> 24);
	pl[p++] = (uint8_t)(len >> 16);
	pl[p++] = (uint8_t)(len >> 8);
	pl[p++] = (uint8_t)(len);

	
	uint8_t fbuf[FRAME_MAX_WIRE];
	size_t flen = 0;
	if(frame_build(FRAME_MEDIA_PULL_REQ, pl, (uint16_t)p, fbuf, sizeof(fbuf), &flen)!= FRAME_OK) return ESP_FAIL;
	uart_write_bytes(LINK_UART_PORT, (const char*)fbuf, flen);
	
	frame_t* rx = NULL;
	if(xQueueReceive(s_rxq, &rx, pdMS_TO_TICKS(1000))!= pdTRUE) return ESP_ERR_TIMEOUT;
	if(rx->type != FRAME_MEDIA_CHUNK){
		free(rx);
		return ESP_FAIL;
	}
	
	if(rx->len > len) rx->len = len;
	memcpy(out, rx->payload, rx->len);
	*out_len = rx->len;
	*out_crc = 0;
	
	free(rx);
	return ESP_OK;
}

esp_err_t uart_link_auth_req(const char* ssaid, auth_ssaid_resp_t* out)
{
    // 지금 STM32에는 ssaid용 프레임이 없으니까 일단 NOT SUPPORTED
    return ESP_ERR_NOT_SUPPORTED;
}

bool uart_link_usb_attached(void) { return true; }