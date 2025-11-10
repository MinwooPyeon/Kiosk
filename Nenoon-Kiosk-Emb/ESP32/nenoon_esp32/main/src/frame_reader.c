/*
 * frame_reader.c
 *
 *  Created on: 2025. 11. 10.
 *      Author: SSAFY
 */

#include "frame_reader.h"
#include "frame.h"   // FRAME_HDR_SIZE, FRAME_TLR_SIZE, frame_crc16_ccitt
#include <string.h>

/* 추정: header[3] = type, header[4..5] = be16 length
   전체 길이 = 6 + len + 2(CCITT) */
static inline uint16_t be16(const uint8_t* p){ return (uint16_t)((p[0]<<8)|p[1]); }

void frame_reader_init(frame_reader_t* r, frame_emit_cb_t cb, void* user){
    r->len  = 0;
    r->emit = cb;
    r->user = user;
}

void frame_reader_feed(frame_reader_t* r, const uint8_t* data, size_t n)
{
    if(!n) return;

    /* 입력 append (초과분은 앞쪽을 밀어내며 유지) */
    if(n > sizeof(r->buf) - r->len){
        size_t drop = n - (sizeof(r->buf) - r->len);
        if(drop > r->len){ r->len = 0; } else {
            memmove(r->buf, r->buf + drop, r->len - drop);
            r->len -= drop;
        }
    }
    memcpy(r->buf + r->len, data, n);
    r->len += n;

    /* 슬라이딩 파싱 */
    size_t off = 0;
    while(r->len - off >= (FRAME_HDR_SIZE + FRAME_TLR_SIZE)){
        const uint8_t* p = r->buf + off;
        uint16_t plen = be16(&p[4]);
        /* 비정상 길이 가드 */
        if(plen > FRAME_MAX_WIRE) { off += 1; continue; }

        size_t need = (size_t)FRAME_HDR_SIZE + plen + FRAME_TLR_SIZE;
        if(r->len - off < need) break; // 더 받아야 함

        /* CRC 검증 */
        uint16_t want = be16(&p[FRAME_HDR_SIZE + plen]); // tail CRC
        uint16_t got  = frame_crc16_ccitt(p, FRAME_HDR_SIZE + plen);
        if(want == got){
            /* 유효 프레임 emit */
            if(r->emit) r->emit(p, need, r->user);
            off += need;
        }else{
            /* 재동기화를 위해 한 바이트 전진 */
            off += 1;
        }
    }

    /* 남은 바이트를 앞으로 당김 */
    if(off){
        memmove(r->buf, r->buf + off, r->len - off);
        r->len -= off;
    }
}



