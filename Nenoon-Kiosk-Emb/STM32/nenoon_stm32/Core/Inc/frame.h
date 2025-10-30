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



#ifdef __cplusplus
}
#endif

#endif /* INC_FRAME_H_ */
