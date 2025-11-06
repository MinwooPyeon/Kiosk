package com.pixelro.nenoonkiosk.feature.strabismus.common.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.SecondaryButton
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

/**
 * 화면 하단 메인+보조 두 가지 버튼 영역
 *
 * @param primaryButtonText 메인 버튼 텍스트
 * @param onPrimaryButtonClick 메인 버튼 클릭 콜백
 * @param secondaryButtonText 보조 버튼 텍스트
 * @param onSecondaryButtonClick 보조 버튼 클릭 콜백
 */
@Composable
fun DualButtonBottomBar(
    primaryButtonText: String,
    onPrimaryButtonClick: () -> Unit,
    secondaryButtonText: String,
    onSecondaryButtonClick: () -> Unit
) {
    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(16.dp),
    ) {
        PrimaryButton(
            onClick = onPrimaryButtonClick,
            text = primaryButtonText,
        )
        Spacer(modifier = Modifier.height(16.dp))
        SecondaryButton(
            onClick = onSecondaryButtonClick,
            text = secondaryButtonText,
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun ResultBottomBarHorizontalPreview() {
    NenoonKioskTheme {
        DualButtonBottomBar(
            primaryButtonText = "결과 출력",
            onPrimaryButtonClick = {},
            secondaryButtonText = "메인으로 돌아가기",
            onSecondaryButtonClick = {}
        )
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun ResultBottomBarVerticalPreview() {
    NenoonKioskTheme {
        DualButtonBottomBar(
            primaryButtonText = "결과 프린트하기",
            onPrimaryButtonClick = {},
            secondaryButtonText = "메인 메뉴로 돌아가기",
            onSecondaryButtonClick = {}
        )
    }
}