/*
 * uart_link.h
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */

#ifndef MAIN_INCLUDE_UART_LINK_H_
#define MAIN_INCLUDE_UART_LINK_H_

#include <stdint.h>
#include <stdbool.h>
#include "esp_err.h"
#include "frame.h"

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    char     id[64];
    char     name[64];
    uint32_t size;
    uint32_t index;
} media_item_t;

typedef struct {
    uint32_t      gen;
    uint32_t      count;
    media_item_t* items; // malloc된 배열(호출자 소유)
} media_index_t;


/* Public APIs */
esp_err_t uart_link_init(void);
void      uart_link_set_sniff(bool on);
bool      uart_link_usb_attached(void);

esp_err_t uart_link_lic_mgr_login(const char *id, const char *pw, bool *ok);
esp_err_t uart_link_lic_issue(const char *app, const char *to, char *out_lic, size_t out_sz);
esp_err_t uart_link_lic_validate(const char *lic, bool *ok);
esp_err_t uart_link_lic_get_jwt(const char *lic, char *out_jwt, size_t out_sz);
esp_err_t uart_link_get_index(media_index_t *out);
esp_err_t uart_link_read_chunk(const char *id, uint64_t off, uint32_t len,
                               uint8_t *out, uint32_t *out_len, uint32_t *out_crc);

#ifdef __cplusplus
}
#endif

#endif /* MAIN_INCLUDE_UART_LINK_H_ */