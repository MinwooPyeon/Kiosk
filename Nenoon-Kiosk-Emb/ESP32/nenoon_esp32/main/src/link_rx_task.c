/*
 * link_rx_task.c
 *
 *  Created on: 2025. 11. 10.
 *      Author: SSAFY
 */

#include "link_io.h"
#include "frame_reader.h"
#include "driver/uart.h"
#include "esp_log.h"

#define RX_TASK_STACK 8192
#define RX_TASK_PRIO  (tskIDLE_PRIORITY + 5)

static const char* TAG = "link_rx";
static bool s_sniff = false;
void uart_link_set_sniff(bool on){ s_sniff = on; } // 외부에서 사용

/* frame_reader emit → 큐에 소유권 전송 */
static void emit_cb(const uint8_t* frame, size_t len, void* user)
{
    if(len < FRAME_HDR_SIZE + FRAME_TLR_SIZE) return;
    uint8_t type = frame[3];
    uint16_t plen = (uint16_t)((frame[4] << 8) | frame[5]);
    ESP_LOGI("link_rx","EMIT type=0x%02X plen=%u total=%u", type, (unsigned)plen, (unsigned)len);
    const uint8_t* payload = plen ? &frame[6] : NULL;
    (void)user;
    (void)linkio_enqueue_owned(type, payload, plen);
}

static void rx_task(void* arg)
{
    frame_reader_t rd;
    frame_reader_init(&rd, emit_cb, NULL);

    uint8_t buf[256];
    TickType_t last = xTaskGetTickCount();

    for(;;){
        int n = uart_read_bytes((int)(intptr_t)arg, buf, sizeof(buf), pdMS_TO_TICKS(50));
        if(n > 0){
            if(s_sniff){
                for(int i=0;i<n;i++) ESP_LOGI(TAG, "RX %02X", buf[i]);
            }
            frame_reader_feed(&rd, buf, (size_t)n);
        }
        if(xTaskGetTickCount() - last >= pdMS_TO_TICKS(10000)){
            UBaseType_t wm = uxTaskGetStackHighWaterMark(NULL);
            ESP_LOGI(TAG, "rx stack watermark=%u words", (unsigned)wm);
            last = xTaskGetTickCount();
        }
        taskYIELD();
    }
}

/* 태스크 시작 헬퍼 */
esp_err_t link_rx_task_start(int uart_num)
{
    if(xTaskCreate(rx_task, "link_rx", RX_TASK_STACK, (void*)(intptr_t)uart_num,
                   RX_TASK_PRIO, NULL) != pdPASS){
        return ESP_ERR_NO_MEM;
    }
    return ESP_OK;
}
