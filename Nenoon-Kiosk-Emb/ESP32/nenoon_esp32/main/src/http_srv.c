/*
 * http_srv.c
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */

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
#include <inttypes.h>

static const char* TAG = "http_srv";
static httpd_handle_t s_srv;
static ratelimit_t*   s_rl_lic;
static ratelimit_t*   s_rl_chunk;

/* ---------------- Common ---------------- */

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

// ---- CORS helpers ----
static inline void cors_add_headers(httpd_req_t* r){
    httpd_resp_set_hdr(r, "Access-Control-Allow-Origin",  "*");
    httpd_resp_set_hdr(r, "Access-Control-Allow-Methods", "GET, POST, OPTIONS");
    httpd_resp_set_hdr(r, "Access-Control-Allow-Headers", "Content-Type, Authorization, X-Requested-With, Range");
    httpd_resp_set_hdr(r, "Access-Control-Expose-Headers","X-CRC32, Content-Length, Content-Type");
}

static esp_err_t send_error(httpd_req_t* r, int code, const char* msg){
    cors_add_headers(r); // 에러에도 반드시 CORS 부착
    char status[48];
    snprintf(status, sizeof(status), "%d %s", code, reason_phrase(code));
    httpd_resp_set_status(r, status);
    httpd_resp_set_type(r, "text/plain");
    return httpd_resp_sendstr(r, msg ? msg : "");
}

// 프리플라이트(OPTIONS)
static esp_err_t h_cors_preflight(httpd_req_t* r){
    cors_add_headers(r);
    httpd_resp_set_status(r, "204 No Content");
    httpd_resp_set_type(r, "text/plain");
    return httpd_resp_sendstr(r, "");
}

// Authorization: Bearer <token>
static bool auth_ok(httpd_req_t* r){
    char tok[256] = {0};
    if(httpd_req_get_hdr_value_str(r, "Authorization", tok, sizeof(tok))!=ESP_OK) return false;
    const char* b = strstr(tok, "Bearer ");
    if(!b) return false;
    char ssaid[64];
    return session_verify_get_ssaid(b+7, ssaid, sizeof(ssaid));
}

/* 안전 JSON 이스케이프 */
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
                    EMIT2('\\','u'); EMIT('0'); EMIT('0');
                    EMIT(HEX[(c >> 4) & 0xF]); EMIT(HEX[c & 0xF]);
                }else{
                    EMIT(c);
                }
        }
    }
END:
    dst[w] = 0;
    #undef EMIT
    #undef EMIT2
    return w;
}

/* 본문 수신 루프: 부분수신 방지 */
static esp_err_t recv_body(httpd_req_t* r, char* buf, size_t cap, size_t* out_len){
    if(!buf || cap==0){ if(out_len) *out_len=0; return ESP_ERR_INVALID_ARG; }
    size_t need = r->content_len;
    if(need >= cap) need = cap - 1;
    size_t got = 0;
    while(got < need){
        int n = httpd_req_recv(r, buf + got, need - got);
        if(n <= 0) break;
        got += (size_t)n;
    }
    buf[got] = 0;
    if(out_len) *out_len = got;
    return (got>0 || r->content_len==0) ? ESP_OK : ESP_FAIL;
}

/* ---------------- Probe/Echo (디버깅) ---------------- */

