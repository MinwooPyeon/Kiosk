/*
 * wifi_mgr.c
 *
 *  Created on: 2025. 10. 26.
 *  Updated on: 2025. 11. 06.
 *      Author: Park Joo Hyun
 */

#include "wifi_mgr.h"

#include "esp_log.h"
#include "esp_wifi.h"
#include "esp_event.h"
#include "esp_netif.h"

#include <string.h>

#define BIT_IP (1<<0)

static const char* TAG = "wifi_mgr";
static EventGroupHandle_t s_eg;
static int s_retry;                   // 재시도 횟수 (지수 백오프용)
static wifi_config_t s_wc;            // 현재 설정 보관(재접속용)

/* ----------------- Utilities ----------------- */
static void log_ip_now(const esp_netif_ip_info_t* ip){
    ESP_LOGI(TAG, "Got IP: " IPSTR, IP2STR(&ip->ip));
}

static void log_ip_handle(void){
    esp_netif_t* netif = esp_netif_get_handle_from_ifkey("WIFI_STA_DEF");
    if(!netif) return;
    esp_netif_ip_info_t ip;
    if (esp_netif_get_ip_info(netif, &ip) == ESP_OK) {
        log_ip_now(&ip);
    }
}

/* 간단한 지수 백오프 (최대 8초) */
static TickType_t backoff_delay(int retry){
    int ms = 500;
    for(int i=1;i<retry;i++){
        ms <<= 1;                 // 0.5s,1s,2s,4s,8s...
        if(ms > 8000){ ms = 8000; break; }
    }
    return pdMS_TO_TICKS(ms);
}

/* ----------------- Event Handlers ----------------- */
static void on_ip_event(void* arg, esp_event_base_t base, int32_t id, void* data){
    if(base==IP_EVENT && id==IP_EVENT_STA_GOT_IP){
        ip_event_got_ip_t* e = (ip_event_got_ip_t*)data;
        log_ip_now(&e->ip_info);
        xEventGroupSetBits(s_eg, BIT_IP);
        s_retry = 0; // 성공했으므로 재시도 카운트 리셋
    }
}

static void on_wifi_event(void* arg, esp_event_base_t base, int32_t id, void* data){
    if(base != WIFI_EVENT) return;

    switch(id){
    case WIFI_EVENT_STA_START:
        ESP_LOGI(TAG, "wifi sta start -> connect()");
        esp_wifi_connect();
        break;

    case WIFI_EVENT_STA_CONNECTED:
        ESP_LOGI(TAG, "wifi sta connected (auth in progress)");
        break;

    case WIFI_EVENT_STA_DISCONNECTED:{
        wifi_event_sta_disconnected_t* e = (wifi_event_sta_disconnected_t*)data;
        ESP_LOGE(TAG, "wifi sta disconnected: reason=%d", e->reason);
        // IP 비트 클리어 (연결이 끊겼으니 IP 없음)
        xEventGroupClearBits(s_eg, BIT_IP);

        // 재시도
        s_retry++;
        TickType_t wait = backoff_delay(s_retry);
        ESP_LOGW(TAG, "reconnect (attempt=%d, backoff=%lu ms)", s_retry, (unsigned long)(wait*portTICK_PERIOD_MS));
        vTaskDelay(wait);
        esp_wifi_connect();
        break;
    }

    default:
        break;
    }
}

/* ----------------- Public APIs ----------------- */
esp_err_t wifi_mgr_start(const wifi_mgr_config_t* cfg){
    if(!cfg || !cfg->ssid) return ESP_ERR_INVALID_ARG;

    if(!s_eg) s_eg = xEventGroupCreate();
    s_retry = 0;

    // 기본 NETIF/이벤트 루프는 app_main에서 init 되어있다고 가정 (일반적인 ESP-IDF 프로젝트)
    esp_netif_t* sta = esp_netif_create_default_wifi_sta();
    if(!sta){
        ESP_LOGE(TAG, "failed to create default wifi sta netif");
        return ESP_FAIL;
    }

    wifi_init_config_t init = WIFI_INIT_CONFIG_DEFAULT();
    ESP_ERROR_CHECK(esp_wifi_init(&init));

    // 이벤트 핸들러 등록 (IP / WIFI)
    ESP_ERROR_CHECK(esp_event_handler_register(IP_EVENT,   IP_EVENT_STA_GOT_IP, on_ip_event,  NULL));
    ESP_ERROR_CHECK(esp_event_handler_register(WIFI_EVENT, ESP_EVENT_ANY_ID,     on_wifi_event, NULL));

    // Wi-Fi 동작 파라미터
    memset(&s_wc, 0, sizeof(s_wc));
    // SSID/PW 복사 (널 종료)
    strncpy((char*)s_wc.sta.ssid,     cfg->ssid, sizeof(s_wc.sta.ssid)-1);
    if(cfg->pass) strncpy((char*)s_wc.sta.password, cfg->pass, sizeof(s_wc.sta.password)-1);

    // 스캔/임계값 설정 (디버그에 유리한 값)
    s_wc.sta.scan_method = WIFI_ALL_CHANNEL_SCAN;
    s_wc.sta.threshold.rssi = -127;
    // 최소 인증 모드(스캔 필터, 접속은 AP 보안에 맞춰 진행됨). 비번 있으면 WPA2 이상 기대
    s_wc.sta.threshold.authmode = (cfg->pass && cfg->pass[0]) ? WIFI_AUTH_WPA2_PSK : WIFI_AUTH_OPEN;

    // 절전 해제 (초기 연결 안정화/디버깅에 유리)
    esp_wifi_set_ps(WIFI_PS_NONE);

    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
    ESP_ERROR_CHECK(esp_wifi_set_config(WIFI_IF_STA, &s_wc));
    ESP_ERROR_CHECK(esp_wifi_start());

    // 이 시점에서 실제 connect는 WIFI_EVENT_STA_START에서 호출됨
    ESP_LOGI(TAG, "wifi sta connecting to %s", s_wc.sta.ssid);

    // 여기서 log_ip()를 찍으면 아직 DHCP 전일 수 있으니 생략
    return ESP_OK;
}

bool wifi_mgr_wait_ip(TickType_t to){
    EventBits_t b = xEventGroupWaitBits(s_eg, BIT_IP, pdFALSE, pdFALSE, to);
    if ((b & BIT_IP) == 0){
        // 타임아웃 시 현재 IP를 참고용으로 찍어도 됨
        ESP_LOGW(TAG, "wait ip timeout");
        log_ip_handle();
        return false;
    }
    return true;
}
