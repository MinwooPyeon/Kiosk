/*
 * usb_advert.c
 *
 *  Created on: Oct 30, 2025
 *      Author: SSAFY
 */


#include "usb_advert.h"
#include "usart.h"
#include <string.h>
#include <stdio.h>


#define TAG 					"[USB_ADVERT]"
#define USB_ADVERT_MAX_FILES   	16
#define USB_ADVERT_MAX_NAME    	64

static char     s_files[USB_ADVERT_MAX_FILES][USB_ADVERT_MAX_NAME];
static uint32_t s_count = 0;
static bool     s_scanned = false;

extern FATFS	USBH_fatfs;
extern char		USBHPath[4];

const char* usb_advert_strerr(usb_advert_err_t err)
{
    switch(err){
    case USB_ADVERT_OK:             return "OK";
    case USB_ADVERT_ERR_NOT_MOUNTED:return "Mount failed";
    case USB_ADVERT_ERR_OPENDIR:    return "Open dir failed";
    case USB_ADVERT_ERR_NO_FILES:   return "No matched files";
    case USB_ADVERT_ERR_FOPEN:      return "Open file failed";
    case USB_ADVERT_ERR_FREAD:      return "Read file failed";
    case USB_ADVERT_ERR_PARAM:      return "Bad parameter";
    case USB_ADVERT_ERR_IDX_RANGE:  return "Index out of range";
    case USB_ADVERT_ERR_IO:         return "I/O error";
    default:                        return "Unknown";
    }
}

void USB_Advert_Init(void){
	s_count = 0;
	s_scanned = false;
}
usb_advert_err_t USB_Advert_Scan(void);
usb_advert_err_t USB_Advert_ReadByName(const char* filename);
usb_advert_err_t USB_Advert_ReadByIndex(uint32_t index);
uint32_t USB_Advert_GetFileCount(void);

const char* USB_Advert_GetName(uint32_t index);
const char* usb_advert_errstr(usb_advert_err_t err);
