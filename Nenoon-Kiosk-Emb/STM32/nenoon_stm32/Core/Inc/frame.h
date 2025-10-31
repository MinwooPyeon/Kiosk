/*
 * frame.h
 *
 *  Created on: Oct 30, 2025
 *      Author: SSAFY
 */

#ifndef INC_FRAME_H_
#define INC_FRAME_H_

#include <stdint.h>
#include <stddef.h>
#include <stdbool.h>

#define FRAME_MAGIC_MSB			0xA5
#define FRAME_MAGIC_LSB			0x5A
#define FRAME_VER				0x01

#define FRAME_AUTH_CHALLENGE	0x10
#define FRAME_AUTH_RESPONSE		0x11
#define FRAME_MEDIA_INDEX		0x20
#define FRAME_MEDIA_PULL		0x21
#define FRAME_MEDIA_CHUNK		0x22
#define FRAME_HB_STAT			0x30
#define FRAME_LIC_MGR_LOGIN         0x40  /* Manager Login */
#define FRAME_LIC_ISSUE             0x41  /* License Issuance */
#define FRAME_LIC_REVOKE            0x42  /* License Revoke */
#define FRAME_LIC_VALIDATE          0x43  /* License Validate */
#define FRAME_LIC_GET_CHALLENGE     0x44  /* Get Challenge Byte */
#define FRAME_LIC_GET_JWT           0x45  /* Get JWT Token */
#define FRAME_LIC_RESP              0x4F  /* result + json/err */

#define FRAME_MAX_PAYLOAD		512
#define FRAME_HDR_SIZE			6u
#define FRAME_TLR_SIZE			2u
#define FRAME_MAX_WIRE			(FRAME_HDR_SIZE + FRAME_MAX_PAYLOAD + FRAME_TLR_SIZE)

typedef enum{
    FRAME_OK = 0,
    FRAME_ERR_ARG,
    FRAME_ERR_OOB,
} frame_err_t;

#ifdef __cplusplus
extern "C"{
#endif

uint16_t frame_crc_ccitt(const uint8_t* data, size_t len);
frame_err_t frame_build(uint8_t type, const uint8_t* payload, uint16_t len, uint8_t* out_buf, size_t out_cap, size_t* out_len);

#ifdef __cplusplus
}
#endif

#endif /* INC_FRAME_H_ */
