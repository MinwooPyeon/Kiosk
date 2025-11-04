/*
 * media_disaptcher.h
 *
 *  Created on: 2025. 11. 4.
 *      Author: SSAFY
 */

#ifndef MAIN_INCLUDE_MEDIA_DISAPTCHER_H_
#define MAIN_INCLUDE_MEDIA_DISAPTCHER_H_

#include <stdint.h>

void media_dispatch_index(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len);
void media_dispatch_pull(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len);


#endif /* MAIN_INCLUDE_MEDIA_DISAPTCHER_H_ */
