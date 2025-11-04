/*
 * usb_advert.c
 *
 *  Created on: Oct 30, 2025
 *      Author: SSAFY
 *
 *  역할:
 *  1) USB MSC가 FatFs로 마운트된 후 루트 디렉터리를 스캔해서
 *     광고용 이미지(.jpg/.png) 목록을 RAM에 보관한다.
 *  2) 이름으로/인덱스로 읽어 ST-LINK 로그로 뿌리거나
 *     UART6(ESP32)로 chunk 단위로 내보낼 수 있다.
 */

#include "usb_advert.h"
#include "usart.h"
#include "frame.h"

#include "fatfs.h"
#include <string.h>
#include <stdio.h>
#include <stdbool.h>
#include <stdint.h>

#define TAG                     "[USB_ADVERT] "
#define USB_ADVERT_MAX_FILES    16
#define USB_ADVERT_MAX_NAME     64
#define USB_ADVERT_CHUNK_SIZE   1024


extern FATFS USBHFatFS;
extern char  USBHPath[4];

static char     s_files[USB_ADVERT_MAX_FILES][USB_ADVERT_MAX_NAME];
static usb_advert_meta_t s_metas[USB_ADVERT_MAX_FILES];
static uint32_t s_count   = 0;
static bool     s_scanned = false;


/* 에러 문자열                                               */
const char* USB_Advert_errstr(usb_advert_err_t err)
{
    switch (err) {
    case USB_ADVERT_OK:              return "OK";
    case USB_ADVERT_ERR_NOT_MOUNTED: return "Mount failed";
    case USB_ADVERT_ERR_OPENDIR:     return "Open dir failed";
    case USB_ADVERT_ERR_NO_FILES:    return "No matched files";
    case USB_ADVERT_ERR_FOPEN:       return "Open file failed";
    case USB_ADVERT_ERR_FREAD:       return "Read file failed";
    case USB_ADVERT_ERR_PARAM:       return "Bad parameter";
    case USB_ADVERT_ERR_IDX_RANGE:   return "Index out of range";
    case USB_ADVERT_ERR_IO:          return "I/O error";
    default:                         return "Unknown";
    }
}

void USB_Advert_Init(void)
{
    s_count   = 0;
    s_scanned = false;
    STLINK_UART_Println(TAG "init");
}

usb_advert_err_t USB_Advert_Scan(void)
{
    FRESULT res;
    DIR     dir;
    FILINFO fno;

    STLINK_UART_Println(TAG "scan start");

    res = f_mount(&USBHFatFS, (TCHAR const*)USBHPath, 0);
    if (res != FR_OK) {
        STLINK_UART_Println(TAG "mount failed");
        return USB_ADVERT_ERR_NOT_MOUNTED;
    }

    res = f_opendir(&dir, "/");
    if (res != FR_OK) {
        STLINK_UART_Println(TAG "opendir failed");
        return USB_ADVERT_ERR_OPENDIR;
    }

    s_count = 0;

    while (1) {
        res = f_readdir(&dir, &fno);
        if (res != FR_OK) {
            f_closedir(&dir);
            STLINK_UART_Println(TAG "readdir error");
            return USB_ADVERT_ERR_IO;
        }

        if (fno.fname[0] == 0)
            break;

        if (fno.fattrib & AM_DIR)
            continue;

        if (strstr(fno.fname, ".jpg") || strstr(fno.fname, ".JPG") ||
            strstr(fno.fname, ".png") || strstr(fno.fname, ".PNG"))
        {
            strncpy(s_files[s_count], fno.fname, USB_ADVERT_MAX_NAME - 1);
            s_files[s_count][USB_ADVERT_MAX_NAME - 1] = '\0';
            strncpy(s_metas[s_count].name, fno.fname, sizeof(s_metas[s_count].name)-1);
            s_metas[s_count].size = fno.fsize;
            strcpy(s_metas[s_count].mime, (strstr(fno.fname, ".png")||strstr(fno.fname,".PNG")) ? "image/png" : "image/jpeg");
            memset(s_metas[s_count].sha16, 0, sizeof(s_metas[s_count].sha16));
            s_count++;

            if (s_count >= USB_ADVERT_MAX_FILES)
                break;
        }
    }

    f_closedir(&dir);

    {
        char msg[64];
        snprintf(msg, sizeof(msg), TAG "found %lu files", (unsigned long)s_count);
        STLINK_UART_Println(msg);
    }

    s_scanned = true;

    if (s_count == 0)
        return USB_ADVERT_ERR_NO_FILES;
    return USB_ADVERT_OK;
}

