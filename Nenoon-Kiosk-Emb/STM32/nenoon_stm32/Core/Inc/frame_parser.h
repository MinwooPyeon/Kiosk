/*
 * frame_parser.h
 *
 *  Created on: Nov 2, 2025
 *      Author: SSAFY
 */

#ifndef INC_FRAME_PARSER_H_
#define INC_FRAME_PARSER_H_

#include <stdint.h>
#include <stddef.h>
#include "frame.h"

typedef void (*frame_rx_cb_t)(const uint8_t * frame, size_t len);

typedef enum{
	FP_SYNC1 =0,
	FP_SYNC2,
	FP_HDR,
	FP_PAYLOAD,
	FP_CRC
}frame_parser_state_t;

typedef struct{
	frame_parser_state_t 	state;
	uint8_t					buf[FRAME_MAX_WIRE];
	size_t					pos;
	uint16_t				expect_payload;
	frame_rx_cb_t 			on_frame;
}frame_parser_t;

void frame_parser_init(frame_parser_t* p, frame_rx_cb_t cb);
void frame_parser_feed(frame_parser_t* p, const uint8_t* data, size_t len);

#endif /* INC_FRAME_PARSER_H_ */
