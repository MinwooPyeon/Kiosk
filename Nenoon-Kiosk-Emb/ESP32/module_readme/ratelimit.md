# RateLimit 모듈 설명서 (ESP-IDF 5.5.1)

> 본 문서는 제공된 `ratelimit.h` / `ratelimit.c` 소스를 기준으로 작성되었습니다. 각 **공개 API 함수별로 구현 코드**를 바로 아래에 **붙여서** 설명합니다. (ESP-IDF 공식 API만 사용)

---

## 0. 파일 구성

- `ratelimit.h` — 공개 인터페이스(구성체/프로토타입)
- `ratelimit.c` — 내부 구조체/헬퍼/구현

---

## 1) ratelimit.h (전체)

```c
/*
 * ratelimit.h
 *
 *  Created on: 2025. 10. 26.
 *  Updated on: 2025. 10. 28.
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
    uint32_t    capacity;
    uint32_t    refill_tokens;
    uint32_t    period_ms;
    bool        start_full;
}ratelimit_config_t;

/*=====static life cycle=====*/
size_t      ratelimit_static_size(void);
esp_err_t   ratelimit_init(ratelimit_t* rl, const ratelimit_config_t* cfg);
void        ratelimit_deinit(ratelimit_t* rl);

/*=====dynamic life cycle=====*/
esp_err_t   ratelimit_create(const ratelimit_config_t* cfg, ratelimit_t** out);
void        ratelimit_destroy(ratelimit_t* rl);

/*=====function=====*/
esp_err_t ratelimit_take(ratelimit_t* rl, uint32_t n, TickType_t timeout);
esp_err_t ratelimit_get_available(ratelimit_t* rl, uint32_t* out_tokens);
esp_err_t ratelimit_reset(ratelimit_t* rl, bool to_full);
esp_err_t ratelimit_reconfigure(ratelimit_t* rl, const ratelimit_config_t* cfg);

#endif /* MAIN_INCLUDE_RATELIMIT_H_ */
```

---

## 2) ratelimit.c — 내부 구조/헬퍼

### 2.1 내부 상태 구조체

```c
#define TAG "rate_limit"

struct ratelimit_s{
    uint32_t            capacity;
    uint32_t            refill_tokens;
    uint64_t            period_us;
    
    uint32_t            tokens;
    int64_t             last_refill_us;
    
    SemaphoreHandle_t   mtx;
    bool                inited;
};
```

- `capacity`: 버킷 최대 토큰 수
- `refill_tokens`: 주기당 충전되는 토큰 수
- `period_us`: 리필 주기 (µs)
- `tokens`: 현재 보유 토큰
- `last_refill_us`: 마지막 리필 시각 (`esp_timer_get_time()`)
- `mtx`: FreeRTOS mutex
- `inited`: 초기화 여부

### 2.2 헬퍼 함수 (전체 코드)

```c
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
```

- **리필 로직**: `elapsed / period_us` 만큼의 정수 주기가 경과했을 때만 `refill_tokens * periods` 만큼 누적 충전 → `capacity`로 클램핑
- **락 유틸**: 타임드 락으로 교착/무한 대기 방지
- **구성 검증**: `capacity/refill/period`는 모두 양수여야 함

---

## 3) Static Life Cycle API

### 3.1 `size_t ratelimit_static_size(void)`

```c
size_t  ratelimit_static_size(void){
    return sizeof(ratelimit_t);  
}
```

**설명**: 정적 할당 시 필요한 바이트 수를 반환합니다.

---

### 3.2 `esp_err_t ratelimit_init(ratelimit_t* rl, const ratelimit_config_t* cfg)`

```c
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
```

**핵심 포인트**
- µs 단위로 주기를 변환 (`period_us`)
- 시작 시점 토큰 설정 (`start_full`)
- 뮤텍스 생성 실패 시 `ESP_ERR_NO_MEM`

---

### 3.3 `void ratelimit_deinit(ratelimit_t* rl)`

