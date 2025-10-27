# Frame Parser 설명서

## 1. Header File

### 1.1 `frame_err_t` — 에러 코드

> Frame 모듈 전역에서 사용하는 오류 코드.

```c
typedef enum{
    FRAME_OK,
    FRAME_ERR_ARG,
    FRAME_ERR_OOB,
    FRAME_ERR_MAGIC,
    FRAME_ERR_CRC,
    FRAME_ERR_TRUNC
} frame_err_t;
```

- **FRAME_OK** : 정상 동작  
- **FRAME_ERR_ARG** : 잘못된 인자  
- **FRAME_ERR_OOB** : 허용 범위를 초과  
- **FRAME_ERR_MAGIC** : MAGIC 불일치  
- **FRAME_ERR_CRC** : CRC 불일치  
- **FRAME_ERR_TRUNC** : 데이터 부족

---

### 1.2 `frame_t` — 단일 프레임 구조체

> 송수신 데이터의 프레임 단위 표현체.

```c
typedef struct {
    uint8_t     type;
    uint16_t    len;
    uint8_t     payload[FRAME_MAX_PAYLOAD];
} frame_t;
```

- **type** : 프레임 타입  
- **len** : Payload 길이  
- **payload** : 실제 데이터

---

### 1.3 `frame_parser_t` — 파서 상태 구조체

> 스트리밍 데이터 파싱을 위한 내부 버퍼 및 상태 관리용 구조체.

```c
typedef struct {
    uint8_t     buf[FRAME_MAX_WIRE];
    size_t      fill;
    size_t      scan;
} frame_parser_t;
```

- **buf** : 누적 데이터 버퍼  
- **fill** : 현재 버퍼에 채워진 바이트 수  
- **scan** : 스캔 인덱스 (현재 사용하지 않음)

---

### 1.4 `frame_parse_status_t` — 파서 상태 값

> 파싱 과정 중 상태 또는 재동기화 결과를 표현.

```c
typedef enum {
    FP_EMIT = 0,            // out 프레임 1개 완성됨 (정상)
    FP_MORE,                // 더 많은 입력 필요 (정상 대기)
    FP_RESYNC_MAGIC,        // MAGIC 동기화 중(노이즈 드롭 후 재시도)
    FP_RESYNC_VERSION,      // 버전 불일치 → 1바이트 드롭 후 재동기화
    FP_RESYNC_LEN_OOB,      // LEN 상한 초과 → 1바이트 드롭 후 재동기화
    FP_RESYNC_CRC_FAIL,     // CRC 불일치 → 1바이트 드롭 후 재동기화
    FP_OVERFLOW,            // 내부 버퍼 가득 참 (입력 일부/전부 미수용)
    FP_ARG_ERROR            // 잘못된 인자
} frame_parse_status_t;
```

- **FP_EMIT** : 정상적으로 프레임 1개 완성됨  
- **FP_MORE** : 데이터가 부족하여 추가 입력 필요  
- **FP_RESYNC_MAGIC** : MAGIC 재동기화 중  
- **FP_RESYNC_VERSION** : 버전 불일치로 1바이트 드롭  
- **FP_RESYNC_LEN_OOB** : 길이 오류로 1바이트 드롭  
- **FP_RESYNC_CRC_FAIL** : CRC 실패로 1바이트 드롭  
- **FP_OVERFLOW** : 버퍼 용량 초과  
- **FP_ARG_ERROR** : 잘못된 인자

---

## 2. Source File

### 2.1 상수 정의

```c
#define FRAME_MAGIC_MSB     0xA5u
#define FRAME_MAGIC_LSB     0x5Au
#define FRAME_VER           0x01u
#define FRAME_HDR_SIZE      6u
#define FRAME_TLR_SIZE      2u
#define FRAME_MAX_PAYLOAD   1024u
#define FRAME_MAX_WIRE      (FRAME_HDR_SIZE + FRAME_MAX_PAYLOAD + FRAME_TLR_SIZE)
```

- **MAGIC** : 프레임 시작 시그니처 (0xA5 0x5A)  
- **VER** : 프로토콜 버전  
- **HDR_SIZE / TLR_SIZE** : 헤더(6B) / 트레일러(2B) 길이  
- **MAX_PAYLOAD** : 최대 1024바이트  
- **MAX_WIRE** : 프레임 전체 최대 크기

---

### 2.2 CRC 계산 함수

```c
uint16_t frame_crc16_ccitt(const uint8_t* data, size_t len){
    uint16_t crc = 0xFFFFu;
    for(size_t i=0;i<len;i++){
        crc ^= (uint16_t)data[i] << 8;
        for(int b=0;b<8;b++){
            if(crc & 0x8000) crc = (uint16_t)((crc << 1) ^ 0x1021);
            else             crc = (uint16_t)(crc << 1);
        }
    }
    return crc;
}
```

- **역할** : `VER | TYPE | LEN | PAYLOAD` 영역에 대해 CRC16-CCITT(FALSE) 계산  
- **다항식** : 0x1021  
- **초기값** : 0xFFFF  
- **XOR Out** : 0x0000  
- **Refin/Refout** : False  

---

### 2.3 프레임 빌드

```c
frame_err_t frame_build(uint8_t type,
                        const uint8_t* payload, uint16_t len,
                        uint8_t* out_buf, size_t out_cap, size_t* out_len);
```

