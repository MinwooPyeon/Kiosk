/*
 * uart_link.c
 *
 *  Created on: Nov 2, 2025
 *      Author: SSAFY
 */

#include "uart_link.h"
#include "usart.h"
#include "frame_parser.h"
#include "frame_dispatcher.h"

#define UART6_RX_BUF_SZ		256

static uint8_t				s_uart6_rx_buf[UART6_RX_BUF_SZ];
static frame_parser_t		s_fp;


static void on_frame_rx(const uint8_t* frame, size_t len){
	proto_dispatch_handle(frame, len);
}
void uart_link_init(void){
	frame_parser_init(&s_fp, on_frame_rx);

	HAL_UARTEx_ReceiveToIdle_DMA(&huart6, s_uart6_rx_buf, sizeof(s_uart6_rx_buf));
}
void HAL_UARTEx_RxEventCallback(UART_HandleTypeDef *huart, uint16_t size){
	if(huart->Instance == USART6){
		frame_parser_feed(&s_fp, s_uart6_rx_buf, size);
		HAL_UARTEx_ReceiveToIdle_DMA(&huart6, s_uart6_rx_buf, sizeof(s_uart6_rx_buf));
	}
}
