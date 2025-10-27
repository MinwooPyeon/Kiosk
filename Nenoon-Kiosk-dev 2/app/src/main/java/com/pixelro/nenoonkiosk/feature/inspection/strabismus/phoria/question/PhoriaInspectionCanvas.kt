package com.pixelro.nenoonkiosk.feature.inspection.strabismus.phoria.question

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

/**
 * 사위 검사용 원과 십자가 캔버스 컴포넌트
 *
 * 노란색 배경에 녹색 원과 주황색 십자가를 그립니다.
 * 검사를 위해 실제 물리적 크기를 계산하여 정확한 크기로 렌더링 필요.
 *
 * @param modifier Modifier
 */
@Composable
fun PhoriaInspectionCanvas(modifier: Modifier = Modifier) {
    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .height(500.dp)
                .background(Color(0xFFEBF961)),
        contentAlignment = Alignment.Center,
    ) {
        val context = LocalContext.current
        val displayMetrics = context.resources.displayMetrics
        val ppi = displayMetrics.xdpi // 화면의 정확한 물리적 인치당 픽셀 수

        val mmToPx = ppi / 25.4f

        // TODO: 기기 변경 시 재측정 필요 - 48mm가 맞는지 확인하고 보정값 조정
        // 관찰된 렌더링 부정확성 보정 (목표 기기에서 48mm가 아닌 52mm로 렌더링됨)
        // 48mm * (48 / 52) = 44.3mm
        val circleDiameterPx = 44.3f * mmToPx
        val circleThicknessPx = 4 * mmToPx
        val crossLengthPx = 16 * mmToPx
        val crossThicknessPx = 4 * mmToPx

        Canvas(modifier = Modifier.size((circleDiameterPx / LocalDensity.current.density).dp)) {
            // 원 그리기
            drawCircle(
                color = Color(0xFF14Fa14),
                radius = circleDiameterPx / 2,
                style = Stroke(width = circleThicknessPx),
            )

            // 십자가 그리기
            // 수평선
            drawLine(
                color = Color(0xFFEA5821),
                start = Offset(center.x - crossLengthPx / 2, center.y),
                end = Offset(center.x + crossLengthPx / 2, center.y),
                strokeWidth = crossThicknessPx,
            )
            // 수직선
            drawLine(
                color = Color(0xFFEA5821),
                start = Offset(center.x, center.y - crossLengthPx / 2),
                end = Offset(center.x, center.y + crossLengthPx / 2),
                strokeWidth = crossThicknessPx,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhoriaInspectionCanvasPreview() {
    NenoonKioskTheme {
        PhoriaInspectionCanvas()
    }
}