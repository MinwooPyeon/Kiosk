package com.pixelro.nenoonkiosk.feature.inspection.macular.mchart.progress

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetection
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun MChartInspectionContent(
    uiState: MChartUiState,
    exoPlayer: ExoPlayer? = null,
    onStraightClick: () -> Unit,
    onBentClick: () -> Unit,
    onTTSDone: () -> Unit,
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val inPreview = LocalInspectionMode.current // Preview 감지

    // Preview 모드에서는 TTS 실행 금지
    if (!inPreview) {
        LaunchedEffect(Unit) {
            TTS.setOnDoneListener { onTTSDone() }
            TTS.speechTTS(context.getString(R.string.tts_straight_or_bent), TextToSpeech.QUEUE_ADD)
            TTS.speechTTS(context.getString(R.string.tts_start), TextToSpeech.QUEUE_ADD)
        }
        DisposableEffect(Unit) { onDispose { TTS.clearOnDoneListener() } }
    }

    // Preview 모드에서는 FaceDetection 비활성화
    if (!inPreview) {
        FaceDetection()
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 상단 안내문
        Text(
            modifier = Modifier.padding(top = 40.dp, bottom = 40.dp),
            text = buildAnnotatedString {
                append(stringResource(R.string.mchart_test_description_1) + " ")
                withStyle(SpanStyle(neNoon_blue, fontWeight = FontWeight.Bold)) {
                    append(stringResource(R.string.mchart_test_description_2) + " ")
                }
                append(stringResource(R.string.mchart_test_description_3))
                withStyle(SpanStyle(neNoon_blue, fontWeight = FontWeight.Bold)) {
                    append(" " + stringResource(R.string.mchart_test_description_4))
                }
                append(stringResource(R.string.mchart_test_description_5))
            },
            fontSize = 32.sp,
            color = Color.White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        // 검사 시표 (영상 or 이미지)
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .width(700.dp)
                .height(700.dp)
                .background(Color.White, RoundedCornerShape(12.dp))
        ) {
            val showVideo =
                !inPreview &&
                        uiState.isTTSSpeaking &&
                        !uiState.isTesting &&
                        uiState.isLeftEye &&
                        exoPlayer != null

            if (showVideo) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        PlayerView(context).apply {
                            useController = false
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            player = exoPlayer
                            exoPlayer?.apply {
                                setMediaItem(
                                    MediaItem.fromUri(
                                        RawResourceDataSource.buildRawResourceUri(R.raw.mchart_video_2)
                                    )
                                )
                                prepare()
                                pause()
                                coroutineScope.launch {
                                    delay(4000)
                                    play()
                                }
                            }
                        }
                    },
                )
            } else {
                Image(
                    modifier = Modifier
                        .padding(start = 40.dp, end = 40.dp, top = 10.dp)
                        .fillMaxSize()
                        .rotate(if (uiState.isVertical) 0f else 90f),
                    painter = painterResource(id = uiState.imageId),
                    contentDescription = null,
                )
            }
        }

        // 하단 버튼
        if (!uiState.isTTSSpeaking) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                PrimaryButton(
                    onClick = onStraightClick,
                    text = stringResource(R.string.mchart_test_content_straight),
                    modifier = Modifier
                        .padding(horizontal = 40.dp, vertical = 10.dp)
                        .height(80.dp)
                )
                PrimaryButton(
                    onClick = onBentClick,
                    text = stringResource(R.string.mchart_test_content_bent),
                    modifier = Modifier
                        .padding(horizontal = 40.dp, vertical = 10.dp)
                        .height(80.dp)
                )
            }
        } else if (uiState.isLeftEye) {
            Text(
                text = buildAnnotatedString {
                    append(stringResource(R.string.presbyopia_video_guide_1))
                    withStyle(SpanStyle(color = Color(0xff1d71e1), fontWeight = FontWeight.Bold)) {
                        append(" " + stringResource(R.string.presbyopia_video_guide_2))
                    }
                    append(stringResource(R.string.presbyopia_video_guide_3))
                },
                fontWeight = FontWeight.Bold,
                fontSize = 45.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}


@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    apiLevel = 34,
    widthDp = 800,
    heightDp = 1280,
    name = "MChart Content Preview"
)
@Composable
fun MChartContentPreview() {

    val dummyState = MChartUiState(
        isLeftEye = true,
        isVertical = true,
        currentLevel = 5,
        imageId = R.drawable.mchart_0_0,
        isTTSSpeaking = false,
        isTesting = true
    )

    NenoonKioskTheme {
        MChartInspectionContent(
            uiState = dummyState,
            exoPlayer = null,
            onStraightClick = {},
            onBentClick = {},
            onTTSDone = {}
        )
    }
}

