package com.pixelro.nenoonkiosk.feature.strabismus.phoria.adjustment

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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.HowToButton
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.TopBarVertical
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.strabismus.phoria.howtodialog.PhoriaHowToDialog
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme

/**
 * 사위 검사 조정 화면
 *
 * @param onConfirmClicked 확인 버튼 클릭 콜백 (십자가 위치, 원 위치)
 * @param onBackClicked 뒤로가기 버튼 클릭 콜백
 * @param onShowHowToClicked 도움말 버튼 클릭 콜백
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PhoriaAdjustmentScreen(
    onConfirmClicked: (Offset, Offset) -> Unit,
    onBackClicked: () -> Unit,
    onShowHowToClicked: () -> Unit,
) {
    LaunchedEffect(Unit) {
        TTS.speechTTS(
            StringProvider.getString(R.string.tts_sawi_adjustment),
            TextToSpeech.QUEUE_FLUSH
        )
    }

    // 텍스트 리소스
    val screenTitle = StringProvider.getStringComposable(R.string.sawi_question_title)
    val nextButtonText = StringProvider.getStringComposable(R.string.common_next)
    val mainText = StringProvider.getStringComposable(R.string.sawi_adjustment_main_text)

    var crosshairPosition = remember { mutableStateOf<Offset?>(null) }
    var circlePosition = remember { mutableStateOf<Offset?>(null) }
    var showHowToDialog by remember { mutableStateOf(false) }

    if (showHowToDialog) {
        PhoriaHowToDialog(onDismissRequest = { showHowToDialog = false })
    }
    Scaffold(
        containerColor = Color.Black,
        topBar = {
            TopBarVertical(
                title = screenTitle,
                showBackButton = true,
                onBackClicked = onBackClicked,
                actions = {
                    HowToButton(onClick = { showHowToDialog = true })
                },
                containerColor = Color.Black,
                contentColor = Color.White,
            )
        },
        bottomBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(Color.Black)
                        .padding(start = 24.dp, end = 24.dp, top = 24.dp, bottom = 24.dp),
            ) {
                PrimaryButton(
                    onClick = {
                        if (crosshairPosition != null && circlePosition != null) {
                            onConfirmClicked(crosshairPosition.value!!, circlePosition.value!!)
                        }
                    },
                    text = nextButtonText,
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 24.dp),
                )
            }
        },
    ) { paddingValues ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(Color.Black)
                    .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            PhoriaAdjustmentCanvas(
                crosshairPosition,
                circlePosition
            )
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = mainText,
                color = Color.White,
                fontSize = 48.sp,
                textAlign = TextAlign.Center,
            )
        }
    }

}

@Preview(showBackground = true, device = "spec:width=1280dp,height=800dp,dpi=240")
@Composable
private fun PhoriaAdjustmentScreenHorizontalPreview() {
    NenoonKioskTheme {
        PhoriaAdjustmentScreen(
            onConfirmClicked = { _, _ -> },
            onBackClicked = { },
            onShowHowToClicked = { }
        )
    }
}


@Preview(showBackground = true, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PhoriaAdjustmentScreenVerticalPreview() {
    NenoonKioskTheme {
        PhoriaAdjustmentScreen(
            onConfirmClicked = { _, _ -> },
            onBackClicked = { },
            onShowHowToClicked = { }
        )
    }
}
