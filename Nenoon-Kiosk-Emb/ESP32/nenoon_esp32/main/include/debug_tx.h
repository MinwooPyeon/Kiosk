/*
 * debug_tx.h
 *
 *  Created on: 2025. 11. 11.
 *      Author: SSAFY
 */

#ifndef MAIN_INCLUDE_DEBUG_TX_H_
#define MAIN_INCLUDE_DEBUG_TX_H_

#include <stddef.h>
#include <stdint.h>
#include "esp_err.h"

/* 원시 바이트 전송 (프레이밍 없음) */
esp_err_t debug_tx_send_raw(const void* data, size_t len);

/* 테스트 프레임 전송 (type=0xF0, payload="PING") */
esp_err_t debug_tx_send_ping(void);

/* 임의 텍스트를 payload로 하는 테스트 프레임 전송 (type=0xF1) */
esp_err_t debug_tx_send_text(const char* text);

/* HEX 문자열("FF00A1…")을 파싱해 원시 바이트 전송 */
esp_err_t debug_tx_send_hex(const char* hex);

/* 간단한 버스트: 매 interval_ms 간격으로 cnt번 PING 프레임 전송 */
esp_err_t debug_tx_burst(size_t cnt, uint32_t interval_ms);

#endif /* MAIN_INCLUDE_DEBUG_TX_H_ */
