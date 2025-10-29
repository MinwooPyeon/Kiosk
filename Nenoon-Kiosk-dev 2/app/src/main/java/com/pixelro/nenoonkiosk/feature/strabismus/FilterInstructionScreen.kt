package com.pixelro.nenoonkiosk.feature.strabismus

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.strabismus.common.component.InspectionInstructionScreen
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

object FilterInstructionFragment {
    const val TEST_TYPE_PHORIA = "phoria"
    const val TEST_TYPE_ANISEIKONIA = "aniseikonia"
}

/**
 * 필터 착용 안내 화면
 *
 * @param testType 검사 타입 (phoria/aniseikonia)
 * @param isWearingGlasses 안경 착용 여부
 * @param onNextClicked 다음 버튼 클릭 콜백
 * @param onBackClicked 뒤로가기 버튼 클릭 콜백
 */
@Composable
fun FilterInstructionScreen(
    testType: String,
    isWearingGlasses: Boolean,
    onNextClicked: () -> Unit,
    onBackClicked: () -> Unit,
) {
    var isButtonEnabled by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_filter_instruction), TextToSpeech.QUEUE_FLUSH)
        kotlinx.coroutines.delay(3000)
        isButtonEnabled = true
    }

    // 텍스트 리소스
    val mainText1 = StringProvider.getStringComposable(R.string.filter_instruction_main_text_1)
    val mainText2 = StringProvider.getStringComposable(R.string.filter_instruction_main_text_2)
    val nextButtonText = StringProvider.getStringComposable(R.string.common_next)

    val imageRes =
        if (isWearingGlasses) {
            R.drawable.eyefilterimageglasses
        } else {
            R.drawable.eyefilterimage
        }

    InspectionInstructionScreen(
        testType = testType,
        texts = listOf(mainText1, mainText2),
        imageRes = imageRes,
        onBackClicked = onBackClicked,
        bottomBar = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
            ) {
                PrimaryButton(
                    onClick = onNextClicked,
                    text = nextButtonText,
                    enabled = isButtonEnabled
                )
            }
        }
    )
}


@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
fun FilterInstructionScreenHorizontalPreview() {
    NenoonKioskTheme {
        FilterInstructionScreen(
            testType = "phoria",
            isWearingGlasses = false,
            onNextClicked = {},
            onBackClicked = {},
        )
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
fun FilterInstructionScreenVerticalPreview() {
    NenoonKioskTheme {
        FilterInstructionScreen(
            testType = "phoria",
            isWearingGlasses = false,
            onNextClicked = {},
            onBackClicked = {},
        )
    }
}
