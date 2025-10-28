/*
 * wifi_mgr.h
 *
 *  Created on: 2025. 10. 26.
 *  Updated on: 2025. 10. 28.
 *      Author: Park Joo Hyun
 */

#ifndef MAIN_INCLUDE_WIFI_MGR_H_
#define MAIN_INCLUDE_WIFI_MGR_H_

#include <stdbool.h>

#include "esp_err.h"
#include "freertos/FreeRTOS.h"
#include "freertos/event_groups.h"


typedef struct{
	const char* ssid;
	const char* pass;
	bool ap_failback;
}wifi_mgr_config_t;

esp_err_t  	wifi_mgr_start(const wifi_mgr_config_t* cfg);
bool		wifi_mgr_wait_ip(TickType_t to);

#endif /* MAIN_INCLUDE_WIFI_MGR_H_ */
