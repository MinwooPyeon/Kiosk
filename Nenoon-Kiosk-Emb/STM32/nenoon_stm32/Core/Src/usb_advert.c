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

	STLINK_UART_Println(TAG "Init");
}
usb_advert_err_t USB_Advert_Scan(void){
	FRESULT res;
	DIR		dir;
	FILINFO	fno;

	STLINK_UART_Println(TAG "Scan Start");

	res = f_mount(&USBH_fatfs, (TCHAR const*)USBHPath, 0);
	if(res != FR_OK){
		return USB_ADVERT_ERR_NOT_MOUNTED;
	}

	res = f_opendir(&dir, "/");
	if(res != FR_OK){
		return USB_ADVERT_ERR_OPENDIR;
	}

	s_count = 0;
	while(1){
		res = f_readdir(&dir, &fno)
		if(res != FR_OK){
			f_closedir(&dir);
			return USB_ADVERT_ERR_IO;
		}
		if(fno.fname[0] == 0) break;

		if(!(frno.fattrib & AM_DIR)){
			if(strstr(fno.name, ".jpg") || strstr(fno.name, ".png")){
				strncpy(s_files[s_count], fno.fname, USB_ADVERT_MAX_NAME-1);
				s_files[s_count][USB_ADVERT_MAX_NAME-1]='\0';
				s_count++;
				if(s_count >= USB_ADVERT_MAX_FILES) break;
			}
		}
	}
	f_closedir(&dir);

	char msg[64];
	snprintf(msg, sizeof(msg), TAG " found %lu files", (unsigned long)s_count);
	STLINK_UART_PrintLn(msg);


	s_scanned = true;
	if(s_count == 0) return USB_ADVERT_ERR_NO_FILES;
	return USB_ADVERT_OK;
}
usb_advert_err_t USB_Advert_ReadByName(const char* filename);
usb_advert_err_t USB_Advert_ReadByIndex(uint32_t index);
uint32_t USB_Advert_GetFileCount(void);

const char* USB_Advert_GetName(uint32_t index);
const char* usb_advert_errstr(usb_advert_err_t err);
