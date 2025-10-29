// app_main.c
#include "esp_err.h"
#include "esp_event.h"
#include "esp_log.h"
#include "freertos/projdefs.h"
#include "nvs_flash.h"
#include "esp_netif.h"
#include "wifi_mgr.h"
#include "session_mgr.h"
#include "uart_link.h"
#include "http_srv.h"


static const char* TAG = "app_main";

void app_main(void) {
    ESP_ERROR_CHECK(nvs_flash_init());
    ESP_ERROR_CHECK(esp_netif_init());
    ESP_ERROR_CHECK(esp_event_loop_create_default());
    
    wifi_mgr_config_t wcfg = {
		.ssid = CONFIG_ESP_WIFI_SSID,
		.pass = CONFIG_ESP_WIFI_PASSWORD,
		.ap_failback = true,
	};
	
	ESP_ERROR_CHECK(wifi_mgr_start(&wcfg));
	wifi_mgr_wait_ip(pdMS_TO_TICKS(15000));
	
	ESP_ERROR_CHECK(session_mgr_init());
	
	ESP_ERROR_CHECK(http_srv_start());
	
	ESP_LOGI(TAG, "system ready");
}
