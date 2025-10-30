package com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R

/**
 * 노안 검사에서 tryCount에 따라 다른 이미지를 표시하는 컴포넌트
 *
 * @param tryCount 현재 시도 횟수 (0, 1, 2+)
 * @param modifier Modifier
 */
@Composable
fun PresbyopiaTestImage(
    tryCount: Int,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Image(
            modifier = Modifier
                .padding(40.dp)
                .height(getImageSize(tryCount))
                .width(getImageSize(tryCount)),
            painter = painterResource(id = getImageResource(tryCount)),
            contentDescription = "",
        )
    }
}

/**
 * tryCount에 따른 이미지 리소스 반환
 */
private fun getImageResource(tryCount: Int): Int = when (tryCount) {
    0 -> R.drawable.img_test_presbyopia_1
    1 -> R.drawable.img_test_presbyopia_2
    else -> R.drawable.img_test_presbyopia_3
}

/**
 * tryCount에 따른 이미지 크기 반환
 */
private fun getImageSize(tryCount: Int): Dp = when (tryCount) {
    0 -> 50.dp
    else -> 70.dp
}