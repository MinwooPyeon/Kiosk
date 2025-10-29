/*
 * http_srv.c
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */

#include "http_srv.h"
#include "session_mgr.h"
#include "uart_link.h"
#include "metrics.h"
#include "ratelimit.h"

#include "esp_http_server.h"
#include "esp_log.h"

#include <string.h>
#include <stdio.h>
#include <stdlib.h>
#include <inttypes.h>

static const char* TAG = "http_srv";
static httpd_handle_t s_srv;
static ratelimit_t* s_rl_media;
static ratelimit_t* s_rl_chunk;

static const char* reason_phrase(int code){
    switch(code){
        case 200: return "OK";
        case 204: return "No Content";
        case 400: return "Bad Request";
        case 401: return "Unauthorized";
        case 404: return "Not Found";
        case 405: return "Method Not Allowed";
        case 409: return "Conflict";
        case 414: return "URI Too Long";
        case 422: return "Unprocessable Entity";
        case 429: return "Too Many Requests";
        case 500: return "Internal Server Error";
        case 502: return "Bad Gateway";
        case 504: return "Gateway Timeout";
        default:  return "Error";
    }
}

static esp_err_t send_error(httpd_req_t* r, int code, const char* msg){
    char status[48];
    snprintf(status, sizeof(status), "%d %s", code, reason_phrase(code));
    httpd_resp_set_status(r, status);
    httpd_resp_set_type(r, "text/plain");
    return httpd_resp_sendstr(r, msg ? msg : "");
}

static esp_err_t json_get_str(const char*  js, const char* key, char* out, size_t out_sz){
	char pat[32];
	snprintf(pat, sizeof(pat), "\"%s\":\"", key);
	
	const char* k = strstr(js, pat);
	if(!k) return ESP_ERR_NOT_FOUND;
	
	const char* s = k + strlen(pat);
	const char* e = strchr(s, '\"');
	if(!e) return ESP_ERR_NOT_FOUND;
	
	size_t n = (size_t)(e - s);
	if(n+1>out_sz) return ESP_ERR_INVALID_SIZE;
	memcpy(out, s, n);
	out[n] = 0;
	
	return ESP_OK;
}

static bool auth_ok(httpd_req_t* r){
    char tok[768];
    if(httpd_req_get_hdr_value_str(r, "Authorization", tok, sizeof(tok))!=ESP_OK) return false;
    const char* b = strstr(tok, "Bearer ");
    if(!b) return false;
    char ssaid[64];
    return session_verify_get_ssaid(b+7, ssaid, sizeof(ssaid));
}

static esp_err_t h_session_open(httpd_req_t* r){
    char body[256]; int n=httpd_req_recv(r, body, sizeof(body)-1);
    if(n<0) n=0; 
    body[n]=0;

    char ssaid[64];
    if(json_get_str(body,"ssaid", ssaid, sizeof(ssaid))!=ESP_OK)
        return send_error(r, 400, "ssaid missing");

    // STM32에 SSAID 판단 요청
    auth_ssaid_resp_t resp={0};
    esp_err_t er = uart_link_auth_req(ssaid, &resp);
    if(er!=ESP_OK) return send_error(r, 502, "bridge error");

    if(resp.result!=1){
        int code = HTTPD_403_FORBIDDEN;
        if(strcmp(resp.reason,"usb_missing")==0) code=409;
        else if(strcmp(resp.reason,"license_invalid")==0) code=422;
        return send_error(r, code, reason_phrase(code));
    }

    // 허용 → 토큰 발급(SSAAD 포함)
    char token[256];
    if(session_open_with_ssaid(ssaid, 10*60*1000, token, sizeof(token))!=ESP_OK)
    	return send_error(r, 500, "issue fail");

    char json[320];
    snprintf(json,sizeof(json),"{\"token\":\"%s\",\"ttlMs\":600000}", token);
    httpd_resp_set_type(r,"application/json");
    return httpd_resp_sendstr(r, json);
}

// v1/usb/status
static esp_err_t h_usb_status(httpd_req_t* r){
	if(!auth_ok(r)) return send_error(r, 401, reason_phrase(401));
	char json[64];
	snprintf(json, sizeof(json), "{\"attached\":%s}", uart_link_usb_attached()?"true":"false");
	httpd_resp_set_type(r, "application/json");
	return httpd_resp_sendstr(r, json);
}

// /v1/media
static esp_err_t h_media_list(httpd_req_t* r){
	if(!auth_ok(r)) return send_error(r, 401, reason_phrase(401));
	if(ratelimit_take_nowait(s_rl_media, 1)!=ESP_OK) return send_error(r, 429, reason_phrase(429));
	
	media_index_t idx;
	if(uart_link_get_index(&idx)!= ESP_OK) return httpd_resp_send_err(r, HTTPD_500_INTERNAL_SERVER_ERROR, "index");
	httpd_resp_set_type(r, "application/json");
	httpd_resp_sendstr(r, ",\"gen\":");
	
	char num[16];
	snprintf(num, sizeof(num), "%" PRIu32, idx.gen);
	httpd_resp_sendstr_chunk(r, num);
	httpd_resp_sendstr_chunk(r, ",\"files\":[");
	
	for(uint32_t i=0;i<idx.count;i++){
        char ent[512];
        snprintf(ent, sizeof(ent),
            "%s{\"id\":\"%s\",\"name\":\"%s\",\"size\":%llu,\"sha256_16\":\"%s\",\"mime\":\"%s\"}",
            (i?",":""), idx.items[i].id, idx.items[i].name,
            (unsigned long long)idx.items[i].size, idx.items[i].sha16, idx.items[i].mime);
        httpd_resp_sendstr_chunk(r, ent);
    }
    
    httpd_resp_sendstr_chunk(r, "]}");
    metrics_inc_ok();
    return httpd_resp_sendstr_chunk(r, NULL);
}

