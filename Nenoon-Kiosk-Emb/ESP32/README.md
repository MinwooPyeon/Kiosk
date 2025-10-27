# ESP32 (네트워크·인증·링크)

## proto (frame.c/.h, types.h)
전송 프레이밍/CRC와 공통 타입을 먼저 고정해야 이후 모든 통신이 맞물려. 완료 기준: FRAME{MAGIC,TYPE,LEN,CRC} 단방향 빌드·파싱 유닛테스트 통과.

## wifi_mgr
지금 만든 상태 머신 버전 그대로: 연결·재시도·이벤트 분배 안정화. 완료 기준: SSID/PW 저장 후 AP 전환/전원 복귀에서 자동 회복 로그 확인.

## cert_store
루트 CA·핀닝 키 주입 래퍼. HTTPS 쓰는 모든 상위 계층의 공통 기반. 완료 기준: esp_http_client_config_t에 증명서 붙여 TLS 핸드셰이크 성공.

## ratelimit
서버 호출 빈도 제어(토큰 버킷). 인증/하트비트 과호출 방지. 완료 기준: 초당/분당 한도를 넘어가면 false 반환 및 카운터 회복 확인.

## auth_adapter
서버 연동(Challenge→Confirm→JWT 저장/갱신). wifi_mgr+cert_store+ratelimit 의존. 완료 기준: NVS에 JWT 저장, 만료 후 재발급 성공.

## uart_link
STM32와 프레임 기반 UART 송수신. 완료 기준: 루프백/상대 STM32와 HELLO/META/DATA/ACK 왕복 확인.

## metrics
http_ok/err, bytes_tx/rx, last_err 등 지표 수집. 완료 기준: 주기 스냅샷 구조체로 pull 가능, 주요 경로에서 카운트 누적.

## http_srv
로컬 상태 조회, 수동 트리거(디버그용). 완료 기준: /health, /reprovision 간단 엔드포인트 동작.

## ota_update
manifest 조회→서명 검증→esp_https_ota→부트 플래그. 

완료 기준: 테스트 피드로 안전 업데이트/롤백 플로우 검증.

## 구현 순서
구현 흐름 요약: proto → wifi → cert → ratelimit → auth → uart → metrics → (http) → ota

이유: 하위 통신 규약과 네트워크 보안 토대를 먼저 굳혀야 상위 인증/전송/OTA가 출렁이지 않아.