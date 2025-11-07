/*
 * task_scan.c
 */
#include "task_scan.h"
#include "usb_advert.h"
#include "usb_host.h"
#include "usbh_core.h"
#include "usbh_msc.h"     // USBH_MSC_IsReady
#include "usart.h"

extern USBH_HandleTypeDef hUsbHostFS;

// USB MSC 장치 준비 여부 빠른 체크
static inline bool usb_ready(void){
    return (USBH_MSC_IsReady(&hUsbHostFS) == USBH_OK);
}

static void vTaskScan(void* arg){
    (void)arg;
    STLINK_UART_Println("[task scan] start");

    // 부팅 직후: 준비됐을 때만 1회 스캔
    if (usb_ready() && !USB_Advert_IsScanned()){
        (void)USB_Advert_Scan();
    }

    for(;;){
        if (!usb_ready()){
            // 장치 미연결/미준비 → 스캔 금지, 과도 로그 방지 위해 조용히 대기
            osDelay(500);
            continue;
        }

        if (!USB_Advert_IsScanned()){
            // 준비된 상태에서만 짧게 스캔 시도
            (void)USB_Advert_Scan();
        }

        // 부하/로그 완화
        osDelay(10000);
    }
}

void task_scan_start(uint32_t stack, osPriority_t prio){
    const osThreadAttr_t attr = {
        .name       = "media_scan",
        .stack_size = stack,
        .priority   = prio
    };
    osThreadNew(vTaskScan, NULL, &attr);
}
