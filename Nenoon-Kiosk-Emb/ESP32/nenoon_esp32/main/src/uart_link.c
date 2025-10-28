/*
 * uart_link.c
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */

#include "uart_link.h"
#include "esp_log.h"
#include <string.h>
#include <stdlib.h>

static const char* TAG = "uart_link";

static uart_event_cb_t s_cb;
static bool s_attached = true;
static media_index_t s_idx;

static uint32_t crc32_soft(const uint8_t* d, size_t n){
	uint32_t c = 0xFFFFFFFFu;
	for(size_t i = 0;i < n;i++){
		c ^= d[i];
		for(int b = 0;b < 8;b++) c = (c>>1) ^ (0xED888320u & (-(int)(c&1)));
	}
	return ~c;
}

esp_err_t uart_link_init(void){
#if CONFIG_UART_LINK_MOCK
	s_idx.gen 	= 1;
	s_idx.count = 3;
	s_idx.items = calloc(s_idx.count, sizeof(media_item_t));
	
	memcpy(&s_idx.items[0], &(media_item_t){ .id="3f2a9c1e-file.mp4", .name="file.mp4", .size=734003200ULL, .sha16="3f2a9c1e7b4c9912", .mime="video/mp4"}, sizeof(media_item_t));
    memcpy(&s_idx.items[1], &(media_item_t){ .id="7aa0d9a2-img.jpg",  .name="img.jpg",  .size=1048576ULL,   .sha16="7aa0d9a2c3d44b10", .mime="image/jpeg"}, sizeof(media_item_t));
    memcpy(&s_idx.items[2], &(media_item_t){ .id="8bb0a1f3-banner.png",.name="banner.png",.size=512000ULL, .sha16="8bb0a1f3f0cc9911", .mime="image/png"}, sizeof(media_item_t));
    ESP_LOGI(TAG, "MOCK index ready");
#else
	//TODO UART + Frame Combine
#endif
	if(s_cb) s_cb("USB_ATTACHED", s_idx.count);
	return ESP_OK;
}

void uart_link_set_event_cb(uart_event_cb_t cb) {s_cb = cb;}
bool uart_link_usb_attached(void) {return s_attached;}

esp_err_t uart_link_get_index(media_index_t* out){
	if(!out) return ESP_ERR_INVALID_ARG;
	*out = s_idx;
	return ESP_OK;
}

esp_err_t uart_link_read_chunk(const char *id, uint64_t off, uint32_t len, uint8_t *out, uint32_t *out_len, uint32_t *out_crc){
	if(!id||!out||!out_len||!out_crc) return ESP_ERR_INVALID_ARG;
	
	const media_item_t* f = NULL;
	for(uint32_t i = 0;i<s_idx.count;i++){
		if(strcmp(s_idx.items[i].id, id) == 0){
			f = &s_idx.items[i];
			break;
		}
	}
	if(!f) return ESP_ERR_NOT_FOUND;
	if(off >= f->size) {*out_len = 0; *out_crc = 0; return ESP_OK;}
	
	uint32_t can = (uint32_t)((f->size - off) > len ? len : (f->size - off));
	
	for(uint32_t i = 0;i<can;i++) out[i] = (uint8_t)((off+i)^0x5A);
	*out_len = can;
	*out_crc = crc32_soft(out, can);
	
	return ESP_OK;
}

esp_err_t uart_link_auth_req(const char* ssaid, auth_ssaid_resp_t* out){
	 if(!ssaid||!out) return ESP_ERR_INVALID_ARG;
#if CONFIG_UART_LINK_MOCK
    static char owner[64]={0};
    if(!s_attached){ strcpy(out->reason,"usb_missing"); out->result=0; out->owner[0]=0; return ESP_OK; }
    if(owner[0]==0){ strncpy(owner, ssaid, sizeof(owner)-1); out->result=1; strcpy(out->owner, owner); out->reason[0]=0; return ESP_OK; }
    if(strcmp(owner, ssaid)==0){ out->result=1; strcpy(out->owner, owner); out->reason[0]=0; return ESP_OK; }
    strcpy(out->owner, owner); strcpy(out->reason,"owner_mismatch"); out->result=0; return ESP_OK;
#else
    // TODO: FRAME_AUTH_SSAID_REQ/RESP 교환 구현
    return ESP_ERR_NOT_SUPPORTED;
#endif
}