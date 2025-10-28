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
#include "nvs.h"
#include "mbedtls/md.h"
#include "mbedtls/base64.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

static const char* TAG = "session_mgr";
static uint8_t s_secret[32];

#define NVS_NS       "session"
#define NVS_KEY_OWNER "owner_ssaid"

/* ===== base64url helpers ===== */
static void b64url_encode(const uint8_t* in, size_t n, char* out, size_t cap){
    size_t olen=0;
    unsigned char* tmp = malloc((n*4/3)+8);
    if(!tmp){ if(cap) out[0]=0; return; }
    if(mbedtls_base64_encode(tmp, (n*4/3)+8, &olen, in, n)!=0){ free(tmp); if(cap) out[0]=0; return; }
    size_t k=0;
    for(size_t t=0; t<olen && k+1<cap; t++){
        char c=(char)tmp[t];
        if(c=='+') c='-';
        else if(c=='/') c='_';
        else if(c=='=') continue;
        out[k++]=c;
    }
    out[k]=0;
    free(tmp);
}
static size_t b64url_decode(const char* in, uint8_t* out, size_t cap){
    size_t n=strlen(in);
    size_t pad=(4-(n%4))%4;
    char* tmp=malloc(n+pad+1);
    if(!tmp) return 0;
    for(size_t i=0;i<n;i++) tmp[i]=(in[i]=='-')?'+':(in[i]=='_')?'/':in[i];
    for(size_t p=0;p<pad;p++) tmp[n+p]='=';
    tmp[n+pad]=0;
    size_t olen=0;
    if(mbedtls_base64_decode(out, cap, &olen, (const unsigned char*)tmp, n+pad)!=0) olen=0;
    free(tmp);
    return olen;
}

/* ===== NVS owner helpers ===== */
static esp_err_t owner_load(char* out, size_t out_sz){
    nvs_handle_t h; size_t n=out_sz;
    esp_err_t er = nvs_open(NVS_NS, NVS_READONLY, &h);
    if(er!=ESP_OK) return er;
    er = nvs_get_str(h, NVS_KEY_OWNER, out, &n);
    nvs_close(h);
    return er;
}
static esp_err_t owner_save(const char* s){
    nvs_handle_t h; esp_err_t er=nvs_open(NVS_NS, NVS_READWRITE, &h);
    if(er!=ESP_OK) return er;
    er = nvs_set_str(h, NVS_KEY_OWNER, (s && s[0])? s : "");
    if(er==ESP_OK) er = nvs_commit(h);
    nvs_close(h);
    return er;
}

esp_err_t session_owner_get(char* out, size_t out_sz){ return owner_load(out,out_sz); }
esp_err_t session_owner_clear(void){ return owner_save(""); }

/* ===== secret derive ===== */
esp_err_t session_manager_init(void){
    uint8_t mac[6]={0};
    ESP_ERROR_CHECK(esp_read_mac(mac, ESP_MAC_WIFI_STA));
    for(int i=0;i<32;i++){
        uint8_t m=mac[i%6];
        s_secret[i]=(uint8_t)(m ^ (0xA5 ^ (i*17)));
    }
    ESP_LOGI(TAG, "secret derived");
    return ESP_OK;
}

static esp_err_t hmac_sha256(const uint8_t* msg, size_t n, uint8_t out[32]){
    const mbedtls_md_info_t* mi = mbedtls_md_info_from_type(MBEDTLS_MD_SHA256);
    mbedtls_md_context_t ctx; mbedtls_md_init(&ctx);
    if(mbedtls_md_setup(&ctx, mi, 1)!=0) return ESP_FAIL;
    mbedtls_md_hmac_starts(&ctx, s_secret, sizeof(s_secret));
    mbedtls_md_hmac_update(&ctx, msg, n);
    mbedtls_md_hmac_finish(&ctx, out);
    mbedtls_md_free(&ctx);
    return ESP_OK;
}

/* ===== token issue/verify with SSAID ===== */
esp_err_t session_open_with_ssaid(const char* ssaid, unsigned ttl_ms,
                                  char* out, size_t out_len)
{
    if(!ssaid||!ssaid[0]||!out) return ESP_ERR_INVALID_ARG;

    /* owner binding */
    char cur[64]={0};
    esp_err_t er = owner_load(cur, sizeof(cur));
    if(er==ESP_OK && cur[0]!=0){
        if(strcmp(cur, ssaid)!=0) return ESP_ERR_INVALID_STATE; // owner mismatch
    }else{
        ESP_ERROR_CHECK(owner_save(ssaid));
    }

    uint64_t now_ms = esp_timer_get_time()/1000ULL;
    uint64_t exp_ms = now_ms + ttl_ms;
    uint32_t nonce=0; esp_fill_random(&nonce,sizeof(nonce));

    char payload[192];
    int n = snprintf(payload, sizeof(payload),
        "{\"dev\":%u,\"ssaid\":\"%s\",\"exp\":%llu,\"nonce\":%u}",
        (unsigned)now_ms, ssaid, (unsigned long long)exp_ms, (unsigned)nonce);
    if(n<=0) return ESP_FAIL;

    char p64[256]; b64url_encode((const uint8_t*)payload, n, p64, sizeof(p64));
    uint8_t mac[32]; ESP_ERROR_CHECK(hmac_sha256((const uint8_t*)p64, strlen(p64), mac));
    char s64[256]; b64url_encode(mac, sizeof(mac), s64, sizeof(s64));

    if(strlen(p64)+1+strlen(s64)+1 > out_len) return ESP_ERR_INVALID_SIZE;
    sprintf(out, "%s.%s", p64, s64);
    return ESP_OK;
}

bool session_verify_get_ssaid(const char* token, char* out_ssaid, size_t out_sz){
    if(!token) return false;
    const char* dot=strchr(token,'.'); if(!dot) return false;
    size_t pLen=(size_t)(dot-token); if(pLen>=256) return false;

    char p64[256]; memcpy(p64, token, pLen); p64[pLen]=0;
    uint8_t mac[32], mac2[32];
    if(b64url_decode(dot+1, mac, sizeof(mac))!=32) return false;
    if(hmac_sha256((const uint8_t*)p64, strlen(p64), mac2)!=ESP_OK) return false;
    if(memcmp(mac, mac2, 32)!=0) return false;

    uint8_t payload[192]; size_t plen=b64url_decode(p64, payload, sizeof(payload));
    if(plen==0) return false; payload[plen]=0;

    const char* exp = strstr((char*)payload,"\"exp\":");
    if(!exp) return false;
    uint64_t exp_ms = strtoull(exp+6,NULL,10);
    uint64_t now_ms = esp_timer_get_time()/1000ULL;
    if(now_ms>=exp_ms) return false;

    const char* s1 = strstr((char*)payload,"\"ssaid\":\"");
    if(!s1) return false; s1+=9;
    const char* s2 = strchr(s1,'\"'); if(!s2) return false;
    size_t n = (size_t)(s2 - s1);

    /* owner match (defense-in-depth) */
    char cur[64]={0};
    if(owner_load(cur,sizeof(cur))==ESP_OK && cur[0]){
        if(strncmp(cur, s1, n)!=0 || cur[n]!=0) return false;
    }
    if(out_ssaid && out_sz){
        size_t c = (n<out_sz-1)? n : (out_sz-1);
        memcpy(out_ssaid, s1, c); out_ssaid[c]=0;
    }
    return true;
}
