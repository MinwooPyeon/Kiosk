package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 경고 애니메이션 기능이 있는 바텀 바 버튼 컴포넌트
 *
 * @param onClick 버튼 클릭 이벤트
 * @param text 버튼에 표시될 텍스트
 * @param modifier Modifier
 * @param enabled 버튼 활성화 여부 (true일 때만 클릭 가능)
 * @param showWarningWhenDisabled 비활성화 상태에서 클릭 시 경고 애니메이션 표시 여부
 * @param onWarningShow 경고 애니메이션 표시 콜백 (true: 표시, false: 숨김)
 */
@Composable
fun BottomWarningButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showWarningWhenDisabled: Boolean = false,
    onWarningShow: ((Boolean) -> Unit)? = null,
) {
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter,
    ) {
        PrimaryButton(
            onClick = {
                if (enabled) {
                    onClick()
                } else if (showWarningWhenDisabled && onWarningShow != null) {
                    // 경고 애니메이션: 3번 깜빡임 + 2초 표시
                    coroutineScope.launch {
                        for (i in 1..3) {
                            onWarningShow(true)
                            delay(400)
                            onWarningShow(false)
                            delay(400)
                        }
                        onWarningShow(true)
                        delay(2000)
                        onWarningShow(false)
                    }
                }
            },
            text = text,
            enabled = true, // PrimaryButton은 항상 활성화, enabled는 내부 로직에서 처리
            modifier =
                Modifier
                    .padding(
                        start = 40.dp,
                        end = 40.dp,
                        bottom = 40.dp,
                    )
                    .fillMaxWidth()
                    .height(80.dp),
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun BottomWarningButtonPreview() {
    NenoonKioskTheme {
        BottomWarningButton(
            onClick = {},
            text = "다음",
            enabled = true,
        )
    }
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun BottomWarningButtonDisabledPreview() {
    NenoonKioskTheme {
        BottomWarningButton(
            onClick = {},
            text = "다음",
            enabled = false,
            showWarningWhenDisabled = true,
            onWarningShow = {},
        )
    }
}