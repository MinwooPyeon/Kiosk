/*
 * frame_dispatcher.c
 *
 *  Created on: Nov 2, 2025
 *      Author: SSAFY
 */

#include "frame_dispatcher.h"

#include "frame.h"
#include "usart.h"
#include "lic_dispatch.h"
#include "media_dispatch.h"
#include <string.h>
#include <stdio.h>

typedef void (*dispatch_fn_t)(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len);

typedef struct{
    uint8_t      req_type;
    uint8_t      resp_type;
    dispatch_fn_t fn;
} route_t;
static const route_t ROUTES[]={
		{ FRAME_MEDIA_INDEX_REQ,   FRAME_MEDIA_INDEX_RESP, media_dispatch_index },
		{ FRAME_MEDIA_PULL_REQ,    FRAME_MEDIA_CHUNK,      media_dispatch_pull  },
		{ FRAME_LIC_MGR_LOGIN,     FRAME_LIC_RESP,         lic_dispatch_mgr_login },
		{ FRAME_LIC_ISSUE,         FRAME_LIC_RESP,         lic_dispatch_issue },
		{ FRAME_LIC_REVOKE,        FRAME_LIC_RESP,         lic_dispatch_revoke },
		{ FRAME_LIC_VALIDATE,      FRAME_LIC_RESP,         lic_dispatch_validate },
		{ FRAME_LIC_GET_CHALLENGE, FRAME_LIC_RESP,         lic_dispatch_get_challenge },
		{ FRAME_LIC_GET_JWT,       FRAME_LIC_RESP,         lic_dispatch_get_jwt },
};


void proto_dispatch_handle(const uint8_t* frame, size_t len) {
    if(len < FRAME_HDR_SIZE + FRAME_TLR_SIZE) {
        STLINK_UART_Println("Error: Frame length too short");
        return;
    }

    uint8_t type = frame[3];
    const uint8_t* payload = &frame[6];
    uint16_t pl_len = (uint16_t)((frame[4] << 8) | frame[5]);

    uint8_t out_payload[256];
    uint16_t out_len = 0;

    bool found = false;  // 요청 타입이 일치한 경우를 추적
    for(size_t i = 0; i < sizeof(ROUTES) / sizeof(ROUTES[0]); i++) {
        if(ROUTES[i].req_type == type) {
            STLINK_UART_Println("Found matching route");
            ROUTES[i].fn(payload, pl_len, out_payload, &out_len);

            uint8_t out_frame[FRAME_MAX_WIRE];
            size_t of_len = 0;
            if(frame_build(ROUTES[i].resp_type, out_payload, out_len, out_frame, sizeof(out_frame), &of_len) == FRAME_OK) {
                STLINK_UART_SendBytes(out_frame, (uint16_t)of_len);  // ST-LINK VCP
                UART6_SendBytes(out_frame, (uint16_t)of_len);        // 기본 UART (USART6)
                found = true;
            } else {
                STLINK_UART_Println("Error: Failed to build frame");
            }
            break;  // 요청 타입이 일치한 경우 더 이상 루프를 돌 필요 없음
        }
    }

    if (!found) {
        STLINK_UART_Println("Error: No matching route found for the request type");
    }
}


