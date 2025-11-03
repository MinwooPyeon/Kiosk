/*
 * http_srv.c
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */

// main/src/http_srv.c  (STM32 프레임에 맞춘 버전)
#include "http_srv.h"
#include "esp_err.h"
#include "session_mgr.h"
#include "uart_link.h"
#include "metrics.h"
#include "ratelimit.h"

#include "esp_http_server.h"
#include "esp_log.h"
#include <string.h>
#include <stdio.h>
#include <stdlib.h>

static const char* TAG = "http_srv";
static httpd_handle_t s_srv;
static ratelimit_t* s_rl_lic;
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

static bool auth_ok(httpd_req_t* r){
    char tok[512];
    if(httpd_req_get_hdr_value_str(r, "Authorization", tok, sizeof(tok))!=ESP_OK) return false;
    const char* b = strstr(tok, "Bearer ");
    if(!b) return false;
    char ssaid[64];
    return session_verify_get_ssaid(b+7, ssaid, sizeof(ssaid));
}

/* ====== 1) 라이선스 매니저 로그인 → STM32 FRAME_LIC_MGR_LOGIN ====== */
/* POST /v1/lic/login  { "id":"admin", "pw":"admin123" } */
static esp_err_t h_lic_login(httpd_req_t* r){
    char body[128]; int n = httpd_req_recv(r, body, sizeof(body)-1);
    if(n < 0) n = 0;
    body[n] = 0;

    char *id = strstr(body, "\"id\"");
    char *pw = strstr(body, "\"pw\"");
    if(!id || !pw) return send_error(r, 400, "id/pw missing");

    char idv[32]={0}, pwv[32]={0};
    sscanf(id, "\"id\":\"%31[^\"]\"", idv);
    sscanf(pw, "\"pw\":\"%31[^\"]\"", pwv);

    bool ok = false;
    if(uart_link_lic_mgr_login(idv, pwv, &ok) != ESP_OK)
        return send_error(r, 502, "bridge error");
    if(!ok) return send_error(r, 401, "login fail");

    httpd_resp_set_type(r, "application/json");
    return httpd_resp_sendstr(r, "{\"result\":1}");
}

/* ====== 2) 라이선스 발급 → STM32 FRAME_LIC_ISSUE ====== */
/* POST /v1/lic/issue  { "app":"kiosk", "to":"ssafy-user" } */
static esp_err_t h_lic_issue(httpd_req_t* r){
    if(!auth_ok(r)) return send_error(r, 401, "unauthorized");
    char body[160]; int n = httpd_req_recv(r, body, sizeof(body)-1);
    if(n<0) n=0;
    body[n]=0;

    char app[32]={0}, to[64]={0};
    sscanf(strstr(body,"\"app\""), "\"app\":\"%31[^\"]\"", app);
    sscanf(strstr(body,"\"to\""),  "\"to\":\"%63[^\"]\"",  to);

    char lic[128];
    if(uart_link_lic_issue(app, to, lic, sizeof(lic)) != ESP_OK)
        return send_error(r, 502, "bridge error");

    char json[160];
    snprintf(json, sizeof(json), "{\"license\":\"%s\"}", lic);
    httpd_resp_set_type(r, "application/json");
    return httpd_resp_sendstr(r, json);
}

/* ====== 3) 라이선스 검증 → STM32 FRAME_LIC_VALIDATE ====== */
/* POST /v1/lic/validate { "license":"LIC_..." } */
static esp_err_t h_lic_validate(httpd_req_t* r){
    char body[128]; int n = httpd_req_recv(r, body, sizeof(body)-1);
    if(n<0) n=0; 
    body[n]=0;

    char lic[96]={0};
    sscanf(strstr(body,"\"license\""), "\"license\":\"%95[^\"]\"", lic);

    bool ok = false;
    if(uart_link_lic_validate(lic, &ok) != ESP_OK)
        return send_error(r, 502, "bridge error");

    httpd_resp_set_type(r, "application/json");
    if(ok) return httpd_resp_sendstr(r, "{\"valid\":true}");
    else   return httpd_resp_sendstr(r, "{\"valid\":false}");
}

/* ====== 4) 라이선스 → JWT 요청 → STM32 FRAME_LIC_GET_JWT ====== */
/* POST /v1/lic/jwt { "license":"LIC_..." } */
static esp_err_t h_lic_jwt(httpd_req_t* r){
    char body[128]; int n = httpd_req_recv(r, body, sizeof(body)-1);
    if(n<0) n=0;
    body[n]=0;

    char lic[96]={0};
    sscanf(strstr(body,"\"license\""), "\"license\":\"%95[^\"]\"", lic);

    char jwt[160];
    if(uart_link_lic_get_jwt(lic, jwt, sizeof(jwt)) != ESP_OK)
        return send_error(r, 502, "bridge error");

    char json[200];
    snprintf(json, sizeof(json), "{\"jwt\":\"%s\"}", jwt);
    httpd_resp_set_type(r, "application/json");
    return httpd_resp_sendstr(r, json);
}

/* GET  /vi/media */
static esp_err_t h_media_index(httpd_req_t* r){
	if(!uart_link_usb_attached()) return send_error(r, 404, "not usb attached");
	
	media_index_t idx;
	esp_err_t er = uart_link_get_index(&idx);
	if(er != ESP_OK) return send_error(r, 502, "media index bridge error");
	
	httpd_resp_set_type(r, "application/json");
	httpd_resp_sendstr(r, "{\"gen\":1,\"files\":[]}");
	
	return ESP_OK;
}

