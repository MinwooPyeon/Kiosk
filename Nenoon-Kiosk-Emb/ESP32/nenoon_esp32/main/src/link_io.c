/*
 * link_io.c
 *
 *  Created on: 2025. 11. 10.
 *      Author: SSAFY
 */

#include "link_io.h"
#include "driver/uart.h"
#include "esp_log.h"
#include "frame.h"          // FRAME_MAX_WIRE, FRAME_HDR_SIZE, FRAME_TLR_SIZE, frame_build()
#include <string.h>
#include <stdlib.h>

#define RX_QUEUE_LEN 4
#define RX_BUF_BYTES 2048

static const char* TAG = "link_io";
static QueueHandle_t s_rxq;
static int s_uart_num = -1;

/* frame_t.payload가 배열(포인터 X)이므로, 별도 연결 없이 frame_t 자체에 복사 */
typedef struct {
    frame_t f;  // payload는 배열 멤버
} rxblk_t;

static inline uint16_t clamp_plen(uint16_t plen){
    const uint16_t max_payload = (uint16_t)(FRAME_MAX_WIRE - FRAME_HDR_SIZE - FRAME_TLR_SIZE);
    return (plen > max_payload) ? max_payload : plen;
}

QueueHandle_t linkio_get_rx_queue(void){ return s_rxq; }

esp_err_t linkio_init_uart(int uart_num, int tx_pin, int rx_pin, int baud)
{
    if(s_rxq) return ESP_OK;

    const uart_config_t cfg = {
        .baud_rate  = baud,
        .data_bits  = UART_DATA_8_BITS,
        .parity     = UART_PARITY_DISABLE,
        .stop_bits  = UART_STOP_BITS_1,
        .flow_ctrl  = UART_HW_FLOWCTRL_DISABLE,
        .source_clk = UART_SCLK_APB,
    };
    ESP_ERROR_CHECK(uart_param_config(uart_num, &cfg));
    ESP_ERROR_CHECK(uart_set_pin(uart_num, tx_pin, rx_pin, UART_PIN_NO_CHANGE, UART_PIN_NO_CHANGE));
    ESP_ERROR_CHECK(uart_driver_install(uart_num, RX_BUF_BYTES, 0, 0, NULL, 0));
    ESP_ERROR_CHECK(uart_flush_input(uart_num));
    ESP_ERROR_CHECK(uart_set_rx_timeout(uart_num, 2));

    s_rxq = xQueueCreate(RX_QUEUE_LEN, sizeof(frame_t*));
    if(!s_rxq){ ESP_LOGE(TAG, "queue create fail"); return ESP_ERR_NO_MEM; }

    s_uart_num = uart_num;
    return ESP_OK;
}

void linkio_deinit_uart(void)
{
    if(s_rxq){ vQueueDelete(s_rxq); s_rxq = NULL; }
    if(s_uart_num >= 0){ uart_driver_delete(s_uart_num); s_uart_num = -1; }
}

/* 🚩 핵심 수정: payload에 포인터를 ‘대입’하지 않고, frame_t 배열에 ‘복사’ */
bool linkio_enqueue_owned(uint8_t type, const uint8_t* payload, uint16_t plen)
{
    rxblk_t* b = (rxblk_t*)malloc(sizeof(rxblk_t));
    if(!b){ ESP_LOGE(TAG, "rx: OOM %u", (unsigned)sizeof(rxblk_t)); return false; }

    b->f.type = type;
    b->f.len  = clamp_plen(plen);
    if(b->f.len && payload){
        memcpy(b->f.payload, payload, b->f.len);
    }

    frame_t* pf = &b->f;  // 큐에는 frame_t* 저장
    if(xQueueSend(s_rxq, &pf, 0) != pdTRUE){
        free(b);
        return false;
    }
    return true;
}

esp_err_t linkio_send_frame(uint8_t type, const uint8_t* payload, uint16_t plen)
{
    if (s_uart_num < 0) return ESP_ERR_INVALID_STATE;

    esp_err_t ret = ESP_FAIL;
    size_t flen = 0;
    uint8_t *fbuf = malloc(FRAME_MAX_WIRE);
    if (!fbuf) { ESP_LOGE(TAG, "oom"); return ESP_ERR_NO_MEM; }

    frame_err_t fer = frame_build(type, payload, plen, fbuf, FRAME_MAX_WIRE, &flen);
    if (fer == FRAME_OK) {
        int wr = uart_write_bytes(s_uart_num, (const char*)fbuf, flen);
        ret = (wr < 0) ? ESP_FAIL : ESP_OK;
    } else {
        ESP_LOGE(TAG, "frame build fail %d", fer);
        ret = ESP_FAIL;
    }
    free(fbuf);
    return ret;
}


/* 🚩 핵심 수정: 딥카피 불필요. frame_t(배열 포함) 통째로 복사 후 원본 블록만 free */
esp_err_t linkio_recv_frame(frame_t* out, TickType_t to)
{
    if(!out) return ESP_ERR_INVALID_ARG;
    frame_t* rx = NULL;
    if(xQueueReceive(s_rxq, &rx, to) != pdTRUE){
        ESP_LOGE("link_io","recv_frame timeout after %u ms", (unsigned)(to*portTICK_PERIOD_MS)); // ★
        return ESP_ERR_TIMEOUT;
    }
    if(!rx) return ESP_FAIL;
    *out = *rx;
    free((void*)((uintptr_t)rx - offsetof(rxblk_t, f)));
    return ESP_OK;
}

