/*
 * license_mgr.h
 *
 *  Created on: Nov 1, 2025
 *      Author: SSAFY
 */

#ifndef LICENSE_MGR_H
#define LICENSE_MGR_H

#include <stdint.h>
#include <stdbool.h>
#include "frame.h"

#define LIC_MAX_ENTRIES		16
#define LIC_KEY_MAX_LEN		64
#define LIC_APP_MAX_LEN     32
#define LIC_ASSIGNED_TO_MAX 64

typedef struct{
	char		license_key[LIC_KEY_MAX_LEN];
	char		app_id[LIC_APP_MAX_LEN];
	char		issued_to[LIC_ASSIGNED_TO_MAX];
	uint32_t	max_activations;
	uint32_t	valid_days;
	uint32_t	activations;
	bool		revoked;
}license_entry_t;

void lic_mgr_init(void);
bool lic_mgr_manager_login(const char* id, const char* pw);
bool lic_mgr_issue(const char* app_id, const char* issued_to, uint32_t max_activations, uint32_t valid_days, char* out_license_key, size_t out_sz);
bool lic_mgr_revoke(const char* license_key);
bool lic_mgr_validate(const char* license_key);
bool lic_mgr_get_challenge(const char* license_key, const char* app_id, const char* appSigSha256, const char* ssaid, const char* pubkeyPem, uint8_t* out_chal, uint16_t* out_len);
bool lic_mgr_get_jwt(const char* license_key, const char* appSigSha256, const char* ssaid, const char* pubkeyPem, const char* signatureB64, char* out_jwt, size_t out_jwt_sz);

#endif
