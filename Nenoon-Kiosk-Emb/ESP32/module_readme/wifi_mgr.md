# Wi-Fi Manager 설명서

## 1. Header File

### 1.1 `wifi_mgr_state_t` — 상태 값

> Wi-Fi Manager의 상태를 나타내는 열거형.

```c
typedef enum {
    WIFI_MGR_STATE_IDLE,
    WIFI_MGR_STATE_PROVISIONING,
    WIFI_MGR_STATE_CONNECTING,
    WIFI_MGR_STATE_CONNECTED,
    WIFI_MGR_STATE_DISCONNECTED,
    WIFI_MGR_STATE_ERROR,
} wifi_mgr_state_t;
```

- **IDLE**: 대기 상태  
- **PROVISIONING**: 프로비저닝 진행 상태  
- **CONNECTING**: 연결 시도 중  
- **CONNECTED**: 연결 완료  
- **DISCONNECTED**: 연결 끊김  
- **ERROR**: 오류 상태

---

### 1.2 `wifi_mgr_cfg_t` — 설정 값

> 동작 옵션 및 재시도 정책을 담는 설정 구조체.

```c
typedef struct {
    bool     enable_ipv6;        // IPv6 링크로컬 사용
    bool     enable_powersave;   // 전력 절약 모드(WIFI_PS_MIN_MODEM 권장)
    uint32_t max_retry;          // 최대 재시도 횟수 (0 = 무한)
    uint32_t first_backoff_ms;   // 첫 백오프(ms), 이후 지수 백오프
} wifi_mgr_cfg_t;
```

- **enable_ipv6**: IPv6 링크로컬 주소 사용 여부  
- **enable_powersave**: 전력 절약 모드 사용 여부  
- **max_retry**: 최대 재시도 횟수(0은 무한 재시도)  
- **first_backoff_ms**: 최초 재시도 지연(ms). 이후 `<< 1` 지수 증가

---

### 1.3 상태 콜백 시그니처

```c
typedef void (*wifi_mgr_on_state_cb)(wifi_mgr_state_t s, void* user);
```

- 상태 변경 시 호출되는 콜백. `user`는 등록 시 전달한 사용자 포인터.

---

## 2. Source File

### 2.1 `req_t` — 내부 요청 타입

> 이벤트 핸들러에서 직접 지연/재시도를 하지 않고, **전용 태스크**가 처리할 일을 큐로 넘기기 위한 요청 타입.

```c
typedef enum {
    REQ_NONE,
    REQ_CONNECT,
    REQ_STOP
} req_t;
```

- **REQ_NONE**: 요청 없음  
- **REQ_CONNECT**: 연결(재연결) 수행 요청  
- **REQ_STOP**: 재연결 태스크 종료 요청

---

### 2.2 정적 변수(모듈 상태)

```c
static EventGroupHandle_t s_eg;
static QueueHandle_t      s_reqq;
static TaskHandle_t       s_reconn_task;
static esp_netif_t*       s_netif_sta;

static wifi_mgr_state_t   s_state = WIFI_MGR_STATE_IDLE;
static uint32_t           s_retry_count = 0;
static wifi_mgr_cfg_t     s_cfg = {
    .enable_ipv6       = false,
    .enable_powersave  = true,
    .max_retry         = 0,
    .first_backoff_ms  = 500,
};

static wifi_mgr_on_state_cb s_onstate      = NULL;
static void*                s_onstate_user = NULL;
```

- **s_eg**: ONLINE 비트를 set/clear하여 다른 태스크가 대기/처리 가능  
- **s_reqq**: 재연결/중지 요청을 전달하는 큐  
- **s_reconn_task**: 지수 백오프 재연결을 수행하는 전용 태스크  
- **s_netif_sta**: `esp_netif_create_default_wifi_sta()`로 생성한 STA 인터페이스  
- **s_state/s_retry_count/s_cfg**: 상태, 재시도 횟수, 설정 값  
- **s_onstate(_user)**: 상태 변경 콜백과 사용자 포인터

---

### 2.3 내부 헬퍼

#### `set_state` — 상태 전이 + 콜백

```c
static void set_state(wifi_mgr_state_t st) {
    s_state = st;
    if (s_onstate) s_onstate(st, s_onstate_user);
}
```

- **역할**: 상태 저장 후, 등록된 콜백에 통지  
- **가이드**: 콜백 내부에서 **긴 블로킹 작업 지양**

#### `post_req` — 요청 큐 삽입

```c
static void post_req(req_t r) {
    if (s_reqq) (void)xQueueSend(s_reqq, &r, 0);
}
```

- **역할**: 재연결 등 긴 작업을 **태스크로 위임**(이벤트 핸들러 경량화)

---

### 2.4 이벤트 핸들러 (Wi-Fi/IP → 상태 전이)

```c
static void wifi_event_handler(void* arg, esp_event_base_t base, int32_t id, void* data) {
    if (base == WIFI_EVENT) {
        switch (id) {
        case WIFI_EVENT_STA_START:
            ESP_LOGI(TAG, "WIFI_EVENT_STA_START");
            set_state(WIFI_MGR_STATE_CONNECTING);
            esp_wifi_connect();
            break;

        case WIFI_EVENT_STA_DISCONNECTED:
            wifi_event_sta_disconnected_t* ev = (wifi_event_sta_disconnected_t*)data;
            ESP_LOGW(TAG, "Disconnected: reason=%d", ev ? ev->reason : -1);
            xEventGroupClearBits(s_eg, WIFI_BIT_ONLINE);
            set_state(WIFI_MGR_STATE_DISCONNECTED);
            post_req(REQ_CONNECT);
            break;

        default:
            break;
        }
    } else if (base == IP_EVENT) {
        if (id == IP_EVENT_STA_GOT_IP) {
            ip_event_got_ip_t* ev = (ip_event_got_ip_t*)data;
            char ip[16] = {0};
            if (ev) snprintf(ip, sizeof(ip), IPSTR, IP2STR(&ev->ip_info.ip));
            ESP_LOGI(TAG, "Got IP: %s", ip);

            s_retry_count = 0;
            xEventGroupSetBits(s_eg, WIFI_BIT_ONLINE);
            set_state(WIFI_MGR_STATE_CONNECTED);
        }
    }
}
```

