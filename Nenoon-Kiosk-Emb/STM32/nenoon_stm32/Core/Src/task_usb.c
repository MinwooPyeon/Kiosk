/*
 * task_usb.c
 *
 *  Created on: Nov 5, 2025
 *      Author: SSAFY
 */


#include "task_usb.h"
#include "usb_host.h"
#include "usbh_core.h"
#include "usart.h"

extern USBH_HandleTypeDef hUsbHostFS;

static void vTaskUsb(void* arg){
	(void)arg;
	MX_USB_HOST_Init();
	STLINK_UART_Println("[task usb] start");

	for(;;){
		USBH_Process(&hUsbHostFS);
		osDelay(5000);
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
