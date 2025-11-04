/*
 * media_dispatch.h
 *
 *  Created on: Nov 4, 2025
 *      Author: SSAFY
 */

#ifndef INC_MEDIA_DISPATCH_H_
#define INC_MEDIA_DISPATCH_H_

#include <stdint.h>

void media_dispatch_index(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len);
void media_dispatch_pull(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len);

#endif /* INC_MEDIA_DISPATCH_H_ */
