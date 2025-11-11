// app_main.c
#include "esp_err.h"
#include "esp_event.h"
#include "esp_log.h"
#include "nvs_flash.h"
#include "esp_netif.h"

#include "freertos/FreeRTOS.h"   // ★ 추가
#include "freertos/task.h"       // ★ 추가
#include "freertos/projdefs.h"

#include "wifi_mgr.h"
#include "session_mgr.h"
#include "uart_link.h"
#include "http_srv.h"
#include "metrics.h"
#include "debug_tx.h"
#include <string.h>

static const char* TAG = "app_main";

static void task_uart_link(void* arg){
    ESP_ERROR_CHECK(uart_link_init());     // 내부에서 RX 태스크 생성됨
    ESP_LOGI(TAG, "uart_link initialized");
    vTaskDelete(NULL);
}

static void task_session_mgr(void* arg){
    ESP_ERROR_CHECK(session_mgr_init());
    ESP_LOGI(TAG, "session manager initialized");
    vTaskDelete(NULL);
}

static void task_net_http(void* arg){
    wifi_mgr_config_t wcfg = { .ssid = "5367", .pass = "mwhp9276" };
    ESP_ERROR_CHECK(wifi_mgr_start(&wcfg));

    bool got = wifi_mgr_wait_ip(pdMS_TO_TICKS(30000));
    if(!got){
        ESP_LOGE(TAG, "WiFi connect timeout → HTTP server not started");
        vTaskDelete(NULL);
        return;
    }

    // ★ HTTP 서버는 자체 태스크를 만들기 때문에 여기 스택 4KB면 충분
    ESP_ERROR_CHECK(http_srv_start());
    ESP_LOGI(TAG, "Http Server Start");
    vTaskDelete(NULL);
}

void task_metrics(void* arg){
    metrics_snapshot_t m;
    while(1){
        metrics_get(&m);
        ESP_LOGI(TAG, "[metrics] http_ok=%u http_err=%u tx=%u rx=%u sse=%u",
                 m.http_ok, m.http_err, m.bytes_tx, m.bytes_rx, m.sse_clients);
        vTaskDelay(pdMS_TO_TICKS(5000)); 
    }
}

void test_tx(void){
	// 1) PING 프레임 1회
    debug_tx_send_ping();
	ESP_LOGI(TAG, "debug tx send ping");
    // 2) 텍스트 프레임
    debug_tx_send_text("hello, stm32!");
	ESP_LOGI(TAG, "debug tx send hello stm32");
    // 3) HEX → RAW (프레임 래핑 방식으로 전송)
    debug_tx_send_hex("FF00A1C0");
    ESP_LOGI(TAG, "debug tx send FF00A1C0");

    // 4) 10회 버스트(50ms 간격)
    debug_tx_burst(10, 50);
    ESP_LOGI(TAG, "debug tx send burst 10");
}

void app_main(void) {
    // NVS 초기화 예외 처리(이미 사용 중일 때)
    esp_err_t nv = nvs_flash_init();
    if (nv == ESP_ERR_NVS_NO_FREE_PAGES || nv == ESP_ERR_NVS_NEW_VERSION_FOUND) {
        ESP_ERROR_CHECK(nvs_flash_erase());
        ESP_ERROR_CHECK(nvs_flash_init());
    }

    ESP_ERROR_CHECK(esp_netif_init());
    ESP_ERROR_CHECK(esp_event_loop_create_default());

    // 우선순위: UART 초기화(내부 RX 태스크는 모듈에서 prio 고정) → 세션 → 네트워크/HTTP
    xTaskCreate(task_uart_link,   "t_uart",    4096, NULL, 8, NULL);
    xTaskCreate(task_session_mgr, "t_session", 4096, NULL, 7, NULL);
    xTaskCreate(task_net_http,    "t_http",    4096, NULL, 6, NULL);
	
    // 필요 시 켜세요(지속 루프)
    xTaskCreate(task_metrics,   "t_metrics", 4096, NULL, 3, NULL);
}
