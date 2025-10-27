# Cert Store 설명서

## 1. Header File

### 1.1 `cert_store_cfg_t` — 설정 값

> Cert Store 모듈의 루트 신뢰(PEM/번들) 및 SPKI 핀 구성을 담는 구조체.

```c
typedef struct{
    const char* ca_pem;                 // NULL이면 crt_bundle 사용 가능
    uint8_t     pin_spki_sha256[32];    // 32바이트 해시(모두 0이면 미사용)
    bool        have_pin;               // true면 SPKI 핀 사용
    bool        use_crt_bundle;         // true면 ESP x509 bundle 사용
} cert_store_cfg_t;
```

- **ca_pem**: 루트 CA PEM 문자열(정적/롬 권장).
- **pin_spki_sha256**: 서버 인증서 **SPKI(SubjectPublicKeyInfo) DER**의 SHA‑256 결과(32바이트).
- **have_pin**: 핀 사용 여부. `pin_spki_sha256`이 all-zero가 아니면 `true` 권장.
- **use_crt_bundle**: `esp_crt_bundle_attach` 사용 여부.

### 1.2 Public API

#### `void cert_store_init(const cert_store_cfg_t* cfg)`
- 전역 설정 초기화. `ca_pem / use_crt_bundle / pin`을 저장.

#### `esp_err_t cert_store_attach(esp_http_client_config_t* http_cfg)`
- 공개 필드만 사용해 **HTTP 클라이언트에 신뢰 루트 적용**  
  - `ca_pem` 존재 시 `http_cfg->cert_pem = ca_pem`  
  - 아니고 `use_crt_bundle==true`면 `http_cfg->crt_bundle_attach = esp_crt_bundle_attach`

#### `esp_err_t cert_store_preverify_spki(const char* url)`
- **사전 TLS 접속(preflight)** 으로 서버 인증서의 **SPKI 해시**를 계산해 핀과 비교.
- 반환:
  - `ESP_OK`(성공/핀 미사용), `ESP_ERR_NOT_FOUND`(핀 불일치),
  - `ESP_ERR_INVALID_STATE`(미초기화), `ESP_ERR_INVALID_ARG`(URL 에러),
  - `ESP_ERR_NOT_SUPPORTED`(번들만으로 preverify 요청), 그 외 `ESP_FAIL`

---

## 2. Source File

### 2.1 전역 상태

```c
static struct{
    const char* ca_pem;
    uint8_t     pin_spki_sha256[32];
    bool        have_pin;
    bool        use_crt_bundle;
    bool        inited;
} g_cs = {0};
```
- 모듈 수준에서 **단일 인스턴스** 가정. 초기화 후 런타임에 참조.

### 2.2 TLS 컨텍스트

```c
typedef struct{
    mbedtls_entropy_context  entropy;
    mbedtls_ctr_drbg_context ctr_drbg;
    mbedtls_ssl_context      ssl;
    mbedtls_ssl_config       conf;
    mbedtls_x509_crt         cacert;
    mbedtls_net_context      net;
    bool                     cacert_loaded;
    bool                     net_connected;
    bool                     ssl_setup;
} cs_tls_ctx_t;
```
- preverify 과정에 필요한 mbedTLS 상태를 **한 곳에 캡슐화**.

### 2.3 헬퍼

- `all_zero32` : 32바이트 배열이 모두 0인지 검사 → 핀 사용 유무 판정.
- `hex2bytes32`: HEX 문자열을 32바이트로 변환(옵션 유틸).
- `parse_host_port_from_url`: `https://host:port/path` → `host`, `port(기본 443)` 추출.
- `spki_sha256_from_crt`:
  - `mbedtls_pk_write_pubkey_der()`로 **SPKI DER** 추출
  - DER 바이트에 대해 `mbedtls_sha256()` 계산 → 32B 해시 반환.

### 2.4 내부 단계 함수 (단일 책임)

#### `cs_tls_init(cs_tls_ctx_t* c)`
- 엔트로피/DRBG/SSL/CFG/X509/NET 초기화 + DRBG 시드.

#### `cs_tls_load_trust(cs_tls_ctx_t* c)`
- `ca_pem`이 있으면 `mbedtls_x509_crt_parse()`로 로드 → `cacert_loaded=true`  
- `use_crt_bundle==true`만 있는 경우, **현 구현에서는 preverify 미지원** → `ESP_ERR_NOT_SUPPORTED`

#### `cs_tls_setup_ssl(cs_tls_ctx_t* c, const char* host)`
- `mbedtls_ssl_config_defaults()` → `VERIFY_REQUIRED` → `ssl_setup=true` → `set_hostname`

