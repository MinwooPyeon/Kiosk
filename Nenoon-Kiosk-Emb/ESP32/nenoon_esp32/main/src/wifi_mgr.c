/*
 * wifi_mgr.c
 *
 *  Created on: 2025. 10. 26.
 *  Updated on: 2025. 10. 27.
 *      Author: Park Joo Hyun
 */

#include "wifi_mgr.h"
 
#include <string.h>
 
#include "esp_event_base.h"
#include "esp_netif_types.h"
#include "esp_wifi.h"
#include "esp_event.h"
#include "esp_log.h"
#include "esp_netif.h"
 
#include "esp_wifi_types_generic.h"
#include "freertos/FreeRTOS.h"
#include "freertos/projdefs.h"
#include "freertos/task.h"
#include "freertos/event_groups.h"
#include "freertos/queue.h"
#include "portmacro.h"

#define TAG "wifi_mgr"

#define WIFI_BIT_ONLINE BIT0

typedef enum{
	REQ_NONE,
	REQ_CONNECT,
	REQ_STOP
}req_t;

static EventGroupHandle_t 	s_eg;
static QueueHandle_t 		s_reqq;
static TaskHandle_t 		s_reconn_task;
static esp_netif_t* 		s_netif_sta;

static wifi_mgr_state_t		s_state = WIFI_MGR_STATE_IDLE;
static uint32_t				s_retry_count = 0;
static wifi_mgr_cfg_t		s_cfg = {
	.enable_ipv6 = false,
	.enable_powersave = true,
	.max_retry = 0,
	.first_backoff_ms = 500,
};

static wifi_mgr_on_state_cb	s_onstate = NULL;
static void*				s_onstate_user = NULL;

static void set_state(wifi_mgr_state_t st){
	s_state = st;
	if(s_onstate) s_onstate(st, s_onstate_user);
}

static void post_req(req_t r){
	if(s_reqq) (void)xQueueSend(s_reqq, &r, 0);
}

static void wifi_event_handler(void* arg, esp_event_base_t base, int32_t id, void* data){
	if(base == WIFI_EVENT){
		switch(id){
		case WIFI_EVENT_STA_START:
			ESP_LOGI(TAG, "WIFI_EVENT_STA_START");
			set_state(WIFI_MGR_STATE_CONNECTING);
			esp_wifi_connect();
			break;
		case WIFI_EVENT_STA_DISCONNECTED:
			wifi_event_sta_disconnected_t* ev = (wifi_event_sta_disconnected_t*)data;
			ESP_LOGW(TAG, "Disconnected: reason=%d", ev?ev->reason : -1);
			xEventGroupClearBits(s_eg, WIFI_BIT_ONLINE);
			set_state(WIFI_MGR_STATE_DISCONNECTED);
			post_req(REQ_CONNECT);
			break;
		default:
			break;	
		}
	} else if(base == IP_EVENT){
		if(id == IP_EVENT_STA_GOT_IP){
			ip_event_got_ip_t* ev = (ip_event_got_ip_t*)data;
			char ip[16] = {0};
			if(ev) snprintf(ip, sizeof(ip), IPSTR, IP2STR(&ev->ip_info.ip));
			ESP_LOGI(TAG, "Got IP: %s", ip);
			s_retry_count = 0;
			xEventGroupSetBits(s_eg, WIFI_BIT_ONLINE);
			set_state(WIFI_MGR_STATE_CONNECTED);
		}
	}
}

static void reconnect_task(void* arg){
	req_t r;
	while(true){
		if(xQueueReceive(s_reqq, &r, portMAX_DELAY)!=pdTRUE) continue;
		
		if(r == REQ_STOP){
			ESP_LOGI(TAG, "Reconnect task: stop requested");
			break;
		}
		if(r != REQ_CONNECT) continue;
		
		EventBits_t bits = xEventGroupGetBits(s_eg);
		if(bits & WIFI_BIT_ONLINE) continue;
		
		if(s_cfg.max_retry != 0 && s_retry_count>=s_cfg.max_retry){
			ESP_LOGE(TAG, "Max retries reached (%" PRIu32")", s_retry_count);
			set_state(WIFI_MGR_STATE_ERROR);
			continue;
		}
		
		uint32_t shift 		= (s_retry_count > 6) ? 6 : s_retry_count;
		uint32_t delay_ms 	= s_cfg.first_backoff_ms << shift;
		s_retry_count++;
		
		ESP_LOGW(TAG, "Reconnect in %" PRIu32" ms (attempt #%" PRIu32")", delay_ms, s_retry_count);
		vTaskDelay(pdMS_TO_TICKS(delay_ms));
		
		esp_err_t er = esp_wifi_connect();
		if(er != ESP_OK){
			ESP_LOGE(TAG, "esp_wifi_connect() err=%s", esp_err_to_name(er));
			post_req(REQ_CONNECT);
		}
	}
	vTaskDelete(NULL);
}