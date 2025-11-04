/*
 * frame_dispatcher.h
 *
 *  Created on: Nov 2, 2025
 *      Author: SSAFY
 */

#ifndef INC_FRAME_DISPATCHER_H_
#define INC_FRAME_DISPATCHER_H_

#include <stdint.h>
#include <stddef.h>

typedef void (*dispatch_fn_t)(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len);

typedef struct{
    uint8_t      req_type;
    uint8_t      resp_type;
    dispatch_fn_t fn;
} route_t;

typedef const route_t ROUTES[]={
		{ FRAME_MEDIA_INDEX_REQ,   FRAME_MEDIA_INDEX_RESP, media_dispatch_index },
		{ FRAME_MEDIA_PULL_REQ,    FRAME_MEDIA_CHUNK,      media_dispatch_pull  },
		{ FRAME_LIC_MGR_LOGIN,     FRAME_LIC_RESP,         lic_dispatch_mgr_login },
		{ FRAME_LIC_ISSUE,         FRAME_LIC_RESP,         lic_dispatch_issue },
		{ FRAME_LIC_REVOKE,        FRAME_LIC_RESP,         lic_dispatch_revoke },
		{ FRAME_LIC_VALIDATE,      FRAME_LIC_RESP,         lic_dispatch_validate },
		{ FRAME_LIC_GET_CHALLENGE, FRAME_LIC_RESP,         lic_dispatch_get_challenge },
		{ FRAME_LIC_GET_JWT,       FRAME_LIC_RESP,         lic_dispatch_get_jwt },
};

void proto_dispatch_handle(const uint8_t* frame, size_t len);

#endif /* INC_FRAME_DISPATCHER_H_ */
