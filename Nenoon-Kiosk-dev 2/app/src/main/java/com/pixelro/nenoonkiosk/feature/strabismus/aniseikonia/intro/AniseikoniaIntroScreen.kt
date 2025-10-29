package com.pixelro.nenoonkiosk.feature.strabismus.aniseikonia.intro

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.strabismus.aniseikonia.howtodialog.AniseikoniaHowToDialog
import com.pixelro.nenoonkiosk.feature.strabismus.common.component.InspectionIntroScreen
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

/**
 * 부등상시 검사 인트로 화면
 *
 * @param onStartClicked 검사 시작 버튼 클릭 콜백
 * @param onHowToClicked 가이드 보기 버튼 클릭 콜백
 * @param onBackClicked 뒤로가기 버튼 클릭 콜백
 */
@Composable
fun AniseikoniaIntroScreen(
    onStartClicked: () -> Unit,
    onHowToClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    // 리소스
    val title = StringProvider.getStringComposable(R.string.fudo_intro_title)
    val description = StringProvider.getStringComposable(R.string.fudo_intro_description)

    InspectionIntroScreen(
        title = title,
        description = description,
        onStartClicked = onStartClicked,
        onBackClicked = onBackClicked,
        howToDialog = { onDismiss -> AniseikoniaHowToDialog(onDismissRequest = onDismiss) }
    )
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun AniseikoniaIntroScreenHorizontalPreview() {
    NenoonKioskTheme {
        AniseikoniaIntroScreen({}, {}, {})
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun AniseikoniaIntroScreenVerticalPreview() {
    NenoonKioskTheme {
        AniseikoniaIntroScreen({}, {}, {})
    }
}
