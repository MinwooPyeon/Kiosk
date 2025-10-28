/*
 * auth_adapter.c
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */

#include "session_mgr.h"

#include "esp_log.h"
#include "esp_timer.h"
#include "esp_system.h"
#include "esp_mac.h"
#include "esp_random.h"

#include "nvs_flash.h"
#include "mbedtls/md.h"
#include "mbedtls/base64.h"

#include <string.h>
#include <stdio.h>
#include <stdlib.h>

static const char* TAG = "session_mgr";
static uint8_t s_secret[32];

static void b64url_encode(const uint8_t* in, size_t n, char* out, size_t cap){
	static const char* T = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-_";
	size_t i = 0, j = 0;
	while(i < n && j + 4 < cap){
		uint32_t v = (in[i++]<<16);
		if(i<=n) v|=(i < n ? in[i] : 0) << 8;
		if(i<n) i++;
		if(i<=n) v|=(i < n ? in[i] : 0);
	}
	
	size_t olen = 0;
	size_t dlen = (n * 4 / 3) + 8;
	unsigned char* tmp = malloc(dlen);
	if(!tmp){
		if(cap) out[0] = 0;
		return;
	}
	mbedtls_base64_encode(tmp, dlen, &olen, in, n);
	
	size_t k = 0;
	for(size_t t = 0;t < olen && k + 1 < cap;t++){
		char c = (char)tmp[t];
		if(c=='+') c= '-';
		else if(c=='/')c = '_';
		else if(c=='=') continue;
		out[k++] = c;
	}
	
	out[k] = 0;
	free(tmp);
}

static size_t b64url_decode(const char* in, uint8_t* out, size_t cap){
	size_t 	n 		= strlen(in);
	size_t 	pad 	= (4 - (n % 4)) % 4;
	char*	tmp		= malloc(n + pad + 1);
	
	for(size_t i = 0;i < n;i++){ tmp[i] = (in[i] == '-') ? '+' : (in[i] == '_') ? '/' : in[i]; }
    for(size_t p = 0;p < pad;p++) tmp[n + p]='=';
    tmp[n + pad]=0;
    size_t olen=0;
    if(mbedtls_base64_decode(out, cap, &olen, (const unsigned char*)tmp, n+pad)!=0) olen=0;
    free(tmp);
    return olen;	
}

esp_err_t session_manager_init(void){
	 uint8_t mac[6] = {0};
     esp_err_t er = esp_read_mac(mac, ESP_MAC_WIFI_STA);
     if (er != ESP_OK) return er;
     for (int i = 0; i < 32; i++){
         uint8_t m = mac[i % 6];
         s_secret[i] = (uint8_t)(m ^ (0xA5 ^ (i * 17)));
     }
     ESP_LOGI(TAG, "secret derived");
     return ESP_OK;
}

static esp_err_t hmac_sha256(const uint8_t* msg, size_t n, uint8_t out[32]){
	const mbedtls_md_info_t* mi = mbedtls_md_info_from_type(MBEDTLS_MD_SHA256);
	mbedtls_md_context_t ctx;
	mbedtls_md_init(&ctx);
	
	if(mbedtls_md_setup(&ctx, mi , 1) != 0) return ESP_FAIL;
	mbedtls_md_hmac_starts(&ctx, s_secret, sizeof(s_secret));
	mbedtls_md_hmac_update(&ctx, msg, n);
	mbedtls_md_hmac_finish(&ctx, out);
	mbedtls_md_free(&ctx);
	
	return ESP_OK;
}

 esp_err_t session_open(unsigned ttl_ms, char* out, size_t out_len){
	 if(!out || out_len < 32) return ESP_ERR_INVALID_ARG;
	 
	 uint64_t 	now_ms	= esp_timer_get_time() / 1000ULL;
	 uint64_t 	exp_ms	= now_ms + ttl_ms;
	 char		payload[160];
	 uint32_t nonce = 0;
     esp_fill_random(&nonce, sizeof(nonce));
     int n = snprintf(payload, sizeof(payload),
         "{\"dev\":%u,\"exp\":%llu,\"nonce\":%u}",
         (unsigned)now_ms, (unsigned long long)exp_ms, (unsigned)nonce);
	 
	 if(n<=0) return ESP_FAIL;
    char p64[256];
    b64url_encode((const uint8_t*)payload, n, p64, sizeof(p64));
    
    uint8_t mac[32];
    ESP_ERROR_CHECK(hmac_sha256((const uint8_t*)p64, strlen(p64), mac));
    
    char s64[256];
    b64url_encode(mac, sizeof(mac), s64, sizeof(s64));
    
    if(strlen(p64)+1+strlen(s64)+1 > out_len) return ESP_ERR_INVALID_SIZE;
    sprintf(out, "%s.%s", p64, s64);
    
    return ESP_OK; 
 }
 
 bool session_verify(const char* token){
	if(!token) return false;
    const char* dot = strchr(token, '.');
    if(!dot) return false;
    size_t pLen = (size_t)(dot-token);
    char p64[256];
    if(pLen>=sizeof(p64)) return false;
    memcpy(p64, token, pLen);
    p64[pLen]=0;

    uint8_t mac[32], mac2[32];
    size_t macLen = b64url_decode(dot+1, mac, sizeof(mac));
    if(macLen!=32) return false;
    if(hmac_sha256((const uint8_t*)p64, strlen(p64), mac2)!=ESP_OK) return false;
    if(memcmp(mac, mac2, 32)!=0) return false;

    uint8_t payload[192]; size_t plen = b64url_decode(p64, payload, sizeof(payload));
    if(plen==0) return false;
    payload[plen]=0;
    const char* exp = strstr((char*)payload, "\"exp\":");
    if(!exp) return false;
    uint64_t exp_ms = strtoull(exp+6, NULL, 10);
    uint64_t now_ms = esp_timer_get_time()/1000ULL;
    return now_ms < exp_ms;
 }
 
 void session_close_all(void);