// /v1/media/{id}/chunk?off=&len= 
static esp_err_t h_media_chunk(httpd_req_t* r){
	if(!auth_ok(r)) return send_error(r, 401, reason_phrase(401));
	if(ratelimit_take_nowait(s_rl_media, 1)!=ESP_OK) return send_error(r, 429, reason_phrase(429));
	
	const char* uri = r->uri;
	const char* p 	= uri+strlen("/v1/media");
	const char* q	= q=strstr(p,"/chunk"); if(!q) return httpd_resp_send_err(r, HTTPD_404_NOT_FOUND, "bad id");
    char id[128]; 
    size_t idn		=(size_t)(q-p); if(idn>=sizeof(id)) return httpd_resp_send_err(r, HTTPD_414_URI_TOO_LONG, "id too long");
    memcpy(id,p,idn); id[idn]=0;
    
    char qbuf[64]; httpd_req_get_url_query_str(r, qbuf, sizeof(qbuf));
    char tmp[16]; uint64_t off=0; uint32_t len=65536;
    if(httpd_query_key_value(qbuf,"off",tmp,sizeof(tmp))==ESP_OK) off=strtoull(tmp,NULL,10);
    if(httpd_query_key_value(qbuf,"len",tmp,sizeof(tmp))==ESP_OK) len=(uint32_t)strtoul(tmp,NULL,10);
    if(len==0 || len>256*1024) len=64*1024;

    uint8_t* buf = malloc(len); if(!buf) return httpd_resp_send_err(r, HTTPD_500_INTERNAL_SERVER_ERROR, "oom");
    uint32_t got=0, crc=0;
    esp_err_t er = uart_link_read_chunk(id, off, len, buf, &got, &crc);
    if(er!=ESP_OK){ free(buf); metrics_inc_err(); return httpd_resp_send_err(r, HTTPD_500_INTERNAL_SERVER_ERROR, "read fail"); }

    char h[16]; snprintf(h,sizeof(h),"%08" PRIx32, crc);
    httpd_resp_set_type(r, "application/octet-stream");
    httpd_resp_set_hdr(r, "X-CRC32", h);
    esp_err_t ret = httpd_resp_send(r, (const char*)buf, got);
    metrics_add_bytes(0, got);
    free(buf);
    return ret;
}

// /v1/events
static esp_err_t h_events(httpd_req_t* r){
	if(!auth_ok(r)) return httpd_resp_send_err(r, HTTPD_401_UNAUTHORIZED, "unauthorized");
    httpd_resp_set_type(r, "text/event-stream");
    httpd_resp_set_hdr(r, "Cache-Control", "no-cache");
    httpd_resp_set_hdr(r, "Connection", "keep-alive");
    httpd_resp_sendstr_chunk(r, "event: USB_ATTACHED\n");
    httpd_resp_sendstr_chunk(r, uart_link_usb_attached() ? "data: {\"attached\":true}\n\n" : "data: {\"attached\":false}\n\n" );
    metrics_sse_clients_set(1);
    for(;;){
        vTaskDelay(pdMS_TO_TICKS(30000));
        if(httpd_resp_sendstr_chunk(r, ": keep-alive\n\n")!=ESP_OK) break;
    }
    metrics_sse_clients_set(0);
    return httpd_resp_sendstr_chunk(r, NULL);
}

esp_err_t http_srv_start(void){
    ratelimit_config_t c1={.capacity=8,.refill_tokens=2,.period_ms=1000,.start_full=1};
    ratelimit_config_t c2={.capacity=4,.refill_tokens=1,.period_ms=1000,.start_full=1};
    ratelimit_create(&c1,&s_rl_media);
    ratelimit_create(&c2,&s_rl_chunk);

    httpd_config_t cfg=HTTPD_DEFAULT_CONFIG();
    cfg.server_port=80; cfg.lru_purge_enable=true;
    ESP_ERROR_CHECK(httpd_start(&s_srv, &cfg));

    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/session/open", .method=HTTP_POST, .handler=h_session_open });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/usb/status",  .method=HTTP_GET,  .handler=h_usb_status  });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/media",        .method=HTTP_GET,  .handler=h_media_list  });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/health",       .method=HTTP_GET,  .handler=h_events      }); // 임시: /v1/health 대신 /v1/events 아래서 SSE
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/events",       .method=HTTP_GET,  .handler=h_events      });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/media/",       .method=HTTP_GET,  .handler=h_media_chunk });

    ESP_LOGI(TAG, "HTTP server started");
    return ESP_OK;
}