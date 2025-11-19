package com.pixelro.nenoonkiosk.feature.strabismus.phoria.howtodialog

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.strabismus.common.component.InspectionHowToDialog
import com.pixelro.nenoonkiosk.feature.strabismus.model.HowToStepData
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

/**
 * 사위 검사 방법 안내 다이얼로그
 *
 * @param onDismissRequest 다이얼로그 닫기 콜백
 */
@Composable
fun PhoriaHowToDialog(onDismissRequest: () -> Unit) {
    // 리소스
    val title = StringProvider.getStringComposable(R.string.sawi_howto_title)
    val step1 = StringProvider.getStringComposable(R.string.common_step1)
    val step1Desc = StringProvider.getStringComposable(R.string.sawi_howto_step1_desc)
    val step2 = StringProvider.getStringComposable(R.string.common_step2)
    val step2Desc = StringProvider.getStringComposable(R.string.sawi_howto_step2_desc)
    val step3 = StringProvider.getStringComposable(R.string.common_step3)
    val step3Desc = StringProvider.getStringComposable(R.string.sawi_howto_step3_desc)
    val confirmText = StringProvider.getStringComposable(R.string.common_confirm)

    InspectionHowToDialog(
        title = title,
        steps =
            listOf(
                HowToStepData(step = step1, instruction = step1Desc),
                HowToStepData(step = step2, instruction = step2Desc),
                HowToStepData(step = step3, instruction = step3Desc),
            ),
        confirmText = confirmText,
        onDismissRequest = onDismissRequest,
    )
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PhoriaHowToDialogHorizontalPreview() {
    NenoonKioskTheme {
        PhoriaHowToDialog { }
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PhoriaHowToDialogVerticalPreview() {
    NenoonKioskTheme {
        PhoriaHowToDialog { }
    }
}