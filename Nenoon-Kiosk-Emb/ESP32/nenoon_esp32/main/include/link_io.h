/*
 * link_io.h
 *
 *  Created on: 2025. 11. 10.
 *      Author: SSAFY
 */

#ifndef MAIN_INCLUDE_LINK_IO_H_
#define MAIN_INCLUDE_LINK_IO_H_

#include "esp_err.h"
#include "freertos/FreeRTOS.h"
#include "freertos/queue.h"
#include "uart_link.h"
#include "frame.h" 

#ifdef __cplusplus
extern "C" {
#endif

/* 전역 상태 접근자 */
QueueHandle_t linkio_get_rx_queue(void);

/* 큐/IO 초기화·해제 */
esp_err_t linkio_init_uart(int uart_num, int tx_pin, int rx_pin, int baud);
void      linkio_deinit_uart(void);

/* 프레임 소유권 전송(한 블록 malloc: frame_t + payload) */
bool linkio_enqueue_owned(uint8_t type, const uint8_t* payload, uint16_t plen);

/* 프레임 전송/수신 (수신은 딥카피 후 호출자 소유) */
esp_err_t linkio_send_frame(uint8_t type, const uint8_t* payload, uint16_t plen);
esp_err_t linkio_recv_frame(frame_t* out, TickType_t to);

/* 요청-응답 헬퍼 */
static inline esp_err_t linkio_req_resp(uint8_t type, const uint8_t* pl, uint16_t plen,
                                        frame_t* resp, TickType_t to){
    esp_err_t er = linkio_send_frame(type, pl, plen);
    if(er != ESP_OK) return er;
    return linkio_recv_frame(resp, to);
}

#ifdef __cplusplus
}
#endif

#endif /* MAIN_INCLUDE_LINK_IO_H_ */
