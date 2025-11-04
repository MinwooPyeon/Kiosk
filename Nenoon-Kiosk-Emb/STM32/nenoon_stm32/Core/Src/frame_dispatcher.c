/*
 * frame_dispatcher.c
 *
 *  Created on: Nov 2, 2025
 *      Author: SSAFY
 */


#include "frame.h"
#include "usart.h"
#include "lic_dispatch.h"
#include "media_dispatch.h"
#include <string.h>
#include <stdio.h>

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