- **역할** : payload 데이터를 포함한 완전한 프레임 생성  
- **동작 순서**
  1. 인자 검증 (`payload`, `out_buf`, `len`, `out_cap`)  
  2. Header 작성 (MAGIC, VER, TYPE, LEN)  
  3. Payload 복사  
  4. CRC 계산 및 TLR 기록  
  5. 총 길이 반환 (`out_len`)  

- **오류 코드**
  - `FRAME_ERR_ARG` : 잘못된 인자  
  - `FRAME_ERR_OOB` : 버퍼 용량 부족  

---

### 2.4 프레임 길이 확인

```c
frame_err_t frame_peek_len(const uint8_t* in_buf, size_t in_len, uint16_t* out_len);
```

- **역할** : 수신된 데이터에서 Payload 길이만 미리 확인  
- **검증 절차**
  1. MAGIC / VER 확인  
  2. LEN 값이 `FRAME_MAX_PAYLOAD` 이하인지 확인  
- **반환**
  - `FRAME_OK` : 정상  
  - `FRAME_ERR_MAGIC` : MAGIC 불일치  
  - `FRAME_ERR_OOB` : 길이 초과  

---

### 2.5 프레임 파싱

```c
frame_err_t frame_parse(const uint8_t* in_buf, size_t in_len, frame_t* out);
```

- **역할** : 수신된 완전한 프레임 버퍼를 `frame_t` 구조체로 해석  
- **검증 과정**
  - MAGIC / VER / LEN / CRC16 검증  
  - Payload 복사  
- **반환**
  - `FRAME_OK` : 정상  
  - `FRAME_ERR_CRC` : CRC 불일치  
  - `FRAME_ERR_TRUNC` : 데이터 부족  

---

### 2.6 스트리밍 파서 내부 처리

> 지속적으로 입력되는 바이트 스트림(UART, TCP 등)에서 MAGIC 기준으로 프레임 동기화 수행.

- **seek_magic()** : MAGIC(A5 5A) 탐색 및 버퍼 정렬  
- **check_version_or_resync()** : 버전 불일치 시 1바이트 드롭  
- **read_len_or_resync()** : 길이 초과 시 재동기화  
- **check_crc_or_resync()** : CRC 실패 시 재동기화  
- **emit_frame_and_consume()** : 프레임 추출 후 버퍼에서 제거  

---

### 2.7 스트리밍 파서 피드 함수

```c
frame_parse_status_t frame_parser_feed(frame_parser_t* p,
                                       const uint8_t* data, size_t n,
                                       frame_t* out, size_t* consumed);
```

- **역할** : 외부 스트림 데이터를 내부 버퍼에 누적하고 프레임 단위로 해석  
- **처리 흐름**
  1. 입력 데이터 버퍼에 추가 (`append_to_buf`)  
  2. MAGIC 동기화 (`seek_magic`)  
  3. Header 확보 여부 확인  
  4. 버전 / 길이 / CRC 검증  
  5. 완전한 프레임이면 `out`으로 배출  

- **반환 상태**
  - `FP_EMIT` : 프레임 완성  
  - `FP_MORE` : 추가 데이터 필요  
  - `FP_RESYNC_*` : 재동기화 중  
  - `FP_OVERFLOW` : 버퍼 초과  
  - `FP_ARG_ERROR` : 잘못된 인자

---

### 2.8 디버그 로그

- **ESP_LOGE(TAG, ...)** : 인자 오류, CRC 실패 등  
- **ESP_LOGW(TAG, ...)** : MAGIC 불일치, 재시도 경고  
- **ESP_LOGD(TAG, ...)** : 내부 상태 추적  

`TAG`는 `"frame"` 으로 고정되어 있으며,  
`menuconfig → Component config → Log output → Default log level` 에서 `Debug` 이상 설정 시 세부 로그 출력 가능.

---

## 3. 사용 팁 & 권장 사항

- **MAGIC 정렬 유지** : 수신 스트림에 노이즈가 섞일 수 있으므로 항상 재동기화 고려  
- **콜백 내부 최소화** : Blocking I/O 금지, 빠른 반환 권장  
- **CRC 검증 필수** : 데이터 무결성 보장을 위해 항상 CRC 확인 수행  
- **프레임 크기 검증** : `FRAME_MAX_PAYLOAD` 이하로 제한  

---

## 4. 예시 사용법

```c
frame_parser_t parser;
frame_t frame;

frame_parser_init(&parser);

while (uart_available()) {
    uint8_t buf[64];
    size_t  n = uart_read(buf, sizeof(buf));

    frame_parse_status_t st = frame_parser_feed(&parser, buf, n, &frame, NULL);

    if (st == FP_EMIT) {
        ESP_LOGI("main", "Frame type=0x%02X len=%u", frame.type, frame.len);
        process_frame(&frame);
    }
}
```

---

## 5. 확장 및 개선 방안

- **에러 카운터 추가** : CRC 실패, MAGIC 불일치 등 누적 통계  
- **멀티 타입 처리** : 타입별 핸들러 테이블 등록  
- **프로토콜 버전 호환성 확보** : `FRAME_VER` 필드 기반 멀티버전 대응  
- **보안 강화** : Payload 암호화 / 압축 기능 추가  
