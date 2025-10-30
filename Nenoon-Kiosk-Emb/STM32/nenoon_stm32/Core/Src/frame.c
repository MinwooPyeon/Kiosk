/*
 * frame.c
 *
 *  Created on: Oct 30, 2025
 *      Author: SSAFY
 */


#include "frame.h"
#include <string.h>

static inline void be16_write(uint8_t* p, uint16_t v){
	p[0] = (uint8_t)(v >> 8);
	p[1] = (uint8_t)(v & 0xFF);
}

uint16_t frame_crc16_ccitt(const uint8_t* data ,size_t len){
	uint16_t crc = 0xFFFFu;
	for(size_t i =0;i<len;i++){
		crc ^= (uint16_t)data[i] << 8;
		for(int b=0;b<8;b++){
			if(crc & 0x8000) 	crc = (uint16_t)((crc << 1) ^ 0x1021);
			else				crc = (uint16_t)(crc << 1);
		}
	}
	return crc;
}

frame_err_t frame_build(uint8_t type, const uint8_t* payload, uint16_t len, uint8_t* out_buf, size_t out_cap, size_t* out_len){
	if(!out_buf) 				return FRAME_ERR_ARG;
	if(len > FRAME_MAX_PAYLOAD) return FRAME_ERR_OOB;
	if(len && !payload) 		return FRAME_ERR_ARG;

	size_t need = FRAME_HDR_SIZE + len + FRAME_ERR_ORB;

}
