/*
 * ratelimit.c
 *
 *  Created on: 2025. 10. 26.
 *	Updated on: 2025. 10. 28.
 *      Author: Park Joo Hyun
 */

#include "ratelimit.h"

#include <string.h>

#include "esp_log.h"
#include "esp_timer.h"
#include "freertos/semphr.h"

#define TAG "rate_limit"

struct ratelimit_s{
	uint32_t 			capacity;
	uint32_t 			refill_tokens;
	uint64_t 			period_us;
	
	uint32_t 			tokens;
	int64_t 			last_refill_us;
	
	SemaphoreHandle_t 	mtx;
	bool				inited;
};

/*=====Helper=====*/
static inline void rl_refill_locked(ratelimit_t* rl, int64_t now_us){
	if(now_us <= rl->last_refill_us) return;
	
	uint64_t elapsed = (uint64_t)(now_us - rl->last_refill_us);
	if(elapsed < rl->period_us) return;
	
	uint64_t periods = elapsed / rl->period_us;
	if(periods == 0) return;
	
	uint64_t add = periods * rl->refill_tokens;
	uint64_t new_tokens = rl->tokens + add;
	rl->tokens = (new_tokens > rl->capacity)?rl->capacity : (uint32_t)new_tokens;
	
	rl->last_refill_us += (int64_t)(periods*rl->period_us);
}

static esp_err_t rl_lock_timed(SemaphoreHandle_t mtx, TickType_t to){
	if(!mtx) return ESP_ERR_INVALID_ARG;
	if(xSemaphoreTake(mtx, to)!= pdTRUE) return ESP_ERR_TIMEOUT;
	return ESP_OK;
}

static void rl_unlock(SemaphoreHandle_t mtx)
{
    xSemaphoreGive(mtx);
}

static esp_err_t rl_check_cfg(const ratelimit_config_t* cfg)
{
    if (!cfg) return ESP_ERR_INVALID_ARG;
    if (cfg->capacity == 0 || cfg->refill_tokens == 0 || cfg->period_ms == 0) {
        return ESP_ERR_INVALID_ARG;
    }
    return ESP_OK;
}

/*=====Static Life Cycle=====*/
size_t 	ratelimit_static_size(void){
	return sizeof(ratelimit_t);	
}

esp_err_t ratelimit_init(ratelimit_t* rl, const ratelimit_config_t* cfg){
	esp_err_t err = rl_check_cfg(cfg);
    if (err != ESP_OK) {
        ESP_LOGE(TAG, "init: invalid config");
        return err;
    }
    if (!rl) return ESP_ERR_INVALID_ARG;

    memset(rl, 0, sizeof(*rl));
    rl->capacity      = cfg->capacity;
    rl->refill_tokens = cfg->refill_tokens;
    rl->period_us     = (uint64_t)cfg->period_ms * 1000ULL;
    rl->tokens        = cfg->start_full ? cfg->capacity : 0u;
    rl->last_refill_us = esp_timer_get_time();

    rl->mtx = xSemaphoreCreateMutex();
    if (!rl->mtx) {
        ESP_LOGE(TAG, "init: mutex create failed");
        return ESP_ERR_NO_MEM;
    }

    rl->inited = true;
    ESP_LOGI(TAG, "init: cap=%u refill=%u per=%ums start=%s",
             (unsigned)rl->capacity, (unsigned)rl->refill_tokens,
             (unsigned)cfg->period_ms, cfg->start_full ? "full" : "empty");
    return ESP_OK;
}

void ratelimit_deinit(ratelimit_t* rl){
	if (!rl || !rl->inited) return;
    if (rl->mtx) {
        vSemaphoreDelete(rl->mtx);
        rl->mtx = NULL;
    }
    rl->inited = false;
}

/*=====dynamic Life Cycle=====*/

esp_err_t ratelimit_create(const ratelimit_config_t* cfg, ratelimit_t** out)
{
    if (!out) return ESP_ERR_INVALID_ARG;
    *out = NULL;

    esp_err_t err = rl_check_cfg(cfg);
    if (err != ESP_OK) return err;

    ratelimit_t* rl = (ratelimit_t*)calloc(1, sizeof(ratelimit_t));
    if (!rl) return ESP_ERR_NO_MEM;

    err = ratelimit_init(rl, cfg);
    if (err != ESP_OK) {
        free(rl);
        return err;
    }
    *out = rl;
    return ESP_OK;
}

void ratelimit_destroy(ratelimit_t* rl)
{
    if (!rl) return;
    ratelimit_deinit(rl);
    free(rl);
}

