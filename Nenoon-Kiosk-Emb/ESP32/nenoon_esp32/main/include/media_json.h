/*
 * media_json.h
 *
 *  Created on: 2025. 11. 10.
 *      Author: SSAFY
 */

#ifndef MAIN_INCLUDE_MEDIA_JSON_H_
#define MAIN_INCLUDE_MEDIA_JSON_H_

#include <stddef.h>
#include <stdint.h>
#include "uart_link.h"  // media_index_t, media_item_t

#ifdef __cplusplus
extern "C" {
#endif

typedef struct {
    const char* js;
    size_t      js_len;
} json_view_t;

void     media_json_parse_index(const json_view_t* v, media_index_t* out);
uint32_t media_json_parse_gen_or(const json_view_t* v, uint32_t defv);

#ifdef __cplusplus
}
#endif




#endif /* MAIN_INCLUDE_MEDIA_JSON_H_ */
