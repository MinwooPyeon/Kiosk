/*
 * frame.h
* Simple binary framing (MAGIC + VER + TYPE + LEN + PAYLOAD + CRC16)
 *
 * Wire format (big-endian):
 *   [0..1]  MAGIC = 0xA5 0x5A
 *   [2]     VER   = 0x01
 *   [3]     TYPE
 *   [4..5]  LEN   = payload length (0..FRAME_MAX_PAYLOAD)
 *   [6..]   PAYLOAD (LEN bytes)
 *   [...]   CRC16-CCITT (0x1021, init 0xFFFF) over bytes [2..(5+LEN)]
 *            i.e., VER|TYPE|LEN|PAYLOAD  (MAGIC 제외)
 * 
 *  Created on: 2025. 10. 26.
 *  Updated on: 2025. 10. 27.
 *      Author: Park Joo Hyun
 */

#ifndef MAIN_INCLUDE_FRAME_H_
#define MAIN_INCLUDE_FRAME_H_

#define FRAME_MAX_PAYLOAD   1024u
#define FRAME_MAGIC_MSB     0xA5u
#define FRAME_MAGIC_LSB     0x5Au
#define FRAME_VER           0x01u

#define FRAME_HDR_SIZE      6u
#define FRAME_TLR_SIZE      2u
#define FRAME_MAX_WIRE      (FRAME_HDR_SIZE + FRAME_MAX_PAYLOAD + FRAME_TLR_SIZE)

#include <stdint.h>
#include <stddef.h>
#include <stdbool.h>

typedef enum{
    FRAME_OK,
    FRAME_ERR_ARG,
    FRAME_ERR_OOB,
    FRAME_ERR_MAGIC,
    FRAME_ERR_CRC,
    FRAME_ERR_TRUNC
}frame_err_t;

typedef struct{
    uint8_t     type;
    uint16_t    len;
    uint8_t     payload[FRAME_MAX_PAYLOAD];
}frame_t;

typedef struct{
    uint8_t     buf[FRAME_MAX_WIRE];
    size_t      fill;
    size_t      scan;
}frame_parser_t;

typedef enum {
    FP_EMIT = 0,            // out 프레임 1개 완성됨 (정상)
    FP_MORE,                // 더 많은 입력 필요 (정상 대기)
    FP_RESYNC_MAGIC,        // MAGIC 동기화 중(노이즈 드롭 후 재시도)
    FP_RESYNC_VERSION,      // 버전 불일치 → 1바이트 드롭 후 재동기화
    FP_RESYNC_LEN_OOB,      // LEN 상한 초과 → 1바이트 드롭 후 재동기화
    FP_RESYNC_CRC_FAIL,     // CRC 불일치 → 1바이트 드롭 후 재동기화
    FP_OVERFLOW,            // 내부 버퍼 가득 참 (입력 일부/전부 미수용)
    FP_ARG_ERROR            // 잘못된 인자
} frame_parse_status_t;

void                    frame_parser_init(frame_parser_t* p);
frame_parse_status_t    frame_parser_feed(frame_parser_t* p, const uint8_t* data, size_t n, frame_t* out, size_t* consumed);
uint16_t                frame_crc16_ccitt(const uint8_t* data, size_t len);
#endif /* MAIN_INCLUDE_FRAME_H_ */