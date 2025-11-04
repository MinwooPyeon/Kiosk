/*
 * media_dispatch.c
 *
 *  Created on: Nov 4, 2025
 *      Author: SSAFY
 */

#include "media_dispatch.h"
#include "usb_advert.h"
#include "ff.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

void media_dispatch_index(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len){
    (void)payload; (void)pl_len;
    if(!USB_Advert_IsScanned()) (void)USB_Advert_Scan();

    uint32_t cnt = USB_Advert_GetFileCount();
    char *w = (char*)out_payload;
    size_t cap = 256, off = 0;

    off += snprintf(&w[off], cap-off, "{\"gen\":1,\"files\":[");
    for (uint32_t i=0; i<cnt && off<cap; i++) {
        const char* name = USB_Advert_GetName(i);
        if (!name) continue;
        FILINFO fi; uint64_t fsz=0;
        if (f_stat(name, &fi) == FR_OK) fsz = fi.fsize;
        off += snprintf(&w[off], cap-off,
                        "%s{\"id\":\"%lu\",\"name\":\"%s\",\"size\":%llu,"
                        "\"sha256_16\":\"0000000000000000\",\"mime\":\"application/octet-stream\"}",
                        (i?",":""), (unsigned long)i, name, (unsigned long long)fsz);
        if (off>=cap) break;
    }
    if (off<cap) off += snprintf(&w[off], cap-off, "]}");
    *out_len = (uint16_t)((off<cap)?off:(cap-1));
}

void media_dispatch_pull(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len){
    if (pl_len < 1+8+4) { *out_len=0; return; }
    const char* id = (const char*)payload;
    size_t id_len = strlen(id);
    if (id_len + 8 + 4 > pl_len) { *out_len=0; return; }

    const uint8_t* p = payload + id_len + 1;
    uint64_t off = ((uint64_t)p[0]<<56)|((uint64_t)p[1]<<48)|((uint64_t)p[2]<<40)|((uint64_t)p[3]<<32)|
                   ((uint64_t)p[4]<<24)|((uint64_t)p[5]<<16)|((uint64_t)p[6]<<8)|((uint64_t)p[7]);
    p += 8;
    uint32_t want = ((uint32_t)p[0]<<24)|((uint32_t)p[1]<<16)|((uint32_t)p[2]<<8)|((uint32_t)p[3]);

    uint32_t idx = (uint32_t)atoi(id);
    const char* fname = USB_Advert_GetName(idx);
    if (!fname) { *out_len=0; return; }

    FIL f; char path[96];
    snprintf(path, sizeof(path), "/%s", fname);
    if (f_open(&f, path, FA_READ) != FR_OK) { *out_len=0; return; }
    if (off && f_lseek(&f, (FSIZE_t)off) != FR_OK) { f_close(&f); *out_len=0; return; }

    if (want==0 || want>224) want=224;
    UINT br=0;
    if (f_read(&f, out_payload, want, &br) != FR_OK) { f_close(&f); *out_len=0; return; }
    f_close(&f);
    *out_len = (uint16_t)br;
}
