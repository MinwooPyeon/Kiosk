/*
 * http_srv.c
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */

// main/src/http_srv.c  (STM32 프레임에 맞춘 버전)
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

static const char* TAG = "http_srv";
static httpd_handle_t s_srv;
static ratelimit_t* s_rl_lic;

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
    if(n < 0) n = 0; body[n] = 0;

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
    if(n<0) n=0; body[n]=0;

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
    if(n<0) n=0; body[n]=0;

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
    if(n<0) n=0; body[n]=0;

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

/* ====== 기존 세션 열기: 앱이 JWT 또는 라이선스를 먼저 가져오고 싶을 수 있음 ====== */
/* POST /v1/session/open { "ssaid":"..." }  → 이건 지금 로컬 토큰만 발급 */
static esp_err_t h_session_open(httpd_req_t* r){
    char body[256]; int n=httpd_req_recv(r, body, sizeof(body)-1);
    if(n<0) n=0; body[n]=0;

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

    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/session/open", .method=HTTP_POST, .handler=h_session_open });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/lic/login",    .method=HTTP_POST, .handler=h_lic_login    });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/lic/issue",    .method=HTTP_POST, .handler=h_lic_issue    });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/lic/validate", .method=HTTP_POST, .handler=h_lic_validate });
    httpd_register_uri_handler(s_srv, &(httpd_uri_t){ .uri="/v1/lic/jwt",      .method=HTTP_POST, .handler=h_lic_jwt      });

    ESP_LOGI(TAG, "HTTP server started");
    return ESP_OK;
}
