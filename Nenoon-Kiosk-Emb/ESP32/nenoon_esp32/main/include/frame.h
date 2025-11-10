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

#define FRAME_MAX_PAYLOAD   5124u
#define FRAME_MAGIC_MSB     0xA5u
#define FRAME_MAGIC_LSB     0x5Au
#define FRAME_VER           0x01u

#define FRAME_MEDIA_INDEX_REQ		0x30
#define FRAME_MEDIA_INDEX_RESP		0x31
#define FRAME_MEDIA_PULL_REQ		0x32
#define FRAME_MEDIA_CHUNK			0x33
#define FRAME_LIC_MGR_LOGIN         0x40  /* Manager Login */
#define FRAME_LIC_ISSUE             0x41  /* License Issuance */
#define FRAME_LIC_REVOKE            0x42  /* License Revoke */
#define FRAME_LIC_VALIDATE          0x43  /* License Validate */
#define FRAME_LIC_GET_CHALLENGE     0x44  /* Get Challenge Byte */
#define FRAME_LIC_GET_JWT           0x45  /* Get JWT Token */
#define FRAME_LIC_RESP              0x4F  /* result + json/err */

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
    uint32_t	canary1;
    uint32_t	canary2;
}frame_parser_t;

typedef enum {
    FP_EMIT = 0,            // out 프레임 1개 완성됨 (정상)
    FP_MORE,                 // 더 많은 입력 필요 (정상 대기)
    FP_RESYNC_MAGIC,        // MAGIC 동기화 중(노이즈 드롭 후 재시도)
    FP_RESYNC_VERSION,      // 버전 불일치 → 1바이트 드롭 후 재동기화
    FP_RESYNC_LEN_OOB,      // LEN 상한 초과 → 1바이트 드롭 후 재동기화
    FP_RESYNC_CRC_FAIL,     // CRC 불일치 → 1바이트 드롭 후 재동기화
    FP_OVERFLOW,            // 내부 버퍼 가득 참 (입력 일부/전부 미수용)
    FP_ARG_ERROR            // 잘못된 인자
} frame_parse_status_t;

_Static_assert(FRAME_MAX_WIRE >= FRAME_HDR_SIZE + FRAME_TLR_SIZE, "FRAME_MAX_WIRE too small");

void                    frame_parser_init(frame_parser_t* p);
frame_parse_status_t    frame_parser_feed(frame_parser_t* p, const uint8_t* data, size_t n, frame_t* out, size_t* consumed);
uint16_t                frame_crc16_ccitt(const uint8_t* data, size_t len);
frame_err_t frame_build(uint8_t type,
                        const uint8_t* payload, uint16_t len,
                        uint8_t* out_buf, size_t out_cap, size_t* out_len);
#endif /* MAIN_INCLUDE_FRAME_H_ */