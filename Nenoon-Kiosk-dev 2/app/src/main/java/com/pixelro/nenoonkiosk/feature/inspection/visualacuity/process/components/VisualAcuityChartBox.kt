package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
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
 * @param sizeMode 시표 크기 모드 (근거리/원거리)
 */
enum class VisualAcuityChartSizeMode {
    ShortDistance,
    LongDistance,
}

private val LONG_DISTANCE_LANDOLT_SIZES: Map<Int, Dp> =
    mapOf(
        1 to 49.85.dp,
        2 to 25.49.dp,
        3 to 16.42.dp,
        4 to 12.19.dp,
        5 to 9.96.dp,
        6 to 7.75.dp,
        7 to 6.49.dp,
        8 to 5.89.dp,
        9 to 5.34.dp,
        10 to 4.28.dp,
        11 to 3.57.dp,
        12 to 2.96.dp,
    )
private val DEFAULT_LONG_DISTANCE_LANDOLT_SIZE: Dp = 6.51.dp

@Composable
fun VisualAcuityChartBox(
    ansNum: Int,
    sightLevel: Int,
    isFaceDetected: Boolean,
    isFacingForward: Boolean,
    modifier: Modifier = Modifier,
    sizeMode: VisualAcuityChartSizeMode = VisualAcuityChartSizeMode.ShortDistance,
) {
    val chartSize =
        when (sizeMode) {
            VisualAcuityChartSizeMode.ShortDistance -> 360.dp
                VisualAcuityChartSizeMode.LongDistance -> 360.dp
        }
    Box(
        modifier =
            modifier
                .requiredSize(chartSize)
                .background(
                    color = White,
                    shape = RoundedCornerShape(8.dp),
                ),
        contentAlignment = Alignment.Center,
    ) {
        // 란돌트 C 이미지
        val landoltModifier =
            when (sizeMode) {
                VisualAcuityChartSizeMode.ShortDistance -> Modifier
                VisualAcuityChartSizeMode.LongDistance ->
                    Modifier.size(
                        LONG_DISTANCE_LANDOLT_SIZES[sightLevel] ?: DEFAULT_LONG_DISTANCE_LANDOLT_SIZE,
                    )
            }
        LandoltCImage(
            ansNum = ansNum,
            sightLevel = sightLevel,
            sizeMode = sizeMode,
            modifier = landoltModifier,
        )

        // 안내 메시지 (얼굴 인식 실패 시)
        if (!isFaceDetected || !isFacingForward) {
            val warningTopPadding =
                when (sizeMode) {
                    VisualAcuityChartSizeMode.ShortDistance -> 300.dp
                    VisualAcuityChartSizeMode.LongDistance -> chartSize / 2
                }
            WarningOverlay(
                text1 = "정면을 ",
                text2 = "주시",
                text3 = "해주세요",
                modifier = Modifier.padding(top = warningTopPadding)
            )
        }
    }
}
