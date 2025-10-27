/*
 * frame.c
 *
 *  Created on: 2025. 10. 26.
 *  Updated on: 2025. 10. 27.
 *      Author: Park Joo Hyun
 */

#include "frame.h"
#include <string.h>

static inline uint16_t be16_rd(const uint8_t *p) {
	return (uint16_t)((p[0] << 8) | p[1]);
}
static inline void be16_wr(uint8_t *p, uint16_t v) {
	p[0] = (uint8_t)(v >> 8);
	p[1] = (uint8_t)(v & 0xFF);
}

uint16_t frame_crc16_ccitt(const uint8_t *data, size_t len) {
	/* CRC-16/CCITT-FALSE: poly 0x1021, init 0xFFFF, refin/out=false,
	 * xorout=0x0000 */
	uint16_t crc = 0xFFFFu;
	for (size_t i=0;i<len;i++) {
        crc ^= (uint16_t)data[i] << 8;
        for(int b=0;b<8;b++){
            if(crc & 0x8000) crc =  (uint16_t)((crc << 1) ^ 0x1021);
            else                    (uint16_t)(crc << 1);
        }
    }
    return crc;
}

frame_err_t frame_build(uint8_t type, const uint8_t* payload, uint16_t len, uint8_t* out_buf, size_t out_cap, size_t* out_len){
    if(!out_buf) return FRAME_ERR_ARG;
    if((len > 0 && !payload) || len>FRAME_MAX_PAYLOAD) return FRAME_ERR_OOB;

    size_t need = FRAME_HDR_SIZE + len + FRAME_TLR_SIZE;
    if(out_cap < need) return FRAME_ERR_OOB;

    out_buf[0] = FRAME_MAGIC_MSB;
    out_buf[1] = FRAME_MAGIC_LSB;
    out_buf[2] = FRAME_VER;
    out_buf[3] = type;
    be16_wr(&out_buf[4], len);
    if(len) memcpy(&out_buf[6], payload, len);

    uint16_t crc = frame_crc16_ccitt(&out_buf[2], 4u + len);
    be16_wr(&out_buf[6 + len], crc);

    if(out_len) *out_len = need;
    return FRAME_OK;
}

frame_err_t frame_peek_len(const uint8_t* in_buf, size_t in_len, uint16_t* out_len){
    if(!in_buf || !out_len) return FRAME_ERR_ARG;
    if(in_len < FRAME_HDR_SIZE) return FRAME_ERR_TRUNC;
    if(in_buf[0] != FRAME_MAGIC_MSB || in_buf[1] != FRAME_MAGIC_LSB) return FRAME_ERR_MAGIC;
    if(in_buf[2] != FRAME_VER) return FRAME_ERR_MAGIC;
    *out_len = be16_rd(&in_buf[4]);
    if(*out_len > FRAME_MAX_PAYLOAD) return FRAME_ERR_OOB;
    return FRAME_OK;
}

frame_err_t frame_parse(const uint8_t* in_buf, size_t in_len, frame_t* out){
    if(!in_buf || !out) return FRAME_ERR_ARG;
    if(in_len < FRAME_HDR_SIZE + FRAME_TLR_SIZE) return FRAME_ERR_TRUNC;

    if(in_buf[0] != FRAME_MAGIC_MSB || in_buf[1] != FRAME_MAGIC_LSB) return FRAME_ERR_MAGIC;
    if(in_buf[2] != FRAME_VER) return FRAME_ERR_MAGIC;

    uint8_t  type = in_buf[3];
    uint16_t len  = be16_rd(&in_buf[4]);
    if(len > FRAME_MAX_PAYLOAD) return FRAME_ERR_OOB;

    size_t total = FRAME_HDR_SIZE + len + FRAME_TLR_SIZE;
    if(in_len < total) return FRAME_ERR_TRUNC;

    uint16_t crc_rx = be16_rd(&in_buf[6 + len]);
    uint16_t crc_ok = frame_crc16_ccitt(&in_buf[2], 4u + len);
    if(crc_rx != crc_ok) return FRAME_ERR_CRC;

    out->type = type;
    out->len  = len;
    if(len) memcpy(out->payload, &in_buf[6], len);

    return FRAME_OK;
}

void frame_parser_init(frame_parser_t* p){
    if(!p) return;
    p->fill = 0;
    p->scan = 0;
}

static void shift_left(uint8_t* buf, size_t* fill, size_t n){
    if(n==0 || *fill==0) return;
    if(n>= *fill) {*fill = 0; return;}
    memmove(buf, buf+n, *fill-n);
    *fill -= n;
}

bool frame_parser_feed(frame_parser_t* p, const uint8_t* data, size_t n, frame_t* out, size_t* consumed){
    if(consumed) *consumed = 0;
    if(!p || (!data && n) || !out) return false;

    size_t can = (FRAME_MAX_WIRE - p->fill);
    size_t take = (n<can)?n:can;
    if(take && data){
        memcpy(p->buf + p->fill, data, take);
    }
    if(consumed) *consumed = take;

    while(p->fill >= 2){
        size_t i = p->scan;
        for(;i+1<p->fill;i++){
            if(p->buf[i]==FRAME_MAGIC_MSB && p->buf[i+1] == FRAME_MAGIC_LSB)
                break;
        }
        if(i+1 >= p->fill){
            size_t drop = (p->fill > 1)?(p->fill - 1):0;
            shift_left(p->buf, &p->fill, drop);
            p->scan = 0;
            break;
        }

        if(i>0){
            shift_left(p->buf, &p->fill, i);
            p->scan = 0;
        }

        if(p->fill < FRAME_HDR_SIZE) return false;
        if(p->buf[2] != FRAME_VER){
            shift_left(p->buf, &p->fill, 1);
            p->scan = 0;
            continue;
        }

        uint16_t len = be16_rd(&p->buf[4]);
        if(len > FRAME_MAX_PAYLOAD){
            shift_left(p->buf, &p->fill, 1);
            p->scan = 1;
            continue;
        }

        size_t total = FRAME_HDR_SIZE + FRAME_TLR_SIZE;
        if(p->fill < total) return;

        uint16_t crc_rx = be16_rd(&p->buf[6 + len]);
        uint16_t crc_ok = frame_crc16_ccitt(&p->buf[2], 4u + len);
        if(crc_rx != crc_ok){
            /* Bad frame → drop one byte and resync */
            shift_left(p->buf, &p->fill, 1);
            p->scan = 0;
            continue;
        }

        out->type = p->buf[3];
        out->len  = len;
        if(len) memcpy(out->payload, &p->buf[6], len);

        /* Consume the frame from buffer */
        shift_left(p->buf, &p->fill, total);
        p->scan = 0;
        return true;
    }
    return false;
}

