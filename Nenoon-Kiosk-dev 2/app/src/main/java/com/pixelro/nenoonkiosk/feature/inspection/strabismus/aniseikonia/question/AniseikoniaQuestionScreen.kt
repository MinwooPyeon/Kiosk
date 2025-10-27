package com.pixelro.nenoonkiosk.feature.inspection.strabismus.aniseikonia.question

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.aniseikonia.howtodialog.AniseikoniaHowToDialog
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.common.component.InspectionQuestionScreen
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

/**
 * 부등상시 검사 질문 화면
 *
 * @param onNextClicked 다음 버튼 클릭 콜백 (선택된 옵션)
 * @param onBackClicked 뒤로가기 버튼 클릭 콜백
 * @param onShowHowToClicked 도움말 버튼 클릭 콜백
 */
@Composable
fun AniseikoniaQuestionScreen(
    onNextClicked: (Int) -> Unit,
    onBackClicked: () -> Unit,
    onShowHowToClicked: () -> Unit,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_fudo_question), TextToSpeech.QUEUE_FLUSH)
    }

    // 텍스트 리소스
    val title = StringProvider.getStringComposable(R.string.fudo_question_title)
    val mainText = StringProvider.getStringComposable(R.string.sawi_question_main_text)
    val nextButtonText = StringProvider.getStringComposable(R.string.common_next)
    val options =
        listOf(
            StringProvider.getStringComposable(R.string.fudo_question_option1),
            StringProvider.getStringComposable(R.string.fudo_question_option2),
            StringProvider.getStringComposable(R.string.fudo_question_option3),
        )

    // 이미지 리소스
    val testImage = painterResource(id = R.drawable.ic_fudo_test)

    var showHowToDialog by remember { mutableStateOf(false) }

    if (showHowToDialog) {
        AniseikoniaHowToDialog(onDismissRequest = { showHowToDialog = false })
    }

    InspectionQuestionScreen(
        title = title,
        mainText = mainText,
        options = options,
        centerContent = {
            Image(
                painter = testImage,
                contentDescription = "Fudo Test Image",
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(500.dp)
                        .background(Color(0xFFEBF961)),
            )
        },
        nextButtonText = nextButtonText,
        onNextClicked = onNextClicked,
        onBackClicked = onBackClicked,
        onShowHowToClicked = { showHowToDialog = true }
    )
}

//가로 모드 내용 잘림-> 수정 필요
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun AniseikoniaQuestionScreenHorizontalPreview() {
    NenoonKioskTheme {
        AniseikoniaQuestionScreen({}, {}, {})
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun AniseikoniaQuestionScreenVerticalPreview() {
    NenoonKioskTheme {
        AniseikoniaQuestionScreen({}, {}, {})
    }
}
