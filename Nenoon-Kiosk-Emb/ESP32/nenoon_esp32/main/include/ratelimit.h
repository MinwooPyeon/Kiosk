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
#include "esp_err.h"

typedef struct ratelimit_s ratelimit_t;
typedef struct{
	uint32_t 	capacity;
	uint32_t 	refill_tokens;
	uint32_t 	period_ms;
	int 		start_full;
}ratelimit_config_t;

esp_err_t ratelimit_create(const ratelimit_config_t* cfg, ratelimit_t** out);
esp_err_t ratelimit_take_nowait(ratelimit_t* rl, uint32_t n);
void      ratelimit_destroy(ratelimit_t* rl);

#endif /* MAIN_INCLUDE_RATELIMIT_H_ */
