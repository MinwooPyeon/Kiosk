/*
 * lic_dispatch.h
 *
 *  Created on: Nov 4, 2025
 *      Author: SSAFY
 */

#ifndef INC_LIC_DISPATCH_H_
#define INC_LIC_DISPATCH_H_

#include <stdint.h>

void lic_dispatch_mgr_login(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len);
void lic_dispatch_issue(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len);
void lic_dispatch_revoke(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len);
void lic_dispatch_validate(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len);
void lic_dispatch_get_challenge(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len);
void lic_dispatch_get_jwt(const uint8_t* payload, uint16_t pl_len, uint8_t* out_payload, uint16_t* out_len);

#endif /* LIC_DISPATCH_H_ */