// /v1/media/{id}/chunk?off=&len=
static esp_err_t h_media_chunk(httpd_req_t* r){
    // 1) 인증 + 레이트리밋
    if(!auth_ok(r))
        return send_error(r, 401, reason_phrase(401));
    if(ratelimit_take_nowait(s_rl_chunk, 1) != ESP_OK)
        return send_error(r, 429, reason_phrase(429));

    // 2) URI에서 id 뽑기
    //    r->uri 예: "/v1/media/ad_01.mp4/chunk"
    const char* uri = r->uri;
    const char* p   = uri + strlen("/v1/media");      // 여기서부터가 id 시작
    const char* q   = strstr(p, "/chunk");            // id 끝나는 위치
    if(!q)
        return httpd_resp_send_err(r, HTTPD_404_NOT_FOUND, "bad id");

    char id[128];
    size_t idn = (size_t)(q - p);
    if(idn >= sizeof(id))
        return httpd_resp_send_err(r, HTTPD_414_URI_TOO_LONG, "id too long");
    memcpy(id, p, idn);
    id[idn] = 0;

    // 3) 쿼리에서 off, len 뽑기
    //    예: /v1/media/ad_01.mp4/chunk?off=0&len=65536
    char qbuf[64];
    httpd_req_get_url_query_str(r, qbuf, sizeof(qbuf));   // 없으면 qbuf는 빈 문자열
    char tmp[16];
    uint64_t off = 0;
    uint32_t len = 65536;    // 기본값

    if(httpd_query_key_value(qbuf, "off", tmp, sizeof(tmp)) == ESP_OK) {
        off = strtoull(tmp, NULL, 10);
    }
    if(httpd_query_key_value(qbuf, "len", tmp, sizeof(tmp)) == ESP_OK) {
        len = (uint32_t)strtoul(tmp, NULL, 10);
    }
    // 안전 가드
    if(len == 0 || len > 256*1024)
        len = 64*1024;

    // 4) 버퍼 만들고 UART로 실제로 가져오기
    uint8_t* buf = malloc(len);
    if(!buf)
        return httpd_resp_send_err(r, HTTPD_500_INTERNAL_SERVER_ERROR, "oom");

    uint32_t got = 0;
    uint32_t crc = 0;
    esp_err_t er = uart_link_read_chunk(id, off, len, buf, &got, &crc);
    if(er != ESP_OK){
        free(buf);
        metrics_inc_err();
        return httpd_resp_send_err(r, HTTPD_500_INTERNAL_SERVER_ERROR, "read fail");
    }

    // 5) 응답 내려주기
    char h[16];
    snprintf(h, sizeof(h), "%08" PRIx32, crc);
    httpd_resp_set_type(r, "application/octet-stream");
    httpd_resp_set_hdr(r, "X-CRC32", h);

    esp_err_t ret = httpd_resp_send(r, (const char*)buf, got);
    metrics_add_bytes(0, got);
    free(buf);
    return ret;
}



/* ====== 기존 세션 열기: 앱이 JWT 또는 라이선스를 먼저 가져오고 싶을 수 있음 ====== */
/* POST /v1/session/open { "ssaid":"..." }  → 이건 지금 로컬 토큰만 발급 */
static esp_err_t h_session_open(httpd_req_t* r){
    char body[256]; int n=httpd_req_recv(r, body, sizeof(body)-1);
    if(n<0) n=0; 
    body[n]=0;

    char ssaid[64]={0};
    if (sscanf(strstr(body,"\"ssaid\""), "\"ssaid\":\"%63[^\"]\"", ssaid) != 1)
        return send_error(r, 400, "ssaid missing");

    char token[256];
    if(session_open_with_ssaid(ssaid, 10*60*1000, token, sizeof(token))!=ESP_OK)
        return send_error(r, 500, "issue fail");

    char json[320];
    snprintf(json,sizeof(json),"{\"token\":\"%s\",\"ttlMs\":600000}", token);
    httpd_resp_set_type(r,"application/json");
    return httpd_resp_sendstr(r, json);
}

esp_err_t http_srv_start(void){
    ratelimit_config_t c1={.capacity=6,.refill_tokens=2,.period_ms=1000,.start_full=1};
    ratelimit_create(&c1,&s_rl_lic);

    httpd_config_t cfg = HTTPD_DEFAULT_CONFIG();
    cfg.server_port = 80;
    cfg.lru_purge_enable = true;
    ESP_ERROR_CHECK(httpd_start(&s_srv, &cfg));

    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/session/open", .method=HTTP_POST, 	.handler=h_session_open	});
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/lic/login",    .method=HTTP_POST, 	.handler=h_lic_login    });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/lic/issue",    .method=HTTP_POST, 	.handler=h_lic_issue    });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/lic/validate", .method=HTTP_POST, 	.handler=h_lic_validate });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/lic/jwt",      .method=HTTP_POST, 	.handler=h_lic_jwt      });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/media",      	.method=HTTP_GET, 	.handler=h_media_index	});
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/media/chunk",  .method=HTTP_GET, 	.handler=h_media_chunk	});
    ESP_LOGI(TAG, "HTTP server started");
    return ESP_OK;
}
