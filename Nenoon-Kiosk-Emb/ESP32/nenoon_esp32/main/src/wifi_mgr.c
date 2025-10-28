/*
 * wifi_mgr.c
 *
 *  Created on: 2025. 10. 26.
 *  Updated on: 2025. 10. 28.
 *      Author: Park Joo Hyun
 */

#include "wifi_mgr.h"
 
#include "esp_event_base.h"
#include "esp_log.h"
#include "esp_wifi.h"
#include "esp_event.h"
#include <string.h>

#define BIT_IP (1<<0)

static const char* TAG = "wifi_mgr";
static EventGroupHandle_t s_eg;

static void ip_event_handler(void* arg, esp_event_base_t base, int32_t id, void* data){
	if(base==IP_EVENT && id==IP_EVENT_STA_GOT_IP){
		xEventGroupSetBits(s_eg, BIT_IP);
	}
}

esp_err_t wifi_mgr_start(const wifi_mgr_config_t* cfg){
    if(!cfg || !cfg->ssid) return ESP_ERR_INVALID_ARG;
    s_eg = xEventGroupCreate();

    esp_netif_create_default_wifi_sta();
    wifi_init_config_t init = WIFI_INIT_CONFIG_DEFAULT();
    ESP_ERROR_CHECK(esp_wifi_init(&init));
    ESP_ERROR_CHECK(esp_event_handler_register(IP_EVENT, IP_EVENT_STA_GOT_IP, ip_event_handler, NULL));

    wifi_config_t wc = {0};
    strncpy((char*)wc.sta.ssid, cfg->ssid, sizeof(wc.sta.ssid)-1);
    if(cfg->pass) strncpy((char*)wc.sta.password, cfg->pass, sizeof(wc.sta.password)-1);
    wc.sta.threshold.authmode = (cfg->pass && cfg->pass[0]) ? WIFI_AUTH_WPA2_PSK : WIFI_AUTH_OPEN;

    ESP_ERROR_CHECK(esp_wifi_set_mode(WIFI_MODE_STA));
    ESP_ERROR_CHECK(esp_wifi_set_config(WIFI_IF_STA, &wc));
    ESP_ERROR_CHECK(esp_wifi_start());
    ESP_ERROR_CHECK(esp_wifi_connect());
    ESP_LOGI(TAG, "wifi sta connecting to %s", wc.sta.ssid);
    return ESP_OK;
}

bool wifi_mgr_wait_ip(TickType_t to){
    EventBits_t b = xEventGroupWaitBits(s_eg, BIT_IP, pdFALSE, pdFALSE, to);
    return (b & BIT_IP)!=0;
}