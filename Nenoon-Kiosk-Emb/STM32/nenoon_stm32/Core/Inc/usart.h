/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file    usart.h
  * @brief   This file contains all the function prototypes for
  *          the usart.c file
  ******************************************************************************
  * @attention
  *
  * Copyright (c) 2025 STMicroelectronics.
  * All rights reserved.
  *
  * This software is licensed under terms that can be found in the LICENSE file
  * in the root directory of this software component.
  * If no LICENSE file comes with this software, it is provided AS-IS.
  *
  ******************************************************************************
  */
/* USER CODE END Header */
/* Define to prevent recursive inclusion -------------------------------------*/
#ifndef __USART_H__
#define __USART_H__

#ifdef __cplusplus
extern "C" {
#endif

/* Includes ------------------------------------------------------------------*/
#include "main.h"
#include "stm32f4xx_hal.h"
/* USER CODE BEGIN Includes */

/* USER CODE END Includes */

extern UART_HandleTypeDef huart2;

extern UART_HandleTypeDef huart3;

extern UART_HandleTypeDef huart6;

/* USER CODE BEGIN Private defines */

/* USER CODE END Private defines */

void MX_USART2_UART_Init(void);
void MX_USART3_UART_Init(void);
void MX_USART6_UART_Init(void);

/* USER CODE BEGIN Prototypes */
//UART2 - USB Converter
HAL_StatusTypeDef UART2_SendString(const char* s);
HAL_StatusTypeDef UART2_SendBytes(const uint8_t* buf, uint16_t len);
HAL_StatusTypeDef UART2_RecvBytes(const uint8_t* buf, uint16_t len, uint32_t to_ms);

//UART3 - ST-LINK


//UART6 - ESP32
HAL_StatusTypeDef UART6_SendString(const char* s);
HAL_StatusTypeDef UART6_SendBytes(const uint8_t* buf, uint16_t len);
HAL_StatusTypeDef UART6_RecvBytes(const uint8_t* buf, uint16_t len, uint32_t to_ms);
/* USER CODE END Prototypes */

#ifdef __cplusplus
}
#endif

#endif /* __USART_H__ */

