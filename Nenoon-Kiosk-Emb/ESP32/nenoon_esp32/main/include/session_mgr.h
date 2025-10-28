/*
 * auth_adapter.h
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */

#ifndef MAIN_INCLUDE_SESSION_MGR_H_
#define MAIN_INCLUDE_SESSION_MGR_H_

#include "esp_err.h"
#include <stddef.h>
#include <stdbool.h>

esp_err_t 	session_mgr_init(void);
esp_err_t 	session_open_with_ssaid(const char* ssaid, unsigned ttl_ms, char* out, size_t out_len);
bool		session_verify_get_ssaid(const char* token, char* out_ssaid, size_t out_sz);

esp_err_t 	session_owner_get(char* out, size_t out_sz);
esp_err_t 	session_owner_clear(void);

#endif /* MAIN_INCLUDE_SESSION_MGR_H_ */