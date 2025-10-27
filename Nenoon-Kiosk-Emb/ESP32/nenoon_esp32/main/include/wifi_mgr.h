/*
 * wifi_mgr.h
 *
 *  Created on: 2025. 10. 26.
 *      Author: SSAFY
 */

#ifndef MAIN_INCLUDE_WIFI_MGR_H_
#define MAIN_INCLUDE_WIFI_MGR_H_

#include <stdbool.h>
#include <stdint.h>
#include "esp_err.h"

typedef enum{
	WIFI_MGR_STATE_IDLE,
	WIFI_MGR_STATE_PROVISIONING,
	WIFI_MGR_STATE_CONNECTING,
  	WIFI_MGR_STATE_CONNECTED,
  	WIFI_MGR_STATE_DISCONNECTED,
  	WIFI_MGR_STATE_ERROR,
}wifi_mgr_state_t;

typedef struct{
	bool enable_ipv6;
  	bool enable_powersave;   // PS_MIN_MODEM 권장
  	uint32_t max_retry;      // 0=무한
  	uint32_t first_backoff_ms; // 지수 백오프 시작값
}wifi_mgr_cfg_t;

typedef void(*wifi_mgr_on_state_cb)(wifi_mgr_state_t s, void* user);

esp_err_t wifi_mgr_init(const wifi_mgr_cfg_t* cfg);
esp_err_t wifi_mgr_start(void);                  // netif/event 루프 + STA start
esp_err_t wifi_mgr_stop(void);
wifi_mgr_state_t wifi_mgr_get_state(void);
esp_err_t wifi_mgr_get_ip(char* buf, size_t len); // "192.168.0.10" 등

esp_err_t wifi_mgr_force_reprovision(void);
void wifi_mgr_set_state_callback(wifi_mgr_on_state_cb cb, void* user);

#endif /* MAIN_INCLUDE_WIFI_MGR_H_ */