- **STA_START**: 드라이버 시작 → `CONNECTING` 전이 → `esp_wifi_connect()`  
- **STA_DISCONNECTED**: 연결 끊김 → ONLINE 비트 clear → `DISCONNECTED` 전이 → **재연결 요청(큐)**  
- **GOT_IP**: DHCP 성공 → 재시도 카운트 리셋 → ONLINE 비트 set → `CONNECTED` 전이

---

### 2.5 재연결 태스크

```c
static void reconnect_task(void* arg) {
    req_t r;
    for (;;) {
        if (xQueueReceive(s_reqq, &r, portMAX_DELAY) != pdTRUE) continue;

        if (r == REQ_STOP) {
            ESP_LOGI(TAG, "Reconnect task: stop requested");
            break;
        }
        if (r != REQ_CONNECT) continue;

        // 이미 ONLINE이면 스킵
        if (xEventGroupGetBits(s_eg) & WIFI_BIT_ONLINE) continue;

        // 재시도 한도
        if (s_cfg.max_retry != 0 && s_retry_count >= s_cfg.max_retry) {
            ESP_LOGE(TAG, "Max retries reached (%" PRIu32 ")", s_retry_count);
            set_state(WIFI_MGR_STATE_ERROR);
            continue;
        }

        // 지수 백오프 (최대 2^6 배 캡)
        uint32_t shift    = (s_retry_count > 6) ? 6 : s_retry_count;
        uint32_t delay_ms = s_cfg.first_backoff_ms << shift;
        s_retry_count++;

        ESP_LOGW(TAG, "Reconnect in %" PRIu32 " ms (attempt #%" PRIu32 ")", delay_ms, s_retry_count);
        vTaskDelay(pdMS_TO_TICKS(delay_ms));

        // 재연결 시도
        esp_err_t er = esp_wifi_connect();
        if (er != ESP_OK) {
            ESP_LOGE(TAG, "esp_wifi_connect() err=%s", esp_err_to_name(er));
            post_req(REQ_CONNECT); // 즉시 다음 루프에서 재시도
        }
    }
    vTaskDelete(NULL);
}
```

- **흐름**: 큐 수신 → STOP 처리 or CONNECT 처리 → ONLINE 확인 → 한도/백오프 → `esp_wifi_connect()` 재시도  
- **장점**: 지연/재시도를 이벤트 핸들러에서 분리 → 시스템 지연/워치독 리스크 감소  
- **참고**: `PRIu32` 사용 시 `<inttypes.h>` 인클루드 필요

---

### 2.6 Public API 요약

#### `wifi_mgr_init(const wifi_mgr_cfg_t* cfg)`
- `esp_netif_init` / 이벤트 루프 생성  
- STA netif 생성 → `esp_wifi_init` / `WIFI_MODE_STA`  
- 파워세이브 설정(`WIFI_PS_MIN_MODEM` 권장)  
- Wi-Fi/IP 이벤트 핸들러 등록  
- 상태 `IDLE` 설정  
- 재연결 태스크 생성

#### `wifi_mgr_start(void)`
- `esp_wifi_start()`  
- (옵션) `esp_netif_create_ip6_linklocal()`  
- 1차 `esp_wifi_connect()` 시도, 실패 시 **태스크로 재시도 요청**

#### `wifi_mgr_stop(void)`
- 재연결 태스크에 `REQ_STOP` 송신  
- 이벤트 핸들러 언레지스터  
- `esp_wifi_stop()` 호출  
- 상태 `IDLE` 전이

#### `wifi_mgr_get_state(void)`
- 현재 상태 반환

#### `wifi_mgr_get_ip(char* buf, size_t len)`
- `esp_netif_get_ip_info()` → IPv4 문자열로 변환  
- `len < 16`이면 `ESP_ERR_INVALID_ARG`

#### `wifi_mgr_force_reprovision(void)`
- 현재는 **스텁**(프로비저닝 미구현): `PROVISIONING` → `ERROR`, `ESP_ERR_NOT_SUPPORTED` 반환  
  - 실제 구현 시 BLE/SoftAP Wi-Fi Provisioning Manager 연동 지점

#### `wifi_mgr_set_state_callback(wifi_mgr_on_state_cb cb, void* user)`
- 상태 변경 콜백과 사용자 포인터 등록

---

## 3. 사용 팁 & 권장 사항

- **핸들러는 항상 빠르게**: 지연은 전용 태스크에서 처리  
- **콜백은 가볍게**: 블로킹 I/O 지양, 필요 시 별도 태스크로 위임  
- **Kconfig 노출**: `max_retry`, `first_backoff_ms`, `enable_powersave` 등은 `menuconfig`에서 조절 가능하게  
- **메트릭 추가**(선택): 연결까지 걸린 시간, 재시도 횟수/사유, RSSI 주기 보고  
- **프로비저닝 통합**: `wifi_mgr_force_reprovision()`에 BLE/SoftAP 플로우 연결