#### `cs_tls_connect_tcp(cs_tls_ctx_t* c, const char* host, int port)`
- `mbedtls_net_connect()`로 TCP 연결 후 BIO 설정.

#### `cs_tls_handshake(cs_tls_ctx_t* c)`
- `mbedtls_ssl_handshake()` 수행.

#### `cs_tls_verify_chain(cs_tls_ctx_t* c)`
- `mbedtls_ssl_get_verify_result()`가 0인지 확인.

#### `cs_tls_get_peer_spki_sha256(cs_tls_ctx_t* c, uint8_t out32[32])`
- 피어 인증서에서 SPKI 해시(32B) 계산.

#### `cs_tls_cleanup(cs_tls_ctx_t* c)`
- `close_notify` → `net_free` → `ssl_free` → `conf_free` → `x509_free` → `drbg_free` → `entropy_free`

### 2.5 Public API 동작 요약

#### `cert_store_init(const cert_store_cfg_t* cfg)`
- `g_cs` 초기화 및 구성 저장, 현재 구성 로깅.

#### `cert_store_attach(esp_http_client_config_t* http_cfg)`
- **공개 필드만** 사용하여 신뢰 루트 적용: `cert_pem` 또는 `crt_bundle_attach`

#### `cert_store_preverify_spki(const char* url)`
- URL 파싱 → ①init → ②trust → ③ssl setup → ④tcp connect → ⑤handshake → ⑥verify → ⑦SPKI 해시  
- `memcmp(spki, g_cs.pin_spki_sha256, 32)`가 일치해야 성공.

---

## 3. 사용 예

```c
#include "cert_store.h"
#include "esp_http_client.h"
#include "esp_log.h"

extern const char ROOT_CA_PEM[]; // "-----BEGIN CERTIFICATE-----\n..."

void http_with_pinning(void)
{
    cert_store_cfg_t cs = {
        .ca_pem          = ROOT_CA_PEM,
        .use_crt_bundle  = false,
        .pin_spki_sha256 = { /* 32B SHA-256 해시(필요 시) */ },
        .have_pin        = true,   // 핀 미사용이면 false
    };
    cert_store_init(&cs);

    // (선택) 사전 핀 검증
    ESP_ERROR_CHECK(cert_store_preverify_spki("https://api.example.com:443/health"));

    esp_http_client_config_t cfg = {
        .url = "https://api.example.com/health",
    };
    ESP_ERROR_CHECK(cert_store_attach(&cfg));

    esp_http_client_handle_t cli = esp_http_client_init(&cfg);
    ESP_ERROR_CHECK(esp_http_client_perform(cli));
    esp_http_client_cleanup(cli);
}
```

- 번들을 쓰려면 `cs.ca_pem=NULL`, `use_crt_bundle=true`로 설정하고 `cert_store_attach()`에서 `.crt_bundle_attach`가 자동 지정됨.
- **주의**: 현 구현에서 preverify는 PEM만 지원. 번들 + preverify가 필요하면 `esp-tls` 경로 확장을 고려.

---

## 4. Kconfig & CMake

### 4.1 Kconfig (menuconfig)

```
Component config → mbedTLS → Certificates
  [*] Enable trusted root certificate bundle
  [*] Use default certificate bundle
```
- 번들을 코드에서 사용하려면 위 옵션을 활성화하고 `#include "esp_crt_bundle.h"`.

### 4.2 CMake

```cmake
idf_component_register(
  SRCS
    "src/cert_store.c"
  INCLUDE_DIRS
    "include"
  REQUIRES
    esp_http_client
    mbedtls
)
```

---

## 5. 에러 코드 & 로그

- `ESP_ERR_INVALID_STATE` : `cert_store_init()` 미호출 등 초기화 누락
- `ESP_ERR_INVALID_ARG`   : URL 파싱 실패/NULL 인자
- `ESP_ERR_NOT_SUPPORTED` : 번들만으로 preverify 요청(현 구현 정책)
- `ESP_ERR_NOT_FOUND`     : **SPKI 핀 불일치**
- `ESP_FAIL`              : TLS 설정/핸드셰이크/검증 등 일반 실패

**로그 태그**: `TAG="cert_store"`. 보안 관점에서 **핀 불일치 이벤트**는 모니터링 권장.

---

## 6. 설계 메모

- **공개 API만 사용**: `esp_http_client`의 `cert_pem`/`crt_bundle_attach` + **순정 mbedTLS**.  
- **내부 헤더 비사용**: `esp_transport.h` 등 비공개 심볼 의존 없음.  
- **단일 책임 분리**: 테스트/유지보수 용이.  
- **보안성**: 루트 신뢰 + SPKI 핀 이중 가드.