static esp_err_t h_probe_echo(httpd_req_t* r){
    cors_add_headers(r);

    char ctype[64] = {0};
    (void)httpd_req_get_hdr_value_str(r, "Content-Type", ctype, sizeof(ctype));
    char auth[128] = {0};
    (void)httpd_req_get_hdr_value_str(r, "Authorization", auth, sizeof(auth));

    char body[256]; size_t n=0;
    (void)recv_body(r, body, sizeof(body), &n);

    char uri_esc[256];   json_escape(uri_esc,   sizeof(uri_esc),   r->uri, strnlen(r->uri, 255));
    char ctype_esc[96];  json_escape(ctype_esc, sizeof(ctype_esc), ctype,  strnlen(ctype, sizeof(ctype)-1));
    char auth_esc[192];  json_escape(auth_esc,  sizeof(auth_esc),  auth,   strnlen(auth,  sizeof(auth)-1));
    char prev_esc[512];  json_escape(prev_esc,  sizeof(prev_esc),  body,   n);

    ESP_LOGI("probe","method=%d uri=%s len=%d ctype=%s auth=%s preview_len=%d",
             r->method, r->uri, r->content_len, ctype, auth, (int)n);

    httpd_resp_set_type(r, "application/json");
    httpd_resp_sendstr_chunk(r, "{");
    char tmp[32];

    httpd_resp_sendstr_chunk(r, "\"ok\":true,\"method\":");
    snprintf(tmp, sizeof(tmp), "%d", r->method);
    httpd_resp_sendstr_chunk(r, tmp);

    httpd_resp_sendstr_chunk(r, ",\"uri\":\"");      httpd_resp_sendstr_chunk(r, uri_esc);   httpd_resp_sendstr_chunk(r, "\"");
    httpd_resp_sendstr_chunk(r, ",\"contentLen\":"); snprintf(tmp, sizeof(tmp), "%d", r->content_len); httpd_resp_sendstr_chunk(r, tmp);
    httpd_resp_sendstr_chunk(r, ",\"contentType\":\""); httpd_resp_sendstr_chunk(r, ctype_esc); httpd_resp_sendstr_chunk(r, "\"");
    httpd_resp_sendstr_chunk(r, ",\"auth\":\"");     httpd_resp_sendstr_chunk(r, auth_esc);  httpd_resp_sendstr_chunk(r, "\"");
    httpd_resp_sendstr_chunk(r, ",\"preview\":\"");  httpd_resp_sendstr_chunk(r, prev_esc);  httpd_resp_sendstr_chunk(r, "\"}");

    return httpd_resp_send_chunk(r, NULL, 0);
}

/* ---------------- Business APIs ---------------- */

// 1) 라이선스 로그인
static esp_err_t h_lic_login(httpd_req_t* r){
    char body[160]; size_t n=0;
    (void)recv_body(r, body, sizeof(body), &n);

    const char* idp = strstr(body, "\"id\"");
    const char* pwp = strstr(body, "\"pw\"");
    if(!idp || !pwp) return send_error(r, 400, "id/pw missing");

    char idv[32]={0}, pwv[32]={0};
    (void)sscanf(idp, "\"id\":\"%31[^\"]\"", idv);
    (void)sscanf(pwp, "\"pw\":\"%31[^\"]\"", pwv);

    bool ok = false;
    if(uart_link_lic_mgr_login(idv, pwv, &ok) != ESP_OK)
        return send_error(r, 502, "bridge error");
    if(!ok) return send_error(r, 401, "login fail");

    httpd_resp_set_type(r, "application/json");
    cors_add_headers(r);
    return httpd_resp_sendstr(r, "{\"result\":1}");
}

// 2) 라이선스 발급
static esp_err_t h_lic_issue(httpd_req_t* r){
    if(!auth_ok(r)) return send_error(r, 401, "unauthorized");

    char body[160]; size_t n=0;
    (void)recv_body(r, body, sizeof(body), &n);

    const char* appp = strstr(body,"\"app\"");
    const char* top  = strstr(body,"\"to\"");
    if(!appp || !top) return send_error(r, 400, "app/to missing");

    char app[32]={0}, to[64]={0};
    (void)sscanf(appp, "\"app\":\"%31[^\"]\"", app);
    (void)sscanf(top,  "\"to\":\"%63[^\"]\"",  to);

    char lic[128];
    if(uart_link_lic_issue(app, to, lic, sizeof(lic)) != ESP_OK)
        return send_error(r, 502, "bridge error");

    char json[192];
    snprintf(json, sizeof(json), "{\"license\":\"%s\"}", lic);
    httpd_resp_set_type(r, "application/json");
    cors_add_headers(r);
    return httpd_resp_sendstr(r, json);
}

// 3) 라이선스 검증
static esp_err_t h_lic_validate(httpd_req_t* r){
    char body[128]; size_t n=0;
    (void)recv_body(r, body, sizeof(body), &n);

    const char* lp = strstr(body,"\"license\"");
    if(!lp) return send_error(r, 400, "license missing");

    char lic[96]={0};
    (void)sscanf(lp, "\"license\":\"%95[^\"]\"", lic);

    bool ok = false;
    if(uart_link_lic_validate(lic, &ok) != ESP_OK)
        return send_error(r, 502, "bridge error");

    httpd_resp_set_type(r, "application/json");
    cors_add_headers(r);
    return httpd_resp_sendstr(r, ok ? "{\"valid\":true}" : "{\"valid\":false}");
}

