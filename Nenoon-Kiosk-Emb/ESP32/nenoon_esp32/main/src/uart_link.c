/*
 * uart_link.c  (refactored + hardened)
 *
 *  Created on: 2025. 10. 26.
 *  Author: SSAFY
 */

/*
 * uart_link.c  (public API facade)
 */
#include "uart_link.h"
#include "link_io.h"
#include "media_json.h"
#include "driver/uart.h"
#include "esp_log.h"
#include "frame.h"
#include <string.h>
#include <stdlib.h>
#include <inttypes.h>

#define LINK_UART_PORT    UART_NUM_1
#define LINK_UART_BAUD    115200
#define LINK_UART_TX_PIN  17
#define LINK_UART_RX_PIN  16

static const char* TAG = "uart_link";
extern esp_err_t link_rx_task_start(int uart_num);

static volatile bool s_ready = false;

esp_err_t uart_link_init(void)
{
    ESP_ERROR_CHECK(linkio_init_uart(LINK_UART_PORT, LINK_UART_TX_PIN, LINK_UART_RX_PIN, LINK_UART_BAUD));
    ESP_ERROR_CHECK(link_rx_task_start(LINK_UART_PORT));
    s_ready = true;
    ESP_LOGI(TAG, "uart_link ready (U%d TX=%d RX=%d, %d baud)", LINK_UART_PORT, LINK_UART_TX_PIN, LINK_UART_RX_PIN, LINK_UART_BAUD);
    return ESP_OK;
}

bool uart_link_usb_attached(void) { return true; }

esp_err_t uart_link_lic_mgr_login(const char *id, const char *pw, bool *ok)
{
    if (!ok) return ESP_ERR_INVALID_ARG;
    *ok = false;
    if (!s_ready || !id || !pw) return ESP_ERR_INVALID_STATE;

    char buf[128];
    int n = snprintf(buf, sizeof(buf), "%s:%s", id, pw);
    if (n < 0) return ESP_FAIL;
    if ((size_t)n >= sizeof(buf)) return ESP_ERR_NO_MEM;

    frame_t resp = {0};
    esp_err_t er = linkio_req_resp(FRAME_LIC_MGR_LOGIN,
                                   (const uint8_t*)buf, (uint16_t)n,
                                   &resp, pdMS_TO_TICKS(3000));
    if (er != ESP_OK) return er;

    *ok = (resp.len >= 1 && resp.payload[0] == 1);
    return ESP_OK;
}

esp_err_t uart_link_lic_issue(const char *app, const char *to, char *out_lic, size_t out_sz)
{
    if (!s_ready || !app || !to || !out_lic || out_sz==0) return ESP_ERR_INVALID_STATE;

    char buf[128];
    int n = snprintf(buf, sizeof(buf), "%s:%s", app, to);
    if (n < 0) return ESP_FAIL;
    if ((size_t)n >= sizeof(buf)) return ESP_ERR_NO_MEM;

    frame_t resp = {0};
    esp_err_t er = linkio_req_resp(FRAME_LIC_ISSUE,
                                   (const uint8_t*)buf, (uint16_t)n,
                                   &resp, pdMS_TO_TICKS(3000));
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
    if (!ok) return ESP_ERR_INVALID_ARG;
    *ok = false;
    if (!s_ready || !lic) return ESP_ERR_INVALID_STATE;

    frame_t resp = {0};
    esp_err_t er = linkio_req_resp(FRAME_LIC_VALIDATE,
                                   (const uint8_t*)lic, (uint16_t)strlen(lic),
                                   &resp, pdMS_TO_TICKS(3000));
    if (er != ESP_OK) return er;

    *ok = (resp.len >= 1 && resp.payload[0] == 1);
    return ESP_OK;
}

esp_err_t uart_link_lic_get_jwt(const char *lic, char *out_jwt, size_t out_sz)
{
    if (!s_ready || !lic || !out_jwt || out_sz==0) return ESP_ERR_INVALID_STATE;

    frame_t resp = {0};
    esp_err_t er = linkio_req_resp(FRAME_LIC_GET_JWT,
                                   (const uint8_t*)lic, (uint16_t)strlen(lic),
                                   &resp, pdMS_TO_TICKS(3000));
    if (er != ESP_OK) return er;

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
    if (!s_ready) return ESP_ERR_INVALID_STATE;

    frame_t resp = {0};
    esp_err_t er = linkio_req_resp(FRAME_MEDIA_INDEX_REQ, NULL, 0,
                                   &resp, pdMS_TO_TICKS(3000));
    if (er != ESP_OK) return er;

    json_view_t jv = { .js = (const char*)resp.payload, .js_len = resp.len };
    out->gen = media_json_parse_gen_or(&jv, 1);
    media_json_parse_index(&jv, out);
    return ESP_OK;
}

esp_err_t uart_link_read_chunk(const char *id, uint64_t off, uint32_t len,
                               uint8_t *out, uint32_t *out_len, uint32_t *out_crc)
{
    if (!id || !out || !out_len || !out_crc) return ESP_ERR_INVALID_ARG;
    if (!s_ready) return ESP_ERR_INVALID_STATE;

    uint8_t pl[1 + 64 + 8 + 4];
    size_t  p = 0;

    size_t idlen = strlen(id);
    if (idlen + 1 > sizeof(pl)) return ESP_ERR_INVALID_ARG;

    memcpy(&pl[p], id, idlen + 1);  p += idlen + 1;      // zero-terminated
    pl[p++] = (uint8_t)(off >> 56); pl[p++] = (uint8_t)(off >> 48);
    pl[p++] = (uint8_t)(off >> 40); pl[p++] = (uint8_t)(off >> 32);
    pl[p++] = (uint8_t)(off >> 24); pl[p++] = (uint8_t)(off >> 16);
    pl[p++] = (uint8_t)(off >> 8 ); pl[p++] = (uint8_t)(off      );
    pl[p++] = (uint8_t)(len >> 24); pl[p++] = (uint8_t)(len >> 16);
    pl[p++] = (uint8_t)(len >> 8 ); pl[p++] = (uint8_t)(len      );

    frame_t resp = {0};
    esp_err_t er = linkio_req_resp(FRAME_MEDIA_PULL_REQ, pl, (uint16_t)p,
                                   &resp, pdMS_TO_TICKS(3000));
    if (er != ESP_OK) return er;

    if (resp.len > len) resp.len = len;
    if (resp.len) memcpy(out, resp.payload, resp.len);
    *out_len = resp.len;
    *out_crc = 0; // 필요 시 CRC 합의 후 사용
    return ESP_OK;
}
