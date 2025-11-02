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

static void fp_reset(frame_parser_t* p){
	p->state	= FP_SYNC1;
	p->pos		= 0;
	p->expect_payload = 0;
}

/* SYNC1: MAGIC MSB 기다림 */
static void fp_feed_sync1(frame_parser_t* p, uint8_t b){
    if (b == FRAME_MAGIC_MSB) {
        p->buf[0] = b;
        p->pos    = 1;
        p->state  = FP_SYNC2;
    }
}

/* SYNC2: MAGIC LSB 기다림 */
static void fp_feed_sync2(frame_parser_t* p, uint8_t b){
    if (b == FRAME_MAGIC_LSB) {
        p->buf[1] = b;
        p->pos    = 2;
        p->state  = FP_HDR;          /* 이제 VER,TYPE,LEN 받으러감 */
    } else {
        /* 첫 바이트부터 다시 */
        fp_reset(p);
        /* 혹시 이 바이트가 다시 MSB면 그 자리에서 시작해도 됨 */
        if (b == FRAME_MAGIC_MSB) {
            p->buf[0] = b;
            p->pos    = 1;
            p->state  = FP_SYNC2;
        }
    }
}

/* HDR: VER+TYPE+LEN(4바이트) 받는 중 */
static void fp_feed_hdr(frame_parser_t* p, uint8_t b){
    p->buf[p->pos++] = b;
    if (p->pos == FRAME_HDR_SIZE) {
        /* payload 길이 확인 */
        uint16_t plen = be16_rd(&p->buf[4]);
        if (plen > FRAME_MAX_PAYLOAD) {
            /* 말도 안 되는 길이 → 리셋 */
            fp_reset(p);
            return;
        }
        p->expect_payload = plen;
        if (plen == 0) {
            /* payload 없으면 바로 CRC 모드 */
            p->state = FP_CRC;
        } else {
            p->state = FP_PAYLOAD;
        }
    }
}

/* PAYLOAD: expect_payload 만큼 받는 중 */
static void fp_feed_payload(frame_parser_t* p, uint8_t b){
    p->buf[p->pos++] = b;
    if (p->pos == FRAME_HDR_SIZE + p->expect_payload) {
        p->state = FP_CRC;
    }
}

/* CRC: 마지막 2바이트 받기 */
static void fp_feed_crc(frame_parser_t* p, uint8_t b){
    p->buf[p->pos++] = b;
    size_t need = FRAME_HDR_SIZE + p->expect_payload + FRAME_TLR_SIZE;
    if (p->pos == need) {
        /* CRC 검사 */
        uint16_t got_crc  = be16_rd(&p->buf[p->pos - 2]);
        uint16_t calc_crc = frame_crc16_ccitt(&p->buf[2], 4u + p->expect_payload);
        if (got_crc == calc_crc) {
            /* 프레임 완성 → 콜백 */
            if (p->on_frame) {
                p->on_frame(p->buf, p->pos);
            }
        }
        /* 다음 프레임 준비 */
        fp_reset(p);
    }
}

/* --- public --------------------------------------------------------- */

void frame_parser_init(frame_parser_t* p, frame_rx_cb_t cb)
{
    p->on_frame = cb;
    fp_reset(p);
}

void frame_parser_feed(frame_parser_t* p, const uint8_t* data, size_t len)
{
    for (size_t i = 0; i < len; i++) {
        uint8_t b = data[i];

        switch (p->state) {
        case FP_SYNC1:
            fp_feed_sync1(p, b);
            break;
        case FP_SYNC2:
            fp_feed_sync2(p, b);
            break;
        case FP_HDR:
            fp_feed_hdr(p, b);
            break;
        case FP_PAYLOAD:
            fp_feed_payload(p, b);
            break;
        case FP_CRC:
            fp_feed_crc(p, b);
            break;
        default:
            fp_reset(p);
            break;
        }
    }
}
