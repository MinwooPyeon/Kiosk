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

// ---- CORS helpers ----
static inline void cors_add_headers(httpd_req_t* r){
    // 개발/테스트 편의용 설정
    httpd_resp_set_hdr(r, "Access-Control-Allow-Origin",  "*");
    httpd_resp_set_hdr(r, "Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    httpd_resp_set_hdr(r, "Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Range");
    // 브라우저에서 읽게 하고 싶은 응답 헤더 나열 (청크 CRC, 길이 등)
    httpd_resp_set_hdr(r, "Access-Control-Expose-Headers","X-CRC32, Content-Length, Content-Type");
}

// 프리플라이트(OPTIONS) 공용 핸들러
static esp_err_t h_cors_preflight(httpd_req_t* r){
    cors_add_headers(r);
    httpd_resp_set_status(r, "204 No Content");
    httpd_resp_set_type(r, "text/plain");
    return httpd_resp_sendstr(r, "");
}

static bool auth_ok(httpd_req_t* r){
    char tok[512];
    if(httpd_req_get_hdr_value_str(r, "Authorization", tok, sizeof(tok))!=ESP_OK) return false;
    const char* b = strstr(tok, "Bearer ");
    if(!b) return false;
    char ssaid[64];
    return session_verify_get_ssaid(b+7, ssaid, sizeof(ssaid));
}

/* --- 안전 JSON 이스케이프: dst는 항상 널종료, 초과하면 잘라냄 --- */
/* --- 안전 JSON 이스케이프: dst는 항상 널종료, 초과하면 잘라냄 --- */
static size_t json_escape(char* dst, size_t dstsz, const char* src, size_t srclen){
    if(!dst || dstsz==0) return 0;
    size_t w = 0;
    static const char HEX[] = "0123456789abcdef";

    #define EMIT(ch)       do{ if(w+1 < dstsz){ dst[w++] = (ch); } else { goto END; } }while(0)
    #define EMIT2(a,b)     do{ if(w+2 < dstsz){ dst[w++] = (a); dst[w++] = (b); } else { goto END; } }while(0)

    for(size_t i=0;i<srclen;i++){
        unsigned char c = (unsigned char)src[i];
        switch(c){
            case '\"': EMIT2('\\','\"'); break;
            case '\\': EMIT2('\\','\\'); break;
            case '\b': EMIT2('\\','b');  break;
            case '\f': EMIT2('\\','f');  break;
            case '\n': EMIT2('\\','n');  break;
            case '\r': EMIT2('\\','r');  break;
            case '\t': EMIT2('\\','t');  break;
            default:
                if(c < 0x20){
                    /* 제어문자 → "\u00XX" (snprintf 없이 수동 조립) */
                    EMIT2('\\','u');
                    EMIT('0'); EMIT('0');
                    EMIT(HEX[(c >> 4) & 0xF]);
                    EMIT(HEX[c & 0xF]);
                }else{
                    EMIT(c);
                }
        }
    }
END:
    dst[w] = 0;
    return w;

    #undef EMIT
    #undef EMIT2
}


/* ===== PROBE / ECHO (경고 없이 안전) ===== */
static esp_err_t h_probe_echo(httpd_req_t* r){
    cors_add_headers(r);

    /* 헤더 일부 */
    char ctype[64] = {0};
    (void)httpd_req_get_hdr_value_str(r, "Content-Type", ctype, sizeof(ctype));

    char auth[128] = {0};
    (void)httpd_req_get_hdr_value_str(r, "Authorization", auth, sizeof(auth));

    /* 바디 프리뷰 (최대 256B만 읽음) */
    char body[256];
    int want = (r->content_len > 256) ? 256 : r->content_len;
    int n = 0;
    if (want > 0) {
        n = httpd_req_recv(r, body, want);
        if (n < 0) n = 0;
    }

    /* JSON용 이스케이프 (길이 제한) */
    char uri_esc[256];   json_escape(uri_esc,   sizeof(uri_esc),   r->uri,                strnlen(r->uri, 255));
    char ctype_esc[96];  json_escape(ctype_esc, sizeof(ctype_esc), ctype,                 strnlen(ctype, sizeof(ctype)-1));
    char auth_esc[192];  json_escape(auth_esc,  sizeof(auth_esc),  auth,                  strnlen(auth,  sizeof(auth)-1));
    char prev_esc[512];  json_escape(prev_esc,  sizeof(prev_esc),  body,                  (size_t)n);

    /* 로그 (미리보기는 로그에서도 잘라서) */
    ESP_LOGI("probe","method=%d uri=%s len=%d ctype=%s auth=%s preview_len=%d",
             r->method, r->uri, r->content_len, ctype, auth, n);

    httpd_resp_set_type(r, "application/json");

    /* 청크 전송: 큰 snprintf 없이 안전 */
    httpd_resp_sendstr_chunk(r, "{");
    char tmp[64];

    httpd_resp_sendstr_chunk(r, "\"ok\":true,\"method\":");
    snprintf(tmp, sizeof(tmp), "%d", r->method);
    httpd_resp_sendstr_chunk(r, tmp);

    httpd_resp_sendstr_chunk(r, ",\"uri\":\"");
    httpd_resp_sendstr_chunk(r, uri_esc);
    httpd_resp_sendstr_chunk(r, "\"");

    httpd_resp_sendstr_chunk(r, ",\"contentLen\":");
    snprintf(tmp, sizeof(tmp), "%d", r->content_len);
    httpd_resp_sendstr_chunk(r, tmp);

    httpd_resp_sendstr_chunk(r, ",\"contentType\":\"");
    httpd_resp_sendstr_chunk(r, ctype_esc);
    httpd_resp_sendstr_chunk(r, "\"");

    httpd_resp_sendstr_chunk(r, ",\"auth\":\"");
    httpd_resp_sendstr_chunk(r, auth_esc);
    httpd_resp_sendstr_chunk(r, "\"");

    httpd_resp_sendstr_chunk(r, ",\"preview\":\"");
    httpd_resp_sendstr_chunk(r, prev_esc);
    httpd_resp_sendstr_chunk(r, "\"}");

    /* 청크 종료 */
    return httpd_resp_send_chunk(r, NULL, 0);
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
    cors_add_headers(r);  
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
    cors_add_headers(r);  
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
    cors_add_headers(r);  
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
    cors_add_headers(r);  
    return httpd_resp_sendstr(r, json);
}

/* GET  /vi/media */
static esp_err_t h_media_index(httpd_req_t* r){
	if(!uart_link_usb_attached()) return send_error(r, 404, "not usb attached");
	
	media_index_t idx;
	esp_err_t er = uart_link_get_index(&idx);
	if(er != ESP_OK) return send_error(r, 502, "media index bridge error");
	
	httpd_resp_set_type(r, "application/json");
	cors_add_headers(r);  
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
    cors_add_headers(r);  
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
    cors_add_headers(r);  
    return httpd_resp_sendstr(r, json);
}

esp_err_t http_srv_start(void){
    ratelimit_config_t c1={.capacity=6,.refill_tokens=2,.period_ms=1000,.start_full=1};
    ratelimit_create(&c1,&s_rl_lic);

    httpd_config_t cfg = HTTPD_DEFAULT_CONFIG();
    cfg.server_port = 80;
    cfg.lru_purge_enable = true;
    cfg.uri_match_fn = httpd_uri_match_wildcard;
    ESP_ERROR_CHECK(httpd_start(&s_srv, &cfg));

    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/session/open", .method=HTTP_POST, 		.handler=h_probe_echo	});
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/lic/login",    .method=HTTP_POST, 		.handler=h_probe_echo    });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/lic/issue",    .method=HTTP_POST, 		.handler=h_probe_echo    });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/lic/validate", .method=HTTP_POST, 		.handler=h_probe_echo });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/lic/jwt",      .method=HTTP_POST, 		.handler=h_probe_echo      });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/media",      	.method=HTTP_GET, 		.handler=h_probe_echo	});
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/media/chunk",  .method=HTTP_GET, 		.handler=h_probe_echo	});
	httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/*", 			.method=HTTP_OPTIONS, 	.handler=h_cors_preflight });
    ESP_LOGI(TAG, "HTTP server started");
    return ESP_OK;
}
