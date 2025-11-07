/*
 * task_verify.c
 *
 *  Created on: Nov 7, 2025
 *      Author: SSAFY
 */


#include "task_verify.h"

#include "FreeRTOS.h"
#include "task.h"
#include <string.h>
#include <stdio.h>
#include <inttypes.h>
#include <stdbool.h>

#include "frame.h"               // frame_build(), FRAME_* 상수, FRAME_MAX_WIRE
#include "frame_dispatcher.h"    // proto_dispatch_handle()
#include "usart.h"               // STLINK_UART_Println()

#ifndef LOGT
#define LOGT(msg)        STLINK_UART_Println(msg)
#endif

static void vTaskVerifyNoUSB(void *arg);

/* 공통: req 프레임을 만들고 곧바로 디스패처에 먹여서 end-to-end 경로를 검증 */
static void build_and_dispatch(uint8_t req_type, const void* payload, uint16_t pl_len, const char* title)
{
    uint8_t  in_frame[FRAME_MAX_WIRE];
    size_t   in_len = 0;

    char line[96];
    snprintf(line, sizeof(line), "[TEST] %s (type=0x%02X, len=%u)", title, req_type, (unsigned)pl_len);
    LOGT(line);

    frame_err_t fe = frame_build(req_type, (const uint8_t*)payload, pl_len,
                                 in_frame, sizeof(in_frame), &in_len);
    if (fe != FRAME_OK) {
        snprintf(line, sizeof(line), " - frame_build FAILED: err=%d", (int)fe);
        LOGT(line);
        return;
    }

    // 실제 수신된 것처럼 디스패처로 투입 (디스패처 내부에서 응답 프레임을 다시 송신)
    proto_dispatch_handle(in_frame, in_len);
    LOGT(" - dispatched.\r\n");
}

static void vTaskVerifyNoUSB(void *arg)
{
    (void)arg;
    LOGT("==== Verify-No-USB Task: start ====");

    // 1) 매니저 로그인 (id:pw)
    const char *mgr_login = "admin:1234";
    build_and_dispatch(FRAME_LIC_MGR_LOGIN, mgr_login, (uint16_t)strlen(mgr_login), "LIC_MGR_LOGIN");
    vTaskDelay(pdMS_TO_TICKS(100));

    // 2) 라이선스 발급 (app:to)
    const char *issue = "nenoon-kiosk:user001";
    build_and_dispatch(FRAME_LIC_ISSUE, issue, (uint16_t)strlen(issue), "LIC_ISSUE");
    vTaskDelay(pdMS_TO_TICKS(100));

    // 3) 유효성 검사 (라이선스 문자열 – 존재하지 않아도 경로 동작만 확인)
    const char *lic = "LIC-TEST-KEY";
    build_and_dispatch(FRAME_LIC_VALIDATE, lic, (uint16_t)strlen(lic), "LIC_VALIDATE");
    vTaskDelay(pdMS_TO_TICKS(100));

    // 4) 챌린지 요청
    build_and_dispatch(FRAME_LIC_GET_CHALLENGE, lic, (uint16_t)strlen(lic), "LIC_GET_CHALLENGE");
    vTaskDelay(pdMS_TO_TICKS(100));

    // 5) JWT 요청
    build_and_dispatch(FRAME_LIC_GET_JWT, lic, (uint16_t)strlen(lic), "LIC_GET_JWT");
    vTaskDelay(pdMS_TO_TICKS(100));

    // 6) 라이선스 폐기
    build_and_dispatch(FRAME_LIC_REVOKE, lic, (uint16_t)strlen(lic), "LIC_REVOKE");
    vTaskDelay(pdMS_TO_TICKS(100));

    LOGT("==== Verify-No-USB Task: done ====");
    vTaskDelete(NULL); // 1회 검증용이면 삭제, 반복 검증 원하면 주석 처리 후 주기 루프로 변경
}

void task_verify_start(void)
{
    // 우선순위는 너무 높지 않게, 스택은 프레임 버퍼/로그 문자열 여유 있게
    xTaskCreate(vTaskVerifyNoUSB, "verify_no_usb", 1024, NULL, tskIDLE_PRIORITY + 2, NULL);
}
