package com.pixelro.nenoonkiosk.feature.inspection.strabismus.aniseikonia.adjustment

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

/**
 * 부등상시 검사 조정용 캔버스 컴포넌트
 *
 * 노란색 배경에 왼쪽 달과 오른쪽 달 이미지를 표시하며, 오른쪽 달은 크기 조절 가능
 *
 * @param rightMoonScale 오른쪽 달 크기 비율
 * @param modifier Modifier
 */
@Composable
fun AniseikoniaAdjustmentCanvas(
    rightMoonScale: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier =
            modifier
                .fillMaxWidth()
                .height(500.dp)
                .background(Color(0xFFEBF961)),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_fudo_moon_left),
            contentDescription = "Left Moon",
            modifier = Modifier.size(200.dp, 500.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Image(
            painter = painterResource(id = R.drawable.ic_fudo_moon_right),
            contentDescription = "Right Moon",
            modifier =
                Modifier
                    .size(200.dp, 500.dp)
                    .graphicsLayer(
                        scaleX = rightMoonScale,
                        scaleY = rightMoonScale,
                        transformOrigin = TransformOrigin(0f, 0.5f),
                    ),
        )
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun AniseikoniaAdjustmentCanvasVerticalPreview() {
    NenoonKioskTheme {
        AniseikoniaAdjustmentCanvas(rightMoonScale = 1f)
    }
}