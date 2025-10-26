// app_main.c
#include "freertos/FreeRTOS.h"
#include "freertos/task.h"
#include "esp_system.h"
#include "esp_log.h"

void app_main(void) {
    while (1) {
        ESP_LOGI("MAIN", "tick");
        vTaskDelay(pdMS_TO_TICKS(1000));
    }
}
