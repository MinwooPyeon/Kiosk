/*
 * ratelimit.c
 *
 *  Created on: 2025. 10. 26.
 *	Updated on: 2025. 10. 28.
 *      Author: Park Joo Hyun
 */

#include "ratelimit.h"
#include "esp_timer.h"

#include <stdlib.h>

#define TAG "rate_limit"

struct ratelimit_s{
	uint32_t 	cap;
	uint32_t 	tokens;
	uint32_t 	refill;
	uint32_t 	period_ms;
	uint64_t 	last_ms;
};

esp_err_t ratelimit_create(const ratelimit_config_t *cfg, ratelimit_t **out){
	if(!cfg||!out) return ESP_ERR_INVALID_ARG;
	
	ratelimit_t* rl = calloc(1, sizeof(*rl));
	rl->cap 		= cfg->capacity ? cfg->capacity : 5;
	rl->tokens 		= cfg->start_full ? rl->cap : 0;
	rl->refill 		= cfg->refill_tokens ? cfg->refill_tokens : 0;
	rl->refill 		= cfg->refill_tokens ? cfg->refill_tokens : 1;
    rl->period_ms 	= cfg->period_ms ? cfg->period_ms : 1000;
    rl->last_ms 	= esp_timer_get_time() / 1000ULL;
	
	*out 			= rl;
	return ESP_OK;
}


static void refill(ratelimit_t* rl){
	uint64_t now 	= esp_timer_get_time() / 10000ULL;
	uint64_t dt 	= now - rl->last_ms;
	
	if(dt >= rl->period_ms){
		uint32_t steps	= dt / rl->period_ms;
		uint64_t adv	= (uint64_t)steps * rl->period_ms;
		rl->last_ms		= rl->last_ms + adv;
		
		uint64_t add	= (uint64_t)steps * rl->refill;
		rl->tokens		= (rl->tokens + add > rl->cap) ? rl->cap : (rl->tokens + add);
	} 
}

esp_err_t ratelimit_take_nowait(ratelimit_t* rl, uint32_t n){
    if(!rl||n==0) return ESP_ERR_INVALID_ARG;
    refill(rl);
    if(rl->tokens < n) return ESP_ERR_INVALID_STATE;
    rl->tokens -= n; return ESP_OK;
}

void ratelimit_destroy(ratelimit_t* rl){ free(rl); }