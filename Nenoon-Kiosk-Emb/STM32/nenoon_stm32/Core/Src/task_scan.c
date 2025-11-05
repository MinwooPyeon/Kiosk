/*
 * task_scan.c
 *
 *  Created on: Nov 5, 2025
 *      Author: SSAFY
 */


#include "task_scan.h"
#include "usb_advert.h"
#include "ff.h"
#include "usart.h"

static void vTaskScan(void* arg){
	(void)arg;
	STLINK_UART_Println("[task scan] start");

	if(!USB_Advert_IsScanned())
		(void)USB_Advert_Scan();

	for(;;){
		if(!USB_Advert_IsScanned()){
			(void)USB_Advert_Scan();
		}
		osDelay(1000);
	}
}

void task_scan_start(uint32_t stack, osPriority_t prio){
	const osThreadAttr_t attr = {
			.name = "media_scan",
			.stack_size = stack,
			.priority = prio
	};

	osThreadNew(vTaskScan, NULL, &attr);
}
