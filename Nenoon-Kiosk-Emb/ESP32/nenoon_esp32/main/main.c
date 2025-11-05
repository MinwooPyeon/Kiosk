// app_main.c
#include "esp_err.h"
#include "esp_event.h"
#include "esp_log.h"
#include "freertos/projdefs.h"
#include "metrics.h"
#include "nvs_flash.h"
#include "esp_netif.h"

#include "wifi_mgr.h"
#include "session_mgr.h"
#include "uart_link.h"
#include "http_srv.h"
#include <string.h>


static const char* TAG = "app_main";

static void task_uart_link(void* arg){
	ESP_ERROR_CHECK(uart_link_init());
	ESP_LOGI(TAG, "uart_link initialized");
	vTaskDelete(NULL);
}

static void task_session_mgr(void* arg){
	ESP_ERROR_CHECK(session_mgr_init());
	ESP_LOGI(TAG, "session manager initialized");
	vTaskDelete(NULL);
}

static void task_net_http(void* arg){
	wifi_mgr_config_t wcfg ={
		.ssid = "SSAFY-3F",
		.pass = "ssafy123!"
	};
	ESP_ERROR_CHECK(wifi_mgr_start(&wcfg));
	bool got = wifi_mgr_wait_ip(pdMS_TO_TICKS(30000));
	if(!got)
		ESP_LOGE(TAG, "Wifi Connect Timeout");
	ESP_ERROR_CHECK(http_srv_start());
	ESP_LOGI(TAG, "Http Server Start");
	vTaskDelete(NULL);
}

static void task_metrics(void* arg){
	metrics_snapshot_t m;
	while(1){
		metrics_get(&m);
		ESP_LOGI(TAG, "[metrics] http_ok=%u http_err=%u tx=%u rx=%u sse=%u",
                 m.http_ok, m.http_err, m.bytes_tx, m.bytes_rx, m.sse_clients);
        vTaskDelay(pdMS_TO_TICKS(5000));
	}
}

void app_main(void) {
    ESP_ERROR_CHECK(nvs_flash_init());
    ESP_ERROR_CHECK(esp_netif_init());
    ESP_ERROR_CHECK(esp_event_loop_create_default());
    
    xTaskCreate(task_uart_link, "t_uart", 4096, NULL, 8, NULL);
    xTaskCreate(task_session_mgr, "t_uart", 4096, NULL, 7, NULL);
    xTaskCreate(task_net_http, "t_uart", 4096, NULL, 6, NULL);
    xTaskCreate(task_metrics, "t_uart", 4096, NULL, 3, NULL);
}
