package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.common.MediaItem
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetection
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.components.NineAreaOverlay
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.components.PrimaryCompleteButton
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.components.TopCenterGuide
import kotlin.math.tan

@androidx.annotation.OptIn(UnstableApi::class)
@Composable
fun AmslerGridInspectionScreen(
    state: AmslerGridUiState,
    rotX: Float,
    rotY: Float,
    exoPlayer: ExoPlayer?,
    onAreaPressed: (Offset) -> Unit,
    onCompletePressed: () -> Unit,
    modifier: Modifier = Modifier
) {
    FaceDetection()

    val headerAnnotated = when {
        !state.isBlinkingDone -> buildAnnotatedString {
            append(stringResource(R.string.amsler_description1_blink))
        }
        state.isFaceCenter -> buildAnnotatedString {
            withStyle(SpanStyle(color = Color(0xff1d71e1), fontWeight = FontWeight.Bold)) {
                append(stringResource(R.string.amsler_description2_distortion_1))
            }
            append(" " + stringResource(R.string.amsler_description2_distortion_2))
            withStyle(SpanStyle(color = Color(0xffff0000), fontWeight = FontWeight.Bold)) {
                append(" " + stringResource(R.string.amsler_description2_distortion_3) + " ")
            }
            append(stringResource(R.string.amsler_description2_distortion_4))
        }
        else -> buildAnnotatedString {
            append(stringResource(R.string.amsler_description3_center))
        }
    }

    val shouldPlayGuideVideo = state.isBlinkingDone && state.isFaceCenter &&
            try { TTS.tts.isSpeaking } catch (e: Exception) { false } &&
            !state.isTestStarted && state.isLeftEye

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xff000000)),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            modifier = Modifier
                .padding(20.dp)
                .height(160.dp),
            text = headerAnnotated,
            fontSize = when {
                !state.isBlinkingDone -> 40.sp
                state.isFaceCenter -> 32.sp
                else -> 40.sp
            },
            color = Color(0xffffffff),
            fontWeight = FontWeight.Medium
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .background(color = Color(0xffffffff), shape = RoundedCornerShape(12.dp))
                .width(700.dp)
                .height(700.dp)
        ) {
            if (shouldPlayGuideVideo && exoPlayer != null) {
                val context = LocalContext.current
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = {
                        PlayerView(context).apply {
                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                            player = exoPlayer
                            useController = false
                            exoPlayer.setMediaItem(
                                MediaItem.fromUri(
                                    RawResourceDataSource.buildRawResourceUri(R.raw.amsler_video_3)
                                )
                            )
                            exoPlayer.prepare()
                            exoPlayer.play()
                        }
                    }
                )
            } else {
                Image(
                    modifier = Modifier
                        .padding(40.dp)
                        .width(600.dp)
                        .height(600.dp),
                    painter = painterResource(id = R.drawable.amsler_grid),
                    contentDescription = null
                )

                Canvas(
                    modifier = Modifier
                        .padding(40.dp)
                        .width(600.dp)
                        .height(600.dp)
                ) {
                    if (state.isDotShowing) {
                        drawCircle(
                            color = Color(0xff000000),
                            radius = 50f,
                            center = Offset(450f, 450f)
                        )
                    }
                    drawCircle(
                        color = Color(0xff0000ff),
                        radius = 20f,
                        center = Offset(
                            450f - (400f * tan(rotY * 0.0174533)).toFloat(),
                            450f - (400f * tan((rotX + 10) * 0.0174533)).toFloat()
                        )
                    )
                }

                if (state.isTestStarted) {
                    NineAreaOverlay(
                        areas = state.currentSelectedArea,
                        onPress = onAreaPressed
                    )
                }
            }
        }

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (state.isTestStarted) {
                PrimaryCompleteButton(onClick = onCompletePressed)
            } else if (state.isFaceCenter && state.isLeftEye) {
                TopCenterGuide()
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 888, heightDp = 1422, apiLevel = 34)
@Composable
private fun Preview_AmslerGrid() {
    AmslerGridInspectionScreen(
        state = AmslerGridUiState(
            isBlinkingDone = true,
            isFaceCenter = true,
            isTestStarted = true,
            currentSelectedArea = listOf(
                MacularDisorderType.Normal,
                MacularDisorderType.Distorted,
                MacularDisorderType.Normal,
                MacularDisorderType.Normal,
                MacularDisorderType.Distorted,
                MacularDisorderType.Normal,
                MacularDisorderType.Normal,
                MacularDisorderType.Normal,
                MacularDisorderType.Normal
            )
        ),
        rotX = 0f,
        rotY = 0f,
        exoPlayer = null,
        onAreaPressed = {},
        onCompletePressed = {}
    )
}
