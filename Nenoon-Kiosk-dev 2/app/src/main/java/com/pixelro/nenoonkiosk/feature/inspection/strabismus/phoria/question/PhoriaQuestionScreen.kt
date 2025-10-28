package com.pixelro.nenoonkiosk.feature.inspection.strabismus.phoria.question

import android.speech.tts.TextToSpeech
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.tooling.preview.Preview
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.common.component.InspectionQuestionContent
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.phoria.howtodialog.PhoriaHowToDialog
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

/**
 * 사위 검사 질문 화면
 *
 * @param onNextClicked 다음 버튼 클릭 콜백 (선택된 옵션)
 * @param onBackClicked 뒤로가기 버튼 클릭 콜백
 * @param testType 검사 타입
 */
@Composable
fun PhoriaQuestionScreen(
    onNextClicked: (Int) -> Unit,
    onBackClicked: () -> Unit,
    testType: String,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_sawi_question), TextToSpeech.QUEUE_FLUSH)
    }

    // 텍스트 리소스
    val title = StringProvider.getStringComposable(R.string.sawi_question_title)
    val mainText = StringProvider.getStringComposable(R.string.sawi_question_main_text)
    val nextButtonText = StringProvider.getStringComposable(R.string.common_next)
    val options =
        listOf(
            StringProvider.getStringComposable(R.string.sawi_question_option1),
            StringProvider.getStringComposable(R.string.sawi_question_option2),
            StringProvider.getStringComposable(R.string.sawi_question_option3),
            StringProvider.getStringComposable(R.string.sawi_question_option4),
        )

    var showHowToDialog by remember { mutableStateOf(false) }

    if (showHowToDialog) {
        PhoriaHowToDialog(onDismissRequest = { showHowToDialog = false })
    }

    InspectionQuestionContent(
        title = title,
        mainText = mainText,
        options = options,
        centerContent = { PhoriaInspectionCanvas() },
        nextButtonText = nextButtonText,
        onNextClicked = onNextClicked,
        onBackClicked = onBackClicked,
        onShowHowToClicked = { showHowToDialog = true }
    )
}

//가로 모드 내용 잘림-> 수정 필요
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PhoriaQuestionScreenHorizontalPreview() {
    NenoonKioskTheme {
        PhoriaQuestionScreen({}, {}, "sawi")
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PhoriaQuestionScreenVerticalPreview() {
    NenoonKioskTheme {
        PhoriaQuestionScreen({}, {}, "sawi")
    }
}
