/*
 * ratelimit.h
 *
 *  Created on: 2025. 10. 26.
 *	Updated on: 2025. 10. 28.
 *      Author: Park Joo Hyun
 */

#ifndef MAIN_INCLUDE_RATELIMIT_H_
#define MAIN_INCLUDE_RATELIMIT_H_

#include <stdint.h>
#include <stdbool.h>

#include "esp_err.h"
#include "freertos/FreeRTOS.h"

typedef struct ratelimit_s ratelimit_t;

typedef struct{
	uint32_t 	capacity;
	uint32_t 	refill_tokens;
	uint32_t 	period_ms;
	bool		start_full;
}ratelimit_config_t;

/*=====static life cycle=====*/
size_t 		ratelimit_static_size(void);
esp_err_t	ratelimit_init(ratelimit_t* rl, const ratelimit_config_t* cfg);
void		ratelimit_deinit(ratelimit_t* rl);

/*=====dynamic life cycle=====*/
esp_err_t	ratelimit_create(const ratelimit_config_t* cfg, ratelimit_t** out);
void		ratelimit_destroy(ratelimit_t* rl);

/*=====function=====*/
esp_err_t ratelimit_take(ratelimit_t* rl, uint32_t n, TickType_t timeout);
esp_err_t ratelimit_get_available(ratelimit_t* rl, uint32_t* out_tokens);
esp_err_t ratelimit_reset(ratelimit_t* rl, bool to_full);
esp_err_t ratelimit_reconfigure(ratelimit_t* rl, const ratelimit_config_t* cfg);

#endif /* MAIN_INCLUDE_RATELIMIT_H_ */
