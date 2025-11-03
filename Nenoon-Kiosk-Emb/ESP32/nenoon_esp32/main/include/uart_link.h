/*
 * uart_link.h
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */

#ifndef MAIN_INCLUDE_UART_LINK_H_
#define MAIN_INCLUDE_UART_LINK_H_

#include "esp_err.h"

#include <stdint.h>
#include <stddef.h>
#include <stdbool.h>

typedef struct{
	uint32_t		index;
	char			id[48];
	char			name[128];
	uint64_t		size;
	char			sha16[17];
	char			mime[32];
}media_item_t;

typedef struct{
	uint32_t 		gen;
	uint32_t 		count;
	media_item_t*	items;
}media_index_t;

typedef struct{
	int				result;
	char			owner[64];
	char			reason[32];
}auth_ssaid_resp_t;
typedef void (*uart_event_cb_t)(const char* ev, uint32_t arg);

esp_err_t 	uart_link_init(void);

/// HTTP API
esp_err_t uart_link_lic_mgr_login(const char *id, const char *pw, bool *ok);
esp_err_t uart_link_lic_issue(const char *app, const char *to, char *out_lic, size_t out_sz);
esp_err_t uart_link_lic_validate(const char *lic, bool *ok);
esp_err_t uart_link_lic_get_jwt(const char *lic, char *out_jwt, size_t out_sz);

bool uart_link_usb_attached(void);
esp_err_t uart_link_get_index(media_index_t *out);
esp_err_t uart_link_read_chunk(const char *id, uint64_t off, uint32_t len, uint8_t *out, uint32_t *out_len, uint32_t *out_crc);
esp_err_t uart_link_auth_req(const char* ssaid, auth_ssaid_resp_t* out);

#endif /* MAIN_INCLUDE_UART_LINK_H_ */