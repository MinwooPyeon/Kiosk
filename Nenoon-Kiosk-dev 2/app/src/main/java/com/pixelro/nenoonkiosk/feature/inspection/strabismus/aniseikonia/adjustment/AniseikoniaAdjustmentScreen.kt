package com.pixelro.nenoonkiosk.feature.inspection.strabismus.aniseikonia.adjustment

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.HowToButton
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.strabismus.aniseikonia.howtodialog.AniseikoniaHowToDialog
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

/**
 * 부등상시 검사 조정 화면
 *
 * @param onNextClicked 다음 버튼 클릭 콜백 (answer, scaleDifference)
 * @param onBackClicked 뒤로가기 버튼 클릭 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AniseikoniaAdjustmentScreen(
    onNextClicked: (Int, Float) -> Unit,
    onBackClicked: () -> Unit,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(StringProvider.getString(R.string.tts_fudo_adjustment), TextToSpeech.QUEUE_FLUSH)
    }

    // 텍스트 리소스
    val screenTitle = StringProvider.getStringComposable(R.string.fudo_question_title)
    val bottomText = StringProvider.getStringComposable(R.string.fudo_adjustment_bottom_text)
    val nextButtonText = StringProvider.getStringComposable(R.string.common_next)
    val mainText = StringProvider.getStringComposable(R.string.fudo_adjustment_main_text)
    val leftSmallerButtonText = StringProvider.getStringComposable(R.string.fudo_adjustment_button_left_smaller)
    val rightSmallerButtonText = StringProvider.getStringComposable(R.string.fudo_adjustment_button_right_smaller)

    var rightMoonScale by remember { mutableStateOf(1f) }
    var scaleDifference by remember { mutableStateOf(0.0f) }
    var showHowToDialog by remember { mutableStateOf(false) }

    if (showHowToDialog) {
        AniseikoniaHowToDialog(onDismissRequest = { showHowToDialog = false })
    }

    Scaffold(
        containerColor = Color.Black,
        topBar = {
            NenoonTopBar(
                title = screenTitle,
                showBackButton = true,
                onBackClicked = onBackClicked,
                actions = {
                    HowToButton(onClick = { showHowToDialog = true })
                },
                containerColor = Color.Black,
                contentColor = Color.White
            )
        },
        bottomBar = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                val locale = LocalConfiguration.current.locale
                val fontSize = if (locale.language == "ko") 48.sp else 44.sp
                Text(
                    text = bottomText,
                    color = Color.White,
                    fontSize = fontSize,
                    textAlign = TextAlign.Center,
                )
                Spacer(modifier = Modifier.height(16.dp))
                PrimaryButton(
                    onClick = { onNextClicked(1, scaleDifference) },
                    text = nextButtonText,
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Black),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AniseikoniaAdjustmentCanvas(rightMoonScale = rightMoonScale)
            Spacer(modifier = Modifier.height(16.dp))

            val locale = LocalConfiguration.current.locale
            val fontSize = if (locale.language == "ko") 48.sp else 44.sp
            Text(
                text = mainText,
                color = Color.White,
                fontSize = fontSize,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Spacer(modifier = Modifier.width(48.dp))
                Button(
                    onClick = {
                        rightMoonScale *= 0.995f
                        scaleDifference += 0.5f
                    },
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(96.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = neNoon_blue),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        leftSmallerButtonText,
                        color = Color.White,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.width(24.dp))
                Button(
                    onClick = {
                        rightMoonScale *= 1.005f
                        scaleDifference -= 0.5f
                    },
                    modifier =
                        Modifier
                            .weight(1f)
                            .height(96.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = neNoon_blue),
                    shape = RoundedCornerShape(12.dp),
                ) {
                    Text(
                        rightSmallerButtonText,
                        color = Color.White,
                        fontSize = 24.sp,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(modifier = Modifier.width(48.dp))
            }
        }
    }
}

//가로 모드 내용 잘림-> 수정 필요
@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun AniseikoniaAdjustmentScreenHorizontalPreview() {
    NenoonKioskTheme {
        AniseikoniaAdjustmentScreen(
            onNextClicked = { _, _ -> },
            onBackClicked = { }
        )
    }
}

@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun AniseikoniaAdjustmentScreenVerticalPreview() {
    NenoonKioskTheme {
        AniseikoniaAdjustmentScreen(
            onNextClicked = { _, _ -> },
            onBackClicked = { }
        )
    }
}