/*=====function=====*/
esp_err_t ratelimit_take(ratelimit_t* rl, uint32_t n, TickType_t timeout)
{
    if (!rl || !rl->inited || n == 0) return ESP_ERR_INVALID_ARG;

    // lock (we use the same timeout both for lock and waiting for tokens)
    TickType_t deadline = xTaskGetTickCount() + timeout;
    while (true) {
        esp_err_t err = rl_lock_timed(rl->mtx, timeout);
        if (err != ESP_OK) return err;
        // refresh
        int64_t now = esp_timer_get_time();
        rl_refill_locked(rl, now);

        if (rl->tokens >= n) {
            rl->tokens -= n;
            rl_unlock(rl->mtx);
            return ESP_OK;
        }

        // not enough tokens
        if (timeout == 0) {
            rl_unlock(rl->mtx);
            return ESP_ERR_INVALID_STATE; // immediate deny (non-blocking)
        }

        // compute remaining time to wait in ticks
        // estimate time to next token batch
        // We wait at least until at least one token could arrive.
        uint64_t us_to_next = 0;
        if (rl->period_us > 0) {
            uint64_t elapsed = (uint64_t)(now - rl->last_refill_us);
            uint64_t rem = (elapsed % rl->period_us);
            us_to_next = (rem == 0) ? rl->period_us : (rl->period_us - rem);
        }
        // convert to ticks (ceil)
        TickType_t wait_ticks = pdMS_TO_TICKS((us_to_next + 999)/1000);
        if (wait_ticks == 0) wait_ticks = 1;

        rl_unlock(rl->mtx);

        // Check remaining overall timeout
        TickType_t now_ticks = xTaskGetTickCount();
        if (now_ticks >= deadline) return ESP_ERR_INVALID_STATE; // time budget exhausted

        TickType_t remain = deadline - now_ticks;
        if (wait_ticks > remain) wait_ticks = remain;

        vTaskDelay(wait_ticks);
        // loop and try again with reduced timeout
        now_ticks = xTaskGetTickCount();
        if (now_ticks >= deadline) return ESP_ERR_INVALID_STATE;

        timeout = deadline - now_ticks; // update residual timeout for lock
    }
}

esp_err_t ratelimit_get_available(ratelimit_t* rl, uint32_t* out_tokens)
{
    if (!rl || !rl->inited || !out_tokens) return ESP_ERR_INVALID_ARG;

    esp_err_t err = rl_lock_timed(rl->mtx, 0);
    if (err != ESP_OK) return err;

    int64_t now = esp_timer_get_time();
    rl_refill_locked(rl, now);
    *out_tokens = rl->tokens;

    rl_unlock(rl->mtx);
    return ESP_OK;
}

esp_err_t ratelimit_reset(ratelimit_t* rl, bool to_full)
{
    if (!rl || !rl->inited) return ESP_ERR_INVALID_ARG;
    esp_err_t err = rl_lock_timed(rl->mtx, pdMS_TO_TICKS(50));
    if (err != ESP_OK) return err;

    rl->tokens = to_full ? rl->capacity : 0u;
    rl->last_refill_us = esp_timer_get_time();

    rl_unlock(rl->mtx);
    return ESP_OK;
}

esp_err_t ratelimit_reconfigure(ratelimit_t* rl, const ratelimit_config_t* cfg)
{
    esp_err_t err = rl_check_cfg(cfg);
    if (err != ESP_OK) return err;
    if (!rl || !rl->inited) return ESP_ERR_INVALID_ARG;

    err = rl_lock_timed(rl->mtx, pdMS_TO_TICKS(50));
    if (err != ESP_OK) return err;

    // Keep fractional elapsed by adjusting last_refill_us proportionally
    int64_t now = esp_timer_get_time();
    rl_refill_locked(rl, now);

    rl->capacity      = cfg->capacity;
    rl->refill_tokens = cfg->refill_tokens;
    rl->period_us     = (uint64_t)cfg->period_ms * 1000ULL;
    if (rl->tokens > rl->capacity) rl->tokens = rl->capacity;
    if (!cfg->start_full && rl->tokens > rl->capacity) rl->tokens = rl->capacity;
    // don't touch last_refill_us: preserves remainder for precision

    rl_unlock(rl->mtx);
    ESP_LOGI(TAG, "reconfig: cap=%u refill=%u per=%ums start=%s",
             (unsigned)cfg->capacity, (unsigned)cfg->refill_tokens,
             (unsigned)cfg->period_ms, cfg->start_full ? "full" : "keep");
    return ESP_OK;
}
