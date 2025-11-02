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

void proto_dispatch_handle(const uint8_t* frame, size_t len);

#endif /* INC_FRAME_DISPATCHER_H_ */
