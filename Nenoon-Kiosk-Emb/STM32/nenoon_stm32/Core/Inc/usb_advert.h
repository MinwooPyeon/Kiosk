/*
 * advUSB.h
 *
 *  Created on: Oct 30, 2025
 *      Author: SSAFY
 */

#ifndef INC_USB_ADVERT_H_
#define INC_USB_ADVERT_H_

#include "fatfs.h"
#include <stdbool.h>

#ifdef __cplusplus
extern "C"{
#endif

typedef enum{
    USB_ADVERT_OK = 0,              /* 성공 */

    USB_ADVERT_ERR_NOT_MOUNTED,     /* f_mount 실패 */
    USB_ADVERT_ERR_OPENDIR,         /* f_opendir 실패 */
    USB_ADVERT_ERR_NO_FILES,        /* 조건에 맞는 파일 없음 */
    USB_ADVERT_ERR_FOPEN,           /* f_open 실패 */
    USB_ADVERT_ERR_FREAD,           /* f_read 실패 */
    USB_ADVERT_ERR_PARAM,           /* 잘못된 인자 */
    USB_ADVERT_ERR_IDX_RANGE,       /* 인덱스 범위 초과 */
    USB_ADVERT_ERR_IO,              /* 그 외 I/O 에러 */
} usb_advert_err_t;

void USB_Advert_Init(void);
usb_advert_err_t USB_Advert_Scan(void);
usb_advert_err_t USB_Advert_ReadByName(const char* filename);
usb_advert_err_t USB_Advert_ReadByIndex(uint32_t index);
uint32_t USB_Advert_GetFileCount(void);

const char* USB_Advert_GetName(uint32_t index);
const char* usb_advert_errstr(usb_advert_err_t err);
#ifdef __cplusplus
}
#endif

#endif /* INC_USB_ADVERT_H_ */
