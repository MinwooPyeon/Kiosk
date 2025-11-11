#include "debug_tx.h"
#include "link_io.h"     // linkio_send_frame(), linkio_init_uart() 등
#include "frame.h"       // FRAME_MAX_WIRE 등
#include "esp_log.h"
#include "driver/uart.h"
#include <string.h>
#include <stdlib.h>
#include <ctype.h>

static const char* TAG = "debug_tx";

/* === 1) RAW 바이트 전송 ===================================== */
esp_err_t debug_tx_send_raw(const void* data, size_t len)
{
    extern esp_err_t linkio_send_frame(uint8_t type, const uint8_t* payload, uint16_t plen);
    /* 원시 바이트만으로 IRQ 검증이 목적이면 프레임 말고 그대로 쓰는 경로가 필요.
       link_io.c에 다음 보조 함수를 추가해두면 좋습니다:
       int uart_link_write_bytes(const void* data, size_t len);  // uart_write_bytes 래핑
       여기서는 최소 구현으로 frame 없이 바로 전송하려면 link_io에 함수를 하나 더 두세요.
    */
    // 간단 대안: 테스트용 프레임(type=0xEE)에 원시 바이트를 넣어 보냄
    if(len > 0xFFFF) len = 0xFFFF; // 안전
    return linkio_send_frame(0xEE, (const uint8_t*)data, (uint16_t)len);
}

/* === 2) 고정 PING 프레임 ===================================== */
esp_err_t debug_tx_send_ping(void)
{
    static const char payload[] = "PING";
    return linkio_send_frame(0xF0, (const uint8_t*)payload, (uint16_t)sizeof(payload)-1);
}

/* === 3) 텍스트 payload 프레임 ================================= */
esp_err_t debug_tx_send_text(const char* text)
{
    if(!text) text = "";
    size_t n = strlen(text);
    if(n > 0xFFFF) n = 0xFFFF;
    return linkio_send_frame(0xF1, (const uint8_t*)text, (uint16_t)n);
}

/* === 4) HEX 문자열 → 원시 바이트 전송 ========================= */
static int hexval(int c){
    if('0'<=c && c<='9') return c-'0';
    if('a'<=c && c<='f') return c-'a'+10;
    if('A'<=c && c<='F') return c-'A'+10;
    return -1;
}
esp_err_t debug_tx_send_hex(const char* hex)
{
    if(!hex || !*hex) return ESP_ERR_INVALID_ARG;

    size_t L = strlen(hex);
    uint8_t* buf = (uint8_t*)malloc(L/2 + 1);
    if(!buf) return ESP_ERR_NO_MEM;

    size_t w = 0;
    for(size_t i=0;i+1<L;i+=2){
        int hi = hexval(hex[i]); int lo = hexval(hex[i+1]);
        if(hi<0 || lo<0){ free(buf); return ESP_ERR_INVALID_ARG; }
        buf[w++] = (uint8_t)((hi<<4)|lo);
    }
    esp_err_t ret = debug_tx_send_raw(buf, w);
    free(buf);
    return ret;
}

/* === 5) 간단 버스트 =========================================== */
esp_err_t debug_tx_burst(size_t cnt, uint32_t interval_ms)
{
    for(size_t i=0;i<cnt;i++){
        esp_err_t r = debug_tx_send_ping();
        if(r != ESP_OK){
            ESP_LOGE(TAG, "burst fail at %u/%u err=%d", (unsigned)i+1, (unsigned)cnt, (int)r);
            return r;
        }
        vTaskDelay(pdMS_TO_TICKS(interval_ms));
    }
    return ESP_OK;
}