```c
void ratelimit_deinit(ratelimit_t* rl){
    if (!rl || !rl->inited) return;
    if (rl->mtx) {
        vSemaphoreDelete(rl->mtx);
        rl->mtx = NULL;
    }
    rl->inited = false;
}
```

**설명**: 내부 리소스 해제 및 상태 플래그 리셋.

---

## 4) Dynamic Life Cycle API

### 4.1 `esp_err_t ratelimit_create(const ratelimit_config_t* cfg, ratelimit_t** out)`

```c
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
```

**설명**: 동적 메모리 할당 + `init` 호출. 실패 시 누수 없이 정리.

---

### 4.2 `void ratelimit_destroy(ratelimit_t* rl)`

```c
void ratelimit_destroy(ratelimit_t* rl)
{
    if (!rl) return;
    ratelimit_deinit(rl);
    free(rl);
}
```

**설명**: `deinit` 후 메모리 해제.

---

## 5) 기능 API

### 5.1 `esp_err_t ratelimit_take(ratelimit_t* rl, uint32_t n, TickType_t timeout)`

```c
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
```

**동작 설명**
- 매 시도마다 **락 획득 → 리필 → 토큰 검사/차감**을 수행
- 부족하면 다음 리필 시점까지 **최소 대기** (`us_to_next` → `wait_ticks`)
- 전체 `timeout` 예산을 초과하면 `ESP_ERR_INVALID_STATE` 반환

---

### 5.2 `esp_err_t ratelimit_get_available(ratelimit_t* rl, uint32_t* out_tokens)`

```c
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
```

**설명**: 현재 시점 기준으로 리필을 반영한 후 잔여 토큰 수를 반환.

---

### 5.3 `esp_err_t ratelimit_reset(ratelimit_t* rl, bool to_full)`

```c
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
```

**설명**: 버킷을 가득 채우거나 비우고, 리필 기준 시각을 현재로 재설정.

---

### 5.4 `esp_err_t ratelimit_reconfigure(ratelimit_t* rl, const ratelimit_config_t* cfg)`

```c
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
```

**설계 포인트**
- 재구성 직전까지의 리필을 반영해 **정확도** 유지
- 새 `capacity`에 맞게 `tokens` 보정
- `last_refill_us`는 유지하여 미세 타이밍 보존

---

## 6) 에러 코드 표

| 함수 | 주요 반환값 |
|---|---|
| `*_init/create/reconfigure/reset` | `ESP_OK`, `ESP_ERR_INVALID_ARG`, `ESP_ERR_NO_MEM`, `ESP_ERR_TIMEOUT`(락 실패시) |
| `ratelimit_take` | `ESP_OK`, `ESP_ERR_INVALID_ARG`, `ESP_ERR_TIMEOUT`(락 실패), `ESP_ERR_INVALID_STATE`(토큰 부족으로 전체 타임아웃 소진) |
| `ratelimit_get_available` | `ESP_OK`, `ESP_ERR_INVALID_ARG`, `ESP_ERR_TIMEOUT` |

---

## 7) 사용 예시 (의사코드)

```c
ratelimit_t* rl = NULL;
ratelimit_config_t cfg = {
    .capacity = 10,
    .refill_tokens = 2,
    .period_ms = 1000,
    .start_full = true,
};

ESP_ERROR_CHECK(ratelimit_create(&cfg, &rl));

for (;;) {
    if (ratelimit_take(rl, 1, pdMS_TO_TICKS(500)) == ESP_OK) {
        // 1회 작업 수행
        do_work_once();
    } else {
        // 타임아웃 → 다음 루프에서 재시도
    }
}
```

---

## 8) 주의/권장 사항

- **멀티태스크 접근 필수 보호**: 제공된 API 외 직접 필드 접근 금지
- **주기/충전량 설계**: `refill_tokens/period_ms`의 비율이 실제 처리량과 맞도록 튜닝
- **장시간 대기**: `ratelimit_take()`의 `timeout`은 태스크 응답성에 영향. 시스템 워치독 정책 고려
- **로그 레벨**: `TAG`는 `"rate_limit"`, 필요 시 `menuconfig`에서 LOG 레벨 조정

---

**(끝)**
