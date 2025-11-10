/*
 * frame_reader.h
 *
 *  Created on: 2025. 11. 10.
 *      Author: SSAFY
 */

#ifndef MAIN_INCLUDE_FRAME_READER_H_
#define MAIN_INCLUDE_FRAME_READER_H_

#include <stddef.h>
#include <stdint.h>
#include <stdbool.h>

/* 바이트 스트림에서 프레임을 추출하는 소형 리더
   (헤더[6] + payload[len] + CRC16[2]) 가정, big-endian len/CRC */
typedef void (*frame_emit_cb_t)(const uint8_t* frame, size_t len, void* user);

typedef struct {
    uint8_t  buf[1024];
    size_t   len;
    frame_emit_cb_t emit;
    void*    user;
} frame_reader_t;

void frame_reader_init(frame_reader_t* r, frame_emit_cb_t cb, void* user);
void frame_reader_feed(frame_reader_t* r, const uint8_t* data, size_t n);




#endif /* MAIN_INCLUDE_FRAME_READER_H_ */
