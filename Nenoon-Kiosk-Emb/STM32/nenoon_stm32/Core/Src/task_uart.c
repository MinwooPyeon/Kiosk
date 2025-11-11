/*
 * task_uart.c
 *
 *  Created on: Nov 4, 2025
 *      Author: SSAFY
 */


#include "task_uart.h"
#include "usart.h"
#include "frame_parser.h"
#include "frame_dispatcher.h"
#include "uart_link.h"

#include "FreeRTOS.h"
#include "stream_buffer.h"
#include "task.h"
#include "semphr.h"

#define UART6_RX_SB_SIZE	1024u
#define UART6_RX_TRIG		1u

static StreamBufferHandle_t s_uart6_rx_sb;
static uint8_t				s_uart6_rx_dma[512];
static frame_parser_t		s_fp;
static TaskHandle_t         s_uart_rx_th;

static void on_frame_rx(const uint8_t* frame, size_t len){
	proto_dispatch_handle(frame, len);
}

static void vTaskUartRx(void * arg){
	(void)arg;
	STLINK_UART_Println("[task uart] start");
	frame_parser_init(&s_fp, on_frame_rx);\

	__HAL_UART_CLEAR_IDLEFLAG(&huart6);
	__HAL_UART_ENABLE_IT(&huart6, UART_IT_IDLE);
	HAL_UARTEx_ReceiveToIdle_DMA(&huart6, s_uart6_rx_dma, sizeof(s_uart6_rx_dma));
	uint8_t chunk[64];
	for(;;){
		size_t n = xStreamBufferReceive(s_uart6_rx_sb, chunk, sizeof(chunk),portMAX_DELAY);
		if(n){
			STLINK_UART_Println("[task uart] frame parser feed");
			frame_parser_feed(&s_fp, chunk, n);
		}
		(void)ulTaskNotifyTake(pdTRUE, 0);
		HAL_UARTEx_ReceiveToIdle_DMA(&huart6, s_uart6_rx_dma, sizeof(s_uart6_rx_dma));
	}
}

void task_uart_start(uint32_t stack, osPriority_t prio){
	s_uart6_rx_sb = xStreamBufferCreate(UART6_RX_SB_SIZE, UART6_RX_TRIG);

	const osThreadAttr_t attr ={
			.name = "uart_rx",
			.stack_size = stack,
			.priority = prio
	};
	osThreadNew(vTaskUartRx, NULL, &attr);
}

volatile uint32_t g_uart6_isr_cnt = 0;
void HAL_UARTEx_RxEventCallback(UART_HandleTypeDef* huart, uint16_t size){
	if(huart->Instance == USART6){
		STLINK_UART_Println("[uart6] Rx Event Callback");
		BaseType_t xHigher = pdFALSE;
		(void)xStreamBufferSendFromISR(s_uart6_rx_sb, s_uart6_rx_dma, size, &xHigher);
		vTaskNotifyGiveFromISR(s_uart_rx_th, &xHigher);
		portYIELD_FROM_ISR(xHigher);
	}
}
