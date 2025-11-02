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

#include <string.h>
#include <stdlib.h>

#define UART_PORT		UART_NUM_1
#define UART_BAUD		115200
#define UART_TX_PIN	17
#define UART_RX_PIN	16

static const char* TAG = "uart_link";

static uart_event_cb_t s_cb;
static bool s_attached = true;
static media_index_t s_idx;

static QueueHandle_t s_rxq;
static frame_parser_t s_fp;

static void uart_rx_task(void* arg){
	uint8_t buf[256];
	frame_t f;
	size_t	consumed = 0;
	
	while(1){
		int n = uart_read_bytes(UART_PORT, buf, sizeof(buf), pdMS_TO_TICKS(50));
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
			}
		}
	}
}

static esp_err_t link_rpc(uint8_t type, const uint8_t* payload, uint16_t plen, frame_t* out, TickType_t to){
	uint8_t fbuf[FRAME_MAX_WIRE];
	size_t flen = 0;
	frame_err_t fer = frame_build(type, payload, plen, fbuf, sizeof(fbuf), &flen);
	if(fer != FRAME_OK){
		ESP_LOGE(TAG, "frame build fail %d", fer);
		return ESP_FAIL;
	}
	
	int wr = uart_write_bytes(UART_PORT, fbuf, flen);
	if(wr < 0){
		ESP_LOGE(TAG, "uart write fail");
		return ESP_FAIL;
	}
	
	frame_t* rx = NULL;
	if(xQueueReceive(s_rxq, &rx, to) != pdTRUE){
		ESP_LOGE(TAG, "rpc timeout");
		return ESP_FAIL;
	}
	
	if(rx->type != FRAME_LIC_RESP){
		ESP_LOGW(TAG, "unexpected resp type=0x%02X", rx->type);
	}
	if(out)
		*out = *rx;
		
	free(rx);
	return ESP_OK;
}

esp_err_t uart_link_init(void){
	const uart_config_t cfg = {
		.baud_rate = UART_BAUD,
        .data_bits = UART_DATA_8_BITS,
        .parity    = UART_PARITY_DISABLE,
        .stop_bits = UART_STOP_BITS_1,
        .flow_ctrl = UART_HW_FLOWCTRL_DISABLE,
	};
	
	ESP_ERROR_CHECK(uart_driver_install(UART_PORT, 2048, 0, 0, NULL, 0));
	ESP_ERROR_CHECK(uart_param_config(UART_PORT, &cfg));
    ESP_ERROR_CHECK(uart_set_pin(UART_PORT, UART_TX_PIN, UART_RX_PIN,UART_PIN_NO_CHANGE, UART_PIN_NO_CHANGE));
	
	s_rxq = xQueueCreate(5, sizeof(void*));
	xTaskCreate(uart_rx_task, "uart_rx", 4096, NULL, 5, NULL);
	
	return ESP_OK;
}