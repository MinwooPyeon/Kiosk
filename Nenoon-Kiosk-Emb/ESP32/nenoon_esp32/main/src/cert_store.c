/*
 * cert_store.c
 *
 *  Created on: 2025. 10. 26.
 *  Updated on: 2025. 10. 27.
 *      Author: Park Joo Hyun
 */
 
 /*
#include "cert_store.h"
#include <string.h>
#include <ctype.h>
#include <stdio.h>

#include "esp_log.h"
#include "esp_err.h"
#include "esp_http_client.h"
#include "esp_crt_bundle.h"

#include "mbedtls/net_sockets.h"
#include "mbedtls/ssl.h"
#include "mbedtls/ctr_drbg.h"
#include "mbedtls/entropy.h"
#include "mbedtls/x509_crt.h"
#include "mbedtls/pk.h"
#include "mbedtls/sha256.h"

#define TAG "cert_store"

static struct{
	const char* ca_pem;
	uint8_t		pin_spki_sha256[32];
	bool		have_pin;
	bool 		use_crt_bundle;
	bool		inited;
}g_cs = {0};

typedef struct{
	mbedtls_entropy_context 	entropy;
	mbedtls_ctr_drbg_context 	ctr_drbg;
	mbedtls_ssl_context			ssl;
	mbedtls_ssl_config			conf;
	mbedtls_x509_crt			cacert;
	mbedtls_net_context			net;
	bool						cacert_loaded;
	bool						net_connected;
	bool						ssl_setup;
}cs_tls_ctx_t;
*/
/*=====Helpers=====*/
/*
static bool all_zero32(const uint8_t h[32]){
	for(int i=0;i<32;i++){
		if(h[i]!=0)return false;
	}
	return true;
}

static int hex2bytes32(const char* hex, uint8_t out[32]){
	if(!hex) return -1;
	size_t n = strlen(hex);
	if(n == 66 && (hex[0]=='0' && (hex[1]=='x' || hex[1]=='X'))) { hex += 2; n -= 2; }
    if(n != 64) return -1;
    for(int i=0;i<32;i++){
        char c1 = hex[i*2], c2 = hex[i*2+1];
        int v1 = isdigit((unsigned char)c1) ? (c1 - '0') :
                 isxdigit((unsigned char)c1) ? (10 + (tolower((unsigned char)c1)-'a')) : -1;
        int v2 = isdigit((unsigned char)c2) ? (c2 - '0') :
                 isxdigit((unsigned char)c2) ? (10 + (tolower((unsigned char)c2)-'a')) : -1;
        if(v1<0 || v2<0) return -1;
        out[i] = (uint8_t)((v1<<4)|v2);
    }
    return 0;
}

static esp_err_t parse_host_port_from_url(const char* url, char* host, size_t host_sz, int* port){
    if(!url || !host || !port) return ESP_ERR_INVALID_ARG;
    const char* p = strstr(url, "://");
    const char* h = p ? (p+3) : url;
    const char* slash = strchr(h, '/');
    const char* host_end = slash ? slash : (h + strlen(h));
    const char* colon = NULL;
    for(const char* q=h; q<host_end; ++q){ if(*q==':'){ colon=q; break; } }
    if(colon){
        size_t hlen = (size_t)(colon - h);
        if(hlen==0 || hlen >= host_sz) return ESP_ERR_INVALID_ARG;
        memcpy(host, h, hlen); host[hlen]=0;
        int pnum = atoi(colon+1);
        *port = (pnum>0 && pnum<65536)? pnum : 443;
    }else{
        size_t hlen = (size_t)(host_end - h);
        if(hlen==0 || hlen >= host_sz) return ESP_ERR_INVALID_ARG;
        memcpy(host, h, hlen); host[hlen]=0;
        *port = 443;
    }
    return ESP_OK;
}

static int spki_sha256_from_crt(const mbedtls_x509_crt* crt, uint8_t out32[32]){
	
    
    
    uint8_t tmp[1024]; // 보통 256~512바이트면 충분. 키 종류/길이에 따라 조정
    int len = mbedtls_pk_write_pubkey_der((mbedtls_pk_context*)&crt->pk, tmp, sizeof(tmp));
    if(len <= 0) return -1;
    
    mbedtls_sha256(&tmp[sizeof(tmp)-len], (size_t)len, out32, 0 *//*is224*//*);
    return 0;
}
*/
/*=====Preverify Function=====*/
/*
static esp_err_t cs_tls_init(cs_tls_ctx_t* c){
	if(!c) return ESP_ERR_INVALID_ARG;
	memset(c, 0, sizeof(*c));
	
	mbedtls_entropy_init(&c->entropy);
	mbedtls_ctr_drbg_init(&c->ctr_drbg);
	mbedtls_ssl_init(&c->ssl);
	mbedtls_ssl_config_init(&c->conf);
	mbedtls_x509_crt_init(&c->cacert);
	mbedtls_net_init(&c->net);
	
	const char* pers = "cert_store_spki";
	int ret = mbedtls_ctr_drbg_seed(&c->ctr_drbg, mbedtls_entropy_func, &c->entropy,(const unsigned char*)pers, strlen(pers));
	
	if(ret!=0){
		ESP_LOGE(TAG, "ctr_drbg_seed: -0x%04X", -ret);
		return ESP_FAIL;
	}
	return ESP_OK;
}

static esp_err_t cs_tls_load_trust(cs_tls_ctx_t* c){
    if(!c) return ESP_ERR_INVALID_ARG;

    if(g_cs.ca_pem){
        int ret = mbedtls_x509_crt_parse(&c->cacert,
                        (const unsigned char*)g_cs.ca_pem, strlen(g_cs.ca_pem)+1);
        if(ret < 0){
            ESP_LOGE(TAG, "x509_crt_parse: -0x%04X", -ret);
            return ESP_FAIL;
        }
        c->cacert_loaded = true;
        return ESP_OK;
    }

    if(g_cs.use_crt_bundle){
  
        ESP_LOGW(TAG, "Preverify + bundle 조합은 미지원. PEM 루트를 제공해 주세요.");
        return ESP_ERR_NOT_SUPPORTED;
    }

    ESP_LOGE(TAG, "No CA for preverify");
    return ESP_ERR_INVALID_STATE;
}

static esp_err_t cs_tls_setup_ssl(cs_tls_ctx_t* c, const char* host){
    if(!c || !host) return ESP_ERR_INVALID_ARG;

    int ret = mbedtls_ssl_config_defaults(&c->conf,
                MBEDTLS_SSL_IS_CLIENT, MBEDTLS_SSL_TRANSPORT_STREAM,
                MBEDTLS_SSL_PRESET_DEFAULT);
    if(ret!=0){
        ESP_LOGE(TAG, "ssl_config_defaults: -0x%04X", -ret);
        return ESP_FAIL;
    }

    mbedtls_ssl_conf_authmode(&c->conf, MBEDTLS_SSL_VERIFY_REQUIRED);
    if(c->cacert_loaded){
        mbedtls_ssl_conf_ca_chain(&c->conf, &c->cacert, NULL);
    }
    mbedtls_ssl_conf_rng(&c->conf, mbedtls_ctr_drbg_random, &c->ctr_drbg);

    if((ret=mbedtls_ssl_setup(&c->ssl, &c->conf))!=0){
        ESP_LOGE(TAG, "ssl_setup: -0x%04X", -ret);
        return ESP_FAIL;
    }
    c->ssl_setup = true;

    if((ret=mbedtls_ssl_set_hostname(&c->ssl, host))!=0){
        ESP_LOGE(TAG, "set_hostname: -0x%04X", -ret);
        return ESP_FAIL;
    }
    return ESP_OK;
}

static esp_err_t cs_tls_connect_tcp(cs_tls_ctx_t* c, const char* host, int port){
    if(!c || !host) return ESP_ERR_INVALID_ARG;

    char sport[8];
    snprintf(sport, sizeof(sport), "%d", port);

    int ret = mbedtls_net_connect(&c->net, host, sport, MBEDTLS_NET_PROTO_TCP);
    if(ret!=0){
        ESP_LOGE(TAG, "net_connect(%s:%s): -0x%04X", host, sport, -ret);
        return ESP_FAIL;
    }
    c->net_connected = true;

    mbedtls_ssl_set_bio(&c->ssl, &c->net, mbedtls_net_send, mbedtls_net_recv, NULL);
    return ESP_OK;
}

static esp_err_t cs_tls_handshake(cs_tls_ctx_t* c){
    if(!c) return ESP_ERR_INVALID_ARG;
    int ret = mbedtls_ssl_handshake(&c->ssl);
    if(ret!=0){
        ESP_LOGE(TAG, "ssl_handshake: -0x%04X", -ret);
        return ESP_FAIL;
    }
    return ESP_OK;
}

static esp_err_t cs_tls_verify_chain(cs_tls_ctx_t* c){
    if(!c) return ESP_ERR_INVALID_ARG;
    uint32_t flags = mbedtls_ssl_get_verify_result(&c->ssl);
    if(flags != 0){
        ESP_LOGE(TAG, "cert verify failed: flags=0x%08" PRIx32, flags);
        return ESP_FAIL;
    }
    return ESP_OK;
}

static esp_err_t cs_tls_get_peer_spki_sha256(cs_tls_ctx_t* c, uint8_t out32[32]){
    if(!c || !out32) return ESP_ERR_INVALID_ARG;

    const mbedtls_x509_crt* peer = mbedtls_ssl_get_peer_cert(&c->ssl);
    if(!peer){
        ESP_LOGE(TAG, "peer cert is NULL");
        return ESP_FAIL;
    }
    if(spki_sha256_from_crt(peer, out32) != 0){
        ESP_LOGE(TAG, "SPKI write/hash failed");
        return ESP_FAIL;
    }
    return ESP_OK;
}

static void cs_tls_cleanup(cs_tls_ctx_t* c){
    if(!c) return;
    if(c->ssl_setup)        mbedtls_ssl_close_notify(&c->ssl);
    if(c->net_connected)    mbedtls_net_free(&c->net);
    mbedtls_ssl_free(&c->ssl);
    mbedtls_ssl_config_free(&c->conf);
    mbedtls_x509_crt_free(&c->cacert);
    mbedtls_ctr_drbg_free(&c->ctr_drbg);
    mbedtls_entropy_free(&c->entropy);
}
*/
/*=====Public API=====*/
/*
void cert_store_init(const cert_store_cfg_t* cfg){
    memset(&g_cs, 0, sizeof(g_cs));
    if(cfg){
        g_cs.ca_pem        = cfg->ca_pem;
        g_cs.use_crt_bundle= cfg->use_crt_bundle;
        g_cs.have_pin      = !all_zero32(cfg->pin_spki_sha256);
        if(g_cs.have_pin) memcpy(g_cs.pin_spki_sha256, cfg->pin_spki_sha256, 32);
    }
    g_cs.inited = true;
    ESP_LOGI(TAG, "init: ca=%s, bundle=%d, pin=%s",
             g_cs.ca_pem? "pem" : "none", g_cs.use_crt_bundle,
             g_cs.have_pin? "yes":"no");
}

esp_err_t cert_store_attach(esp_http_client_config_t* http_cfg){
	if(!g_cs.inited)	return ESP_ERR_INVALID_STATE;
	if(!http_cfg)		return ESP_ERR_INVALID_STATE;
	
	if(g_cs.ca_pem){
		http_cfg->cert_pem = g_cs.ca_pem;
	}else if(g_cs.use_crt_bundle){
		http_cfg->crt_bundle_attach = esp_crt_bundle_attach;
	}else{
		ESP_LOGW(TAG, "No CA Provided; TLS verify will fail unless server cert is otherwise trusted");
	}
	
	return ESP_OK;
}

esp_err_t cert_store_preverify_spki(const char* url){
    if(!g_cs.inited)   return ESP_ERR_INVALID_STATE;
    if(!g_cs.have_pin) return ESP_OK; 

    char host[128]; int port = 443;
    esp_err_t er = parse_host_port_from_url(url, host, sizeof(host), &port);
    if(er != ESP_OK){
        ESP_LOGE(TAG, "URL parse failed: %s", url);
        return er;
    }

    cs_tls_ctx_t ctx;
    uint8_t spki_hash[32];

    
    if((er = cs_tls_init(&ctx))                       != ESP_OK) goto fail;
    if((er = cs_tls_load_trust(&ctx))                 != ESP_OK) goto fail;
    if((er = cs_tls_setup_ssl(&ctx, host))            != ESP_OK) goto fail;
    if((er = cs_tls_connect_tcp(&ctx, host, port))    != ESP_OK) goto fail;
    if((er = cs_tls_handshake(&ctx))                  != ESP_OK) goto fail;
    if((er = cs_tls_verify_chain(&ctx))               != ESP_OK) goto fail;
    if((er = cs_tls_get_peer_spki_sha256(&ctx, spki_hash)) != ESP_OK) goto fail;

    
    if(memcmp(spki_hash, g_cs.pin_spki_sha256, 32) != 0){
        ESP_LOGE(TAG, "SPKI PIN mismatch");
        cs_tls_cleanup(&ctx);
        return ESP_ERR_NOT_FOUND;  
    }

    ESP_LOGI(TAG, "SPKI PIN verified for %s:%d", host, port);
    cs_tls_cleanup(&ctx);
    return ESP_OK;

fail:
    cs_tls_cleanup(&ctx);
    return er == ESP_OK ? ESP_FAIL : er;
}
*/