// 4) 라이선스 → JWT
static esp_err_t h_lic_jwt(httpd_req_t* r){
    char body[128]; size_t n=0;
    (void)recv_body(r, body, sizeof(body), &n);

    const char* lp = strstr(body,"\"license\"");
    if(!lp) return send_error(r, 400, "license missing");

    char lic[96]={0};
    (void)sscanf(lp, "\"license\":\"%95[^\"]\"", lic);

    char jwt[160];
    if(uart_link_lic_get_jwt(lic, jwt, sizeof(jwt)) != ESP_OK)
        return send_error(r, 502, "bridge error");

    char json[224];
    snprintf(json, sizeof(json), "{\"jwt\":\"%s\"}", jwt);
    httpd_resp_set_type(r, "application/json");
    cors_add_headers(r);
    return httpd_resp_sendstr(r, json);
}

// 5) 미디어 인덱스 (임시 스텁)
static esp_err_t h_media_index(httpd_req_t* r){
    if(!uart_link_usb_attached()) return send_error(r, 404, "not usb attached");

    media_index_t idx;
    esp_err_t er = uart_link_get_index(&idx);
    if(er != ESP_OK) return send_error(r, 502, "media index bridge error");

    // TODO: idx → JSON 직렬화 (현재 스텁)
    httpd_resp_set_type(r, "application/json");
    cors_add_headers(r);
    return httpd_resp_sendstr(r, "{\"gen\":1,\"files\":[]}");
}

// 6) /v1/media/{id}/chunk?off=&len=
static esp_err_t h_media_chunk(httpd_req_t* r){
    if(!auth_ok(r)) return send_error(r, 401, "unauthorized");
    if(ratelimit_take_nowait(s_rl_chunk, 1) != ESP_OK)
        return send_error(r, 429, "too many requests");

    // 라우트는 "/v1/media/*"로 등록되어 있으므로, 여기서 "/chunk" 위치를 확인
    const char* uri = r->uri;               // 예: "/v1/media/ad_01.mp4/chunk?off=0&len=65536"
    const char* chunkp = strstr(uri, "/chunk");
    if(!chunkp) return send_error(r, 404, "bad id");

    // "/v1/media" 이후부터 chunkp 전까지가 id
    const char* idp = uri + strlen("/v1/media");
    size_t idn = (size_t)(chunkp - idp);
    if(idn == 0 || idn >= 128) return send_error(r, 414, "id too long");

    char id[128];
    memcpy(id, idp, idn);
    id[idn] = 0;

    // 쿼리
    char qbuf[64] = {0};
    (void)httpd_req_get_url_query_str(r, qbuf, sizeof(qbuf)); // 없으면 빈 문자열
    char tmp[20] = {0};
    uint64_t off = 0;
    uint32_t len = 65536;

    if(httpd_query_key_value(qbuf, "off", tmp, sizeof(tmp)) == ESP_OK) off = strtoull(tmp, NULL, 10);
    if(httpd_query_key_value(qbuf, "len", tmp, sizeof(tmp)) == ESP_OK) len = (uint32_t)strtoul(tmp, NULL, 10);
    if(len == 0 || len > 256*1024) len = 64*1024;

    uint8_t* buf = (uint8_t*)malloc(len);
    if(!buf) return send_error(r, 500, "oom");

    uint32_t got = 0;
    uint32_t crc = 0;
    esp_err_t er = uart_link_read_chunk(id, off, len, buf, &got, &crc);
    if(er != ESP_OK){
        free(buf);
        metrics_inc_err();
        return send_error(r, 500, "read fail");
    }

    char h[16]; // 8 hex + NUL
    snprintf(h, sizeof(h), "%08" PRIx32, crc);
    httpd_resp_set_type(r, "application/octet-stream");
    cors_add_headers(r);
    httpd_resp_set_hdr(r, "X-CRC32", h);

    esp_err_t ret = httpd_resp_send(r, (const char*)buf, got);
    metrics_add_bytes(0, got);
    free(buf);
    return ret;
}

