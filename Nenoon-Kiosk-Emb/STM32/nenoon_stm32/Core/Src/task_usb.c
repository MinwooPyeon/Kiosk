/*
 * task_usb.c
 *
 *  Created on: Nov 5, 2025
 *      Author: SSAFY
 */


#include "task_usb.h"
#include "usb_host.h"
#include "usart.h"

static void vTaskUsb(void* arg){
	(void)arg;
	STLINK_UART_Println("[task usb] start");

	for(;;){
		MX_USB_HOST_Process();
		osDelay(5);
	}
}

void task_usb_start(uint32_t stack, osPriority_t prio){
	const osThreadAttr_t attr = {
		.name = "usb_host",
		.stack_size = stack,
		.priority = prio
	};

	osThreadNew(vTaskUsb, NULL, &attr);
}
