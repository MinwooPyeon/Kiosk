/*
 * task_usb.h
 *
 *  Created on: Nov 5, 2025
 *      Author: SSAFY
 */


#ifndef INC_TASK_USB_H_
#define INC_TASK_USB_H_

#include "cmsis_os2.h"


void task_usb_start(uint32_t stack, osPriority_t prio);