// 7) 세션 오픈
static esp_err_t h_session_open(httpd_req_t* r){
    char body[256]; size_t n=0;
    (void)recv_body(r, body, sizeof(body), &n);

    const char* k = strstr(body,"\"ssaid\"");
    if(!k) return send_error(r, 400, "ssaid missing");

    char ssaid[64]={0};
    if(sscanf(k, "\"ssaid\":\"%63[^\"]\"", ssaid) != 1)
        return send_error(r, 400, "ssaid parse fail");

    char token[256];
    if(session_open_with_ssaid(ssaid, 10*60*1000, token, sizeof(token))!=ESP_OK)
        return send_error(r, 500, "issue fail");

    char json[320];
    snprintf(json,sizeof(json),"{\"token\":\"%s\",\"ttlMs\":600000}", token);
    httpd_resp_set_type(r,"application/json");
    cors_add_headers(r);
    return httpd_resp_sendstr(r, json);
}

/* ---------------- Route Table ---------------- */

static const httpd_uri_t U_SESSION_OPEN = {
    .uri      = "/v1/session/open",
    .method   = HTTP_POST,
    .handler  = h_session_open,
    .user_ctx = NULL
};
static const httpd_uri_t U_LIC_LOGIN = {
    .uri = "/v1/lic/login", .method = HTTP_POST, .handler = h_lic_login, .user_ctx = NULL
};
static const httpd_uri_t U_LIC_ISSUE = {
    .uri = "/v1/lic/issue", .method = HTTP_POST, .handler = h_lic_issue, .user_ctx = NULL
};
static const httpd_uri_t U_LIC_VALIDATE = {
    .uri = "/v1/lic/validate", .method = HTTP_POST, .handler = h_lic_validate, .user_ctx = NULL
};
static const httpd_uri_t U_LIC_JWT = {
    .uri = "/v1/lic/jwt", .method = HTTP_POST, .handler = h_lic_jwt, .user_ctx = NULL
};
static const httpd_uri_t U_MEDIA_INDEX = {
    .uri = "/v1/media", .method = HTTP_GET, .handler = h_media_index, .user_ctx = NULL
};
// 와일드카드는 끝에만 허용되므로 "/v1/media/*"로 등록 후 내부에서 "/chunk" 여부 확인
static const httpd_uri_t U_MEDIA_ANY = {
    .uri = "/v1/media/*", .method = HTTP_GET, .handler = h_media_chunk, .user_ctx = NULL
};
static const httpd_uri_t U_CORS_PREFLIGHT = {
    .uri = "/v1/*", .method = HTTP_OPTIONS, .handler = h_cors_preflight, .user_ctx = NULL
};
// 디버그용
static const httpd_uri_t U_PROBE_ECHO = {
    .uri = "/v1/probe", .method = HTTP_POST, .handler = h_probe_echo, .user_ctx = NULL
};

esp_err_t http_srv_start(void){
    // 레이트리밋 초기화
    ratelimit_config_t c1={.capacity=6,.refill_tokens=2,.period_ms=1000,.start_full=1};
    ratelimit_create(&c1,&s_rl_lic);
    ratelimit_config_t c2={.capacity=8,.refill_tokens=4,.period_ms=1000,.start_full=1};
    ratelimit_create(&c2,&s_rl_chunk);

    httpd_config_t cfg = HTTPD_DEFAULT_CONFIG();
    cfg.server_port      = 80;
    cfg.lru_purge_enable = true;
    cfg.uri_match_fn     = httpd_uri_match_wildcard;
    cfg.stack_size       = 8192; // 요청 처리 중 스택 여유 확보
    cfg.max_uri_handlers = 16;

    ESP_ERROR_CHECK(httpd_start(&s_srv, &cfg));

    httpd_register_uri_handler(s_srv, &U_SESSION_OPEN);
    httpd_register_uri_handler(s_srv, &U_LIC_LOGIN);
    httpd_register_uri_handler(s_srv, &U_LIC_ISSUE);
    httpd_register_uri_handler(s_srv, &U_LIC_VALIDATE);
    httpd_register_uri_handler(s_srv, &U_LIC_JWT);
    httpd_register_uri_handler(s_srv, &U_MEDIA_INDEX);
    httpd_register_uri_handler(s_srv, &U_MEDIA_ANY);
    httpd_register_uri_handler(s_srv, &U_CORS_PREFLIGHT);
    httpd_register_uri_handler(s_srv, &U_PROBE_ECHO);

    ESP_LOGI(TAG, "HTTP server started");
    return ESP_OK;
}