usb_advert_err_t USB_Advert_ReadByName(const char* filename)
{
    if (filename == NULL)
        return USB_ADVERT_ERR_PARAM;

    FIL     file;
    FRESULT res;
    UINT    br;
    char    buf[128];
    char    path[128];

    snprintf(path, sizeof(path), "/%s", filename);

    res = f_open(&file, path, FA_READ);
    if (res != FR_OK) {
        STLINK_UART_Println(TAG "open fail");
        return USB_ADVERT_ERR_FOPEN;
    }

    STLINK_UART_Println(TAG "reading file...");
    while (1) {
        res = f_read(&file, buf, sizeof(buf) - 1, &br);
        if (res != FR_OK) {
            f_close(&file);
            STLINK_UART_Println(TAG "read fail");
            return USB_ADVERT_ERR_FREAD;
        }

        if (br == 0)
            break;

        buf[br] = '\0';
        STLINK_UART_Print(buf);
    }

    STLINK_UART_Println("\r\n[EOF]");
    f_close(&file);
    return USB_ADVERT_OK;
}

usb_advert_err_t USB_Advert_ReadByIndex(uint32_t index)
{
    if (!s_scanned)
        return USB_ADVERT_ERR_PARAM;
    if (index >= s_count)
        return USB_ADVERT_ERR_IDX_RANGE;

    return USB_Advert_ReadByName(s_files[index]);
}

usb_advert_err_t USB_Advert_StreamFile(const char* filename)
{
    if (filename == NULL)
        return USB_ADVERT_ERR_PARAM;

    FIL     file;
    FRESULT res;
    UINT    br;
    uint8_t buf[USB_ADVERT_CHUNK_SIZE];
    char    path[128];
    uint32_t total = 0;

    snprintf(path, sizeof(path), "/%s", filename);

    res = f_open(&file, path, FA_READ);
    if (res != FR_OK) {
        char msg[96];
        snprintf(msg, sizeof(msg), TAG "open fail: %s\r\n", USB_Advert_errstr(USB_ADVERT_ERR_FOPEN));
        UART6_SendString(msg);
        return USB_ADVERT_ERR_FOPEN;
    }

    UART6_SendString(TAG "streaming start\r\n");

    while (1) {
        res = f_read(&file, buf, sizeof(buf), &br);
        if (res != FR_OK) {
            char msg[96];
            snprintf(msg, sizeof(msg), TAG "read fail: %s\r\n", USB_Advert_errstr(USB_ADVERT_ERR_FREAD));
            UART6_SendString(msg);
            f_close(&file);
            return USB_ADVERT_ERR_FREAD;
        }
        if (br == 0)
            break;

        uint8_t frame_buf[FRAME_HDR_SIZE + USB_ADVERT_CHUNK_SIZE + FRAME_TLR_SIZE];
        size_t	frame_len = 0;

        frame_err_t fer = frame_build(FRAME_MEDIA_CHUNK, buf, (uint16_t)br, frame_buf, sizeof(frame_buf), &frame_len);
        if(fer != FRAME_OK){
        	UART6_SendString(TAG "frame build fail\r\n");
			f_close(&file);
			return USB_ADVERT_ERR_IO;
        }
        UART6_SendBytes(frame_buf, (uint16_t)frame_len);
        total += br;
    }

    f_close(&file);

    {
        char msg[96];
        snprintf(msg, sizeof(msg), TAG "done %lu bytes\r\n", (unsigned long)total);
        UART6_SendString(msg);
    }
    return USB_ADVERT_OK;
}

usb_advert_err_t USB_Advert_StreamAll(void)
{
	if (!s_scanned)
		return USB_ADVERT_ERR_PARAM;

	for (uint32_t i = 0; i < s_count; i++) {
		usb_advert_err_t er = USB_Advert_StreamFile(s_files[i]);
		if (er != USB_ADVERT_OK)
			return er;
	}

	{
		char msg[96];
		snprintf(msg, sizeof(msg), TAG "all streamed (%lu files)\r\n", (unsigned long)s_count);
		UART6_SendString(msg);
	}
	return USB_ADVERT_OK;
}

uint32_t USB_Advert_GetFileCount(void)
{
    return s_count;
}

const char* USB_Advert_GetName(uint32_t index)
{
    if (index < s_count)
        return s_files[index];
    return NULL;
}

bool USB_Advert_GetMeta(uint32_t idx, usb_advert_meta_t* out){
    if(idx >= s_count || !out) return false;
    *out = s_metas[idx];
    return true;
}

bool USB_Advert_IsScanned(void){
	return s_scanned;
}

