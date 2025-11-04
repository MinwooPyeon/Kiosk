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

	for(size_t i = 0 ;i<sizeof(ROUTES)/sizeof(ROUTES[0]);i++){
		if(ROUTES[i].req_type == type){
			ROUTES[i].fn(payload, pl_len, out_payload, &out_len);

			uint8_t out_frame[FRAME_MAX_WIRE];
			size_t	of_len = 0;
			if(frame_build(ROUTES[i].resp_type, out_payload, out_len, out_frame, sizeof(out_frame), &of_len) == FRAME_OK){
			                UART6_SendBytes(out_frame, (uint16_t)of_len);
			}
			return;
		}
	}
}
