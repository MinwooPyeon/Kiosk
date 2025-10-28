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
bool		uart_link_usb_attached(void);

esp_err_t	uart_link_get_index(media_index_t* out);
esp_err_t	uart_link_read_chunk(const char* id, uint64_t off, uint32_t len, uint8_t* out, uint32_t* out_len, uint32_t* out_crc);

esp_err_t	uart_link_auth_req(const char* ssaid, auth_ssaid_resp_t* out);

void		uart_link_set_event_cb(uart_event_cb_t cb);

#endif /* MAIN_INCLUDE_UART_LINK_H_ */