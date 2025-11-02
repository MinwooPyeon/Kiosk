/*
 * frame_parser.c
 *
 *  Created on: Nov 2, 2025
 *      Author: SSAFY
 */


#include "frame_parser.h"

static inline uint16_t be16_rd(const uint8_t* p){
	return (uint16_t)((p[0] << 8) | p[1]);
}

void frame_parser_init(frame_parser_t* p, frame_rx_cb_t cb){
	p->state 			= FP_SYNC1;
	p->pos				= 0;
	p->expect_payload 	= 0;
	p->on_frame 		= cb;
}


void frame_parser_feed(frame_parser_t* p, const uint8_t* data, size_t len){
	for(size_t i=0;i<len;i++){
		uint8_t b = data[i];

		switch(p->state){

		case FP_SYNC1:
			if(b == FRAME_MAGIC_MSB){
				p->buf[0] = b;
				p->pos = 1;
				p->state = FP_SYNC2;
			}
			break;

		case FP_SYNC2:
			if(b == FRAME_MAGIC_LSB){
				p->buf[1] = b;
				p->pos = 2;
				p->state = FP_HDR;
			}else{
				/* 다시 처음부터 */
				p->state = FP_SYNC1;
			}
			break;

		case FP_HDR:
			/* VER(1) + TYPE(1) + LEN(2) = 4바이트 */
			p->buf[p->pos++] = b;
			if(p->pos == FRAME_HDR_SIZE){
				/* payload 길이 파싱 */
				p->expect_payload = be16_rd(&p->buf[4]);
				if(p->expect_payload > FRAME_MAX_PAYLOAD){
					/* 말도 안 되는 길이면 다시 */
					p->state = FP_SYNC1;
					p->pos   = 0;
				}else if(p->expect_payload == 0){
					/* payload 없으면 바로 CRC 2바이트 */
					p->state = FP_CRC;
				}else{
					p->state = FP_PAYLOAD;
				}
			}
			break;

		case FP_PAYLOAD:
			p->buf[p->pos++] = b;
			if(p->pos == FRAME_HDR_SIZE + p->expect_payload){
				p->state = FP_CRC;
			}
			break;

		case FP_CRC:
			p->buf[p->pos++] = b;
			if(p->pos == FRAME_HDR_SIZE + p->expect_payload + FRAME_TLR_SIZE){
				/* 여기서 CRC 검사 */
				uint16_t got_crc = be16_rd(&p->buf[p->pos - 2]);
				uint16_t calc_crc = frame_crc16_ccitt(&p->buf[2],
									  4u + p->expect_payload);
				if(got_crc == calc_crc){
					/* 완성 → 콜백 */
					if(p->on_frame){
						p->on_frame(p->buf, p->pos);
					}
				}
				/* 다음 프레임 준비 */
				p->state = FP_SYNC1;
				p->pos   = 0;
				p->expect_payload = 0;
			}
			break;
		}
	}
}
