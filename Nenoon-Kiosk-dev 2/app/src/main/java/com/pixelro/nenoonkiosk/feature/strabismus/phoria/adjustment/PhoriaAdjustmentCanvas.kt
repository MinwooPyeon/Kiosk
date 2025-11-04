package com.pixelro.nenoonkiosk.feature.strabismus.phoria.adjustment

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import kotlin.math.roundToInt

/**
 * 사위 검사 조정용 캔버스 컴포넌트
 *
 * 노란색 배경에 녹색 원과 주황색 십자가를 그리며, 십자가는 드래그 가능합니다.
 *
 * @param crosshairPosition 십자가 위치
 * @param circlePosition 원 위치
 * @param onCrosshairPositionChange 십자가 위치 변경 콜백
 * @param onCirclePositionChange 원 위치 변경 콜백
 */
@Composable
fun PhoriaAdjustmentCanvas(
    crosshairPosition: MutableState<Offset?>,
    circlePosition: MutableState<Offset?>,
) {
    var boxSize by remember { mutableStateOf<IntOffset?>(null) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .background(Color(0xFFEBF961))
            .onGloballyPositioned { coordinates ->
                if (boxSize == null) {
                    boxSize = IntOffset(coordinates.size.width, coordinates.size.height)
                    val center =
                        Offset(coordinates.size.width / 2f, coordinates.size.height / 2f)
                    crosshairPosition.value = center
                    circlePosition.value = center
                }
            },
        contentAlignment = Alignment.TopStart
    ) {
        if (boxSize != null && circlePosition != null && crosshairPosition != null) {
            val context = LocalContext.current
            val displayMetrics = context.resources.displayMetrics
            val ppi = displayMetrics.xdpi
            val mmToPx = ppi / 25.4f
            val circleDiameterPx = 44.3f * mmToPx
            val circleThicknessPx = 4 * mmToPx
            val crossLengthPx = 16 * mmToPx
            val crossThicknessPx = 4 * mmToPx

            val circleDiameterDp = (circleDiameterPx / LocalDensity.current.density).dp
            val circleDiameterPxValue =
                with(LocalDensity.current) { circleDiameterDp.toPx() }

            Canvas(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (circlePosition.value!!.x - circleDiameterPxValue / 2f).roundToInt(),
                            (circlePosition.value!!.y - circleDiameterPxValue / 2f).roundToInt()
                        )
                    }
                    .size(circleDiameterDp)
            ) {
                drawCircle(
                    color = Color(0xFF14Fa14),
                    radius = circleDiameterPx / 2,
                    style = Stroke(width = circleThicknessPx)
                )
            }
            Canvas(
                modifier = Modifier
                    .offset {
                        IntOffset(
                            (crosshairPosition.value!!.x - circleDiameterPxValue / 2f).roundToInt(),
                            (crosshairPosition.value!!.y - circleDiameterPxValue / 2f).roundToInt()
                        )
                    }
                    .size(circleDiameterDp)
                    .pointerInput(Unit) {
                        detectDragGestures { change, dragAmount ->
                            change.consume()
                            val newPosition = crosshairPosition.value!! + dragAmount
                            val constrainedX =
                                newPosition.x.coerceIn(0f, boxSize!!.x.toFloat())
                            val constrainedY =
                                newPosition.y.coerceIn(0f, boxSize!!.y.toFloat())
                            crosshairPosition.value = (Offset(constrainedX, constrainedY))
                        }
                    }
            ) {
                drawLine(
                    color = Color(0xFFEA5821),
                    start = Offset(center.x - crossLengthPx / 2, center.y),
                    end = Offset(center.x + crossLengthPx / 2, center.y),
                    strokeWidth = crossThicknessPx
                )
                drawLine(
                    color = Color(0xFFEA5821),
                    start = Offset(center.x, center.y - crossLengthPx / 2),
                    end = Offset(center.x, center.y + crossLengthPx / 2),
                    strokeWidth = crossThicknessPx
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PhoriaAdjustmentCanvasPreview() {
    var crosshairPosition by remember { mutableStateOf<Offset?>(null) }
    var circlePosition by remember { mutableStateOf<Offset?>(null) }

    NenoonKioskTheme {

    }
}