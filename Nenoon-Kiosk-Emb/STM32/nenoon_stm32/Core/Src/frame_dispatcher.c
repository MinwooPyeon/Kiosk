/*
 * frame_dispatcher.c
 *
 *  Created on: Nov 2, 2025
 *      Author: SSAFY
 */


#include "frame.h"
#include "license_mgr.h"
#include "lic_dispatch.h"
#include "usart.h"
#include <string.h>
#include <stdio.h>



#include "usb_advert.h"
#include "frame.h"
#include "usart.h"
#include <stdio.h>
#include <string.h>

static void media_dispatch_index(const uint8_t* payload, uint16_t pl_len)
{
    (void)payload; (void)pl_len;

    // 필요하면 여기서 USB 스캔 한 번 더
    if (!USB_Advert_IsScanned()) {
        USB_Advert_Scan();
    }

    uint32_t cnt = USB_Advert_GetFileCount();

    // JSON 빌드
    char js[512];
    size_t off = 0;
    off += snprintf(js+off, sizeof(js)-off, "{\"gen\":1,\"files\":[");
    for (uint32_t i=0; i<cnt && off < sizeof(js); i++) {
        usb_advert_meta_t m;
        if (!USB_Advert_GetMeta(i, &m)) continue;
        off += snprintf(js+off, sizeof(js)-off,
                        "%s{\"id\":\"%u\",\"name\":\"%s\",\"size\":%llu,\"sha256_16\":\"%s\",\"mime\":\"%s\"}",
                        (i?",":""), i,
                        m.name,
                        (unsigned long long)m.size,
                        m.sha16[0]? m.sha16:"0000000000000000",
                        m.mime[0]? m.mime:"application/octet-stream");
    }
    if (off < sizeof(js)) {
        off += snprintf(js+off, sizeof(js)-off, "]}");
    }

    // 프레임으로 감싸서 UART6으로 전송
    uint8_t fbuf[FRAME_MAX_WIRE];
    size_t  flen = 0;
    if (frame_build(FRAME_MEDIA_INDEX_RESP,
                    (const uint8_t*)js, (uint16_t)off,
                    fbuf, sizeof(fbuf), &flen) == FRAME_OK)
    {
        UART6_SendBytes(fbuf, (uint16_t)flen);
    }
}

static void media_dispatch_pull(const uint8_t* payload, uint16_t pl_len)
{
    if (pl_len < 1 + 8 + 4) {
        return; // payload 너무 짧음
    }

    const char* id = (const char*)payload;
    size_t id_len = strlen(id);
    if (id_len + 8 + 4 > pl_len) {
        return;
    }

    const uint8_t* p = payload + id_len + 1;
    uint64_t off =
        ((uint64_t)p[0] << 56) |
        ((uint64_t)p[1] << 48) |
        ((uint64_t)p[2] << 40) |
        ((uint64_t)p[3] << 32) |
        ((uint64_t)p[4] << 24) |
        ((uint64_t)p[5] << 16) |
        ((uint64_t)p[6] << 8)  |
        ((uint64_t)p[7]);
    p += 8;
    uint32_t len =
        ((uint32_t)p[0] << 24) |
        ((uint32_t)p[1] << 16) |
        ((uint32_t)p[2] << 8)  |
        ((uint32_t)p[3]);

    // 여기서 id → 파일명 매핑
    // 만약 위에서 "id"에 그냥 인덱스를 실었다면:
    uint32_t idx = (uint32_t)atoi(id);
    const char* fname = USB_Advert_GetName(idx);
    if (!fname) return;

    // FatFs로 읽기
    FIL file;
    char path[96];
    snprintf(path, sizeof(path), "/%s", fname);
    if (f_open(&file, path, FA_READ) != FR_OK) {
        return;
    }
    // 오프셋으로 이동
    if (f_lseek(&file, off) != FR_OK) {
        f_close(&file);
        return;
    }

    uint8_t buf[1024]; // 한 번에 내려줄 최대 크기
    if (len > sizeof(buf)) len = sizeof(buf);

    UINT br = 0;
    if (f_read(&file, buf, len, &br) != FR_OK) {
        f_close(&file);
        return;
    }
    f_close(&file);

    // CRC32 만들거나, 일단 0 채워서 보내기
    uint8_t fbuf[FRAME_MAX_WIRE];
    size_t  flen = 0;
    if (frame_build(FRAME_MEDIA_CHUNK, buf, (uint16_t)br,
                    fbuf, sizeof(fbuf), &flen) == FRAME_OK)
    {
        UART6_SendBytes(fbuf, (uint16_t)flen);
    }
}



void proto_dispatch_handle(const uint8_t* frame, size_t len){
	if(len < FRAME_HDR_SIZE + FRAME_TLR_SIZE) return;

	uint8_t type = frame[3];
	const uint8_t* payload = &frame[6];
	uint16_t pl_len = (uint16_t)((frame[4] << 8) | frame[5]);

	uint8_t out_payload[256];
	size_t	out_len = 0;

	switch(type){
		case FRAME_MEDIA_INDEX_REQ:
			media_dispatch_index(payload, pl_len);
			return;
		case FRAME_MEDIA_PULL_REQ:
			media_dispatch_pull(payload, pl_len);
			return;
		case FRAME_LIC_MGR_LOGIN:
			lic_dispatch_mgr_login(payload, pl_len, out_payload, &out_len);
			break;
		case FRAME_LIC_ISSUE:
			lic_dispatch_issue(payload, pl_len, out_payload, &out_len);
			break;
		case FRAME_LIC_REVOKE:
			lic_dispatch_revoke(payload, pl_len, out_payload, &out_len);
			break;
		case FRAME_LIC_VALIDATE:
			lic_dispatch_validate(payload, pl_len, out_payload, &out_len);
			break;
		case FRAME_LIC_GET_CHALLENGE:
			lic_dispatch_get_challenge(payload, pl_len, out_payload, &out_len);
			break;
		case FRAME_LIC_GET_JWT:
			lic_dispatch_get_jwt(payload, pl_len, out_payload, &out_len);
			break;
		default:
			return;
	}

	uint8_t out_frame[FRAME_MAX_WIRE];
	size_t	of_len = 0;
	if(frame_build(FRAME_LIC_RESP, out_payload, out_len, out_frame, sizeof(out_frame), &of_len) == FRAME_OK){
		UART6_SendBytes(out_frame, (uint16_t)of_len);
	}
}
