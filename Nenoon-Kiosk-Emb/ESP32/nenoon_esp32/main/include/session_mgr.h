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

esp_err_t 	session_mgr_init();
esp_err_t 	session_open(unsigned ttl_ms, char* out, size_t out_len);
bool		session_verify(const char* token);
void		session_close_all(void);

#endif /* MAIN_INCLUDE_SESSION_MGR_H_ */
