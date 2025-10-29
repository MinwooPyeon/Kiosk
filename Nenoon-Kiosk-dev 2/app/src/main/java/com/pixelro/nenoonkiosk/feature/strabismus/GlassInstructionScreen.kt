package com.pixelro.nenoonkiosk.feature.inspection.strabismus

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.SecondaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.strabismus.common.component.InspectionInstructionContent
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

/**
 * 안경 착용 확인 화면
 *
 * @param testType 검사 타입 (phoria/aniseikonia)
 * @param onYesClicked 예 버튼 클릭 콜백
 * @param onNoClicked 아니오 버튼 클릭 콜백
 * @param onBackClicked 뒤로가기 버튼 클릭 콜백
 */
@Composable
fun GlassInstructionScreen(
    testType: String,
    onYesClicked: () -> Unit,
    onNoClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_glass_instruction), TextToSpeech.QUEUE_FLUSH)
    }

    // 텍스트 리소스
    val mainText = StringProvider.getStringComposable(R.string.glass_instruction_main_text)
    val yesButtonText = StringProvider.getStringComposable(R.string.common_yes)
    val noButtonText = StringProvider.getStringComposable(R.string.common_no)

    val imageRes = R.drawable.glassimage

    InspectionInstructionContent(
        testType = testType,
        texts = listOf(mainText),
        imageRes = imageRes,
        onBackClicked = onBackClicked,
        bottomBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
            ) {
                PrimaryButton(
                    onClick = onYesClicked,
                    text = yesButtonText,
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(16.dp))
                SecondaryButton(
                    onClick = onNoClicked,
                    text = noButtonText,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    )
}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun GlassInstructionScreenHorizontalPreview() {
    NenoonKioskTheme {
        GlassInstructionScreen("phoria", {}, {}, {})
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun GlassInstructionScreenVerticalPreview() {
    NenoonKioskTheme {
        GlassInstructionScreen("phoria", {}, {}, {})
    }
}
