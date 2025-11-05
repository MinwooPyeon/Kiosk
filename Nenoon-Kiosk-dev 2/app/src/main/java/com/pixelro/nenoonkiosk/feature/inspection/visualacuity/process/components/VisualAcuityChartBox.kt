package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.feature.inspection.components.WarningOverlay
import com.pixelro.nenoonkiosk.ui.theme.White

/**
 * 시력 검사 차트 박스 컴포넌트
 *
 * 흰색 박스 안에 란돌트 C 이미지를 표시하며,
 * 얼굴 인식 상태에 따라 경고 메시지를 오버레이로 표시
 *
 * @param ansNum 정답 방향 (2~7)
 * @param sightLevel 시력 난이도 (1~10)
 * @param isFaceDetected 얼굴 인식 여부
 * @param isFacingForward 정면을 보고 있는지 여부
 * @param modifier Modifier
 */
@Composable
fun VisualAcuityChartBox(
    ansNum: Int,
    sightLevel: Int,
    isFaceDetected: Boolean,
    isFacingForward: Boolean,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .padding(top = 40.dp)
                .height(500.dp)
                .width(500.dp)
                .background(
                    color = White,
                    shape = RoundedCornerShape(8.dp),
                ),
        contentAlignment = Alignment.Center,
    ) {
        // 란돌트 C 이미지
        LandoltCImage(
            ansNum = ansNum,
            sightLevel = sightLevel,
        )

        // 안내 메시지 (얼굴 인식 실패 시)
        if (!isFaceDetected || !isFacingForward) {
            WarningOverlay(
                text1 = "정면을 ",
                text2 = "주시",
                text3 = "해주세요",
                modifier = Modifier.padding(top = 300.dp)
            )
        }
    }
}