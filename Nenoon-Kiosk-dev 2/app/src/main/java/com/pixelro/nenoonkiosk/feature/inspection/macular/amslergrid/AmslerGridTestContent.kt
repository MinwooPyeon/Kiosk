package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid

import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.media3.common.MediaItem
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.AnimationProvider
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.core.util.dataprovider.TestType
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetection
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetectionViewModel
import com.pixelro.nenoonkiosk.feature.facedetection.MeasuringDistanceContent
import kotlin.math.tan

@Composable
fun AmslerGridTestContent(
    toResultScreen: (AmslerGridTestResult) -> Unit,
    amslerGridViewModel: AmslerGridViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        amslerGridViewModel.init()
    }
    val measuringDistanceContentVisibleState = remember { MutableTransitionState(true) }
    measuringDistanceContentVisibleState.targetState =
        amslerGridViewModel.isMeasuringDistanceContentVisible.collectAsState().value
    val amslerGridContentVisibleState = remember { MutableTransitionState(false) }
    amslerGridContentVisibleState.targetState =
        amslerGridViewModel.isAmslerGridContentVisible.collectAsState().value

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    Color(0xff000000),
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            MeasuringDistanceContent(
                measuringDistanceContentVisibleState = measuringDistanceContentVisibleState,
                toNextContent = {
                    amslerGridViewModel.updateIsMeasuringDistanceContentVisible(false)
                    amslerGridViewModel.updateIsAmslerGridContentVisible(true)
                },
                selectedTestType = TestType.AmslerGrid,
                isLeftEye = amslerGridViewModel.isLeftEye.collectAsState().value,
            )
            AmslerGridContent(
                amslerGridContentVisibleState = amslerGridContentVisibleState,
                toResultScreen = toResultScreen,
            )
        }
    }
}

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun AmslerGridContent(
    amslerGridContentVisibleState: MutableTransitionState<Boolean>,
    toResultScreen: (AmslerGridTestResult) -> Unit,
    amslerGridViewModel: AmslerGridViewModel = hiltViewModel(),
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel(),
) {
    val exoPlayer = amslerGridViewModel.exoPlayer
    val context = LocalContext.current

    AnimatedVisibility(
        visibleState = amslerGridContentVisibleState,
        enter = AnimationProvider.enterTransition,
        exit = AnimationProvider.exitTransition,
    ) {
        LaunchedEffect(true) {
            amslerGridViewModel.startBlinking()
            amslerGridViewModel.updateIsLookAtTheDotTTSDone(false)
            amslerGridViewModel.updateIsSelectTTSDone(false)
        }
        val isBlinkingDone = amslerGridViewModel.isBlinkingDone.collectAsState().value
        val isDotShowing = amslerGridViewModel.isDotShowing.collectAsState().value
        val isFaceCenter = amslerGridViewModel.isFaceCenter.collectAsState().value
        val isSelectTTSDone = amslerGridViewModel.isSelectTTSDone.collectAsState().value
        val isTestStarted = amslerGridViewModel.isTestStarted.collectAsState().value

        /**
         * TTS
         */
        if (!amslerGridViewModel.isLookAtTheDotTTSDone.collectAsState().value) {
            amslerGridViewModel.updateIsLookAtTheDotTTSDone(true)
            TTS.speechTTS(StringProvider.getString(R.string.tts_blink), TextToSpeech.QUEUE_ADD)
        }
        if (
            amslerGridViewModel.isLookAtTheDotTTSDone.collectAsState().value &&
            isFaceCenter &&
            !isSelectTTSDone
        ) {
            amslerGridViewModel.updateIsSelectTTSDone(true)
            TTS.setOnDoneListener {
                amslerGridViewModel.updateIsTestStarted(true)
                TTS.clearOnDoneListener()
            }
            TTS.speechTTS(
                StringProvider.getString(R.string.tts_description),
                TextToSpeech.QUEUE_ADD,
            )
            TTS.speechTTS(StringProvider.getString(R.string.tts_start), TextToSpeech.QUEUE_ADD)
        }
        FaceDetection()
        Column(
            modifier =
                Modifier
                    .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            val rotX = faceDetectionViewModel.rotX.collectAsState().value
            val rotY = faceDetectionViewModel.rotY.collectAsState().value
            val currentSelectedArea = amslerGridViewModel.currentSelectedArea.collectAsState().value
            val isLeft = amslerGridViewModel.isLeftEye.collectAsState().value

            /**
             * 상단 안내문
             */
            Text(
                modifier =
                    Modifier
                        .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 20.dp)
                        .height(160.dp),
                text =
                    when (!isBlinkingDone) {
                        true ->
                            buildAnnotatedString {
                                append(
                                    StringProvider.getString(
                                        R.string.amsler_description1_blink,
                                    ),
                                )
                            }
                        false ->
                            when (isFaceCenter) {
                                true ->
                                    buildAnnotatedString {
                                        withStyle(
                                            style =
                                                SpanStyle(
                                                    color = Color(0xff1d71e1),
                                                    fontWeight = FontWeight.Bold,
                                                ),
                                        ) {
                                            append(
                                                StringProvider.getString(
                                                    R.string.amsler_description2_distortion_1,
                                                ),
                                            )
                                        }
                                        append(
                                            " " +
                                                StringProvider.getString(
                                                    R.string.amsler_description2_distortion_2,
                                                ),
                                        )
                                        withStyle(
                                            style =
                                                SpanStyle(
                                                    color = Color(0xffff0000),
                                                    fontWeight = FontWeight.Bold,
                                                ),
                                        ) {
                                            append(
                                                " " +
                                                    StringProvider.getString(
                                                        R.string.amsler_description2_distortion_3,
                                                    ) + " ",
                                            )
                                        }
                                        append(
                                            StringProvider.getString(
                                                R.string.amsler_description2_distortion_4,
                                            ),
                                        )
                                    }
                                false ->
                                    buildAnnotatedString {
                                        append(
                                            StringProvider.getString(
                                                R.string.amsler_description3_center,
                                            ),
                                        )
                                    }
                            }
                    },
                fontSize =
                    when (!isBlinkingDone) {
                        true -> 40.sp
                        false ->
                            when (isFaceCenter) {
                                true -> 32.sp
                                false -> 40.sp
                            }
                    },
                color = Color(0xffffffff),
                fontWeight = FontWeight.Medium,
            )
            /**
             * 암슬러 격자 시표
             */
            Box(
                contentAlignment = Alignment.Center,
                modifier =
                    Modifier
                        .background(
                            color = Color(0xffffffff),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .width(700.dp)
                        .height(700.dp),
            ) {
                if (isBlinkingDone && isFaceCenter && TTS.tts.isSpeaking && !isTestStarted && isLeft) {
                    AndroidView(
                        modifier =
                            Modifier
                                .fillMaxSize(),
                        factory = {
                            PlayerView(context).apply {
                                resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                player = exoPlayer
                                useController = false
                                exoPlayer.setMediaItem(
                                    MediaItem.fromUri(
                                        RawResourceDataSource.buildRawResourceUri(
                                            R.raw.amsler_video_3,
                                        ),
                                    ),
                                )
                                exoPlayer.prepare()
                                exoPlayer.play()
                            }
                        },
                    )
                } else {
                    Image(
                        modifier =
                            Modifier
                                .padding(40.dp)
                                .width(600.dp)
                                .height(600.dp),
                        painter = painterResource(id = R.drawable.amsler_grid),
                        contentDescription = "",
                    )
                    Canvas(
                        modifier =
                            Modifier
                                .padding(40.dp)
                                .width(600.dp)
                                .height(600.dp),
                    ) {
                        if (isDotShowing) {
                            drawCircle(
                                color = Color(0xff000000),
                                radius = 50f,
                                center = Offset(450f, 450f),
                            )
                        }
//                        if (!isFaceCenter) {
                        drawCircle(
                            color = Color(0xff0000ff),
                            radius = 20f,
                            center =
                                Offset(
                                    450f - (400f * tan(rotY * 0.0174533)).toFloat(),
                                    450f - (400f * tan((rotX + 10) * 0.0174533)).toFloat(),
                                ),
                        )
//                        }
                        if (isBlinkingDone && !isFaceCenter && 450f - (400f * tan(rotY * 0.0174533)).toFloat() > 400f && 450f -
                            (
                                400f *
                                    tan(
                                        rotY * 0.0174533,
                                    )
                            ).toFloat() < 500f &&
                            450f - (400f * tan((rotX + 10) * 0.0174533)).toFloat() > 400f && 450f -
                            (
                                400f *
                                    tan(
                                        (rotX + 10) * 0.0174533,
                                    )
                            ).toFloat() < 500f
                        ) {
                            amslerGridViewModel.updateIsFaceCenter(true)
                        }
                    }
                    /**
                     * 터치 반응
                     */
                    if (isTestStarted) {
                        Column(
                            modifier =
                                Modifier
                                    .padding(40.dp)
                                    .width(600.dp)
                                    .height(600.dp)
                                    .pointerInput(this) {
                                        detectTapGestures(
                                            onPress = {
                                                amslerGridViewModel.updateCurrentSelectedPosition(it)
                                            },
                                        )
                                    },
                        ) {
                            for (i in 0..2) {
                                Row(
                                    modifier =
                                        Modifier
                                            .weight(1f),
                                ) {
                                    for (j in (i * 3)..(i * 3 + 2)) {
                                        Box(
                                            modifier =
                                                Modifier
                                                    .weight(1f)
                                                    .fillMaxHeight()
                                                    .background(
                                                        color =
                                                            when (currentSelectedArea[j]) {
                                                                MacularDisorderType.Normal ->
                                                                    Color(
                                                                        0x00000000,
                                                                    )

                                                                else -> Color(0x550000ff)
                                                            },
                                                    ),
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            /**
             * 검사 완료 버튼
             */
            Box(
                modifier =
                    Modifier
                        .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                if (isTestStarted) {
                    Box(
                        modifier =
                            Modifier
                                .padding(
                                    start = 40.dp,
                                    end = 40.dp,
                                    bottom = 40.dp,
                                )
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .background(
                                    color = Color(0xff1d71e1),
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .clickable {
                                    if (isLeft) {
                                        amslerGridViewModel.updateIsLeftEye(false)
                                        amslerGridViewModel.updateLeftSelectedArea()
                                        amslerGridViewModel.updateIsMeasuringDistanceContentVisible(true)
                                        amslerGridViewModel.updateIsAmslerGridContentVisible(false)
                                    } else {
                                        amslerGridViewModel.updateRightSelectedArea()
                                        amslerGridViewModel.updateIsAmslerGridContentVisible(false)
                                        TTS.speechTTS(
                                            StringProvider.getString(
                                                R.string.tts_end,
                                            ),
                                            TextToSpeech.QUEUE_ADD,
                                        )
                                        toResultScreen(amslerGridViewModel.getAmslerGridTestResult())
                                    }
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            modifier =
                                Modifier
                                    .padding(bottom = 4.dp),
                            text =
                                StringProvider.getString(
                                    R.string.amsler_complete_button,
                                ),
                            fontSize = 40.sp,
                            color = Color(0xffffffff),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                } else {
                    if (isFaceCenter && isLeft) {
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxSize()
                                    .padding(top = 20.dp),
                            contentAlignment = Alignment.TopCenter,
                        ) {
                            Text(
                                text =
                                    buildAnnotatedString {
                                        append(
                                            StringProvider.getString(
                                                R.string.presbyopia_video_guide_1,
                                            ),
                                        )
                                        withStyle(
                                            style =
                                                SpanStyle(
                                                    color = Color(0xff1d71e1),
                                                    fontWeight = FontWeight.Bold,
                                                ),
                                        ) {
                                            append(
                                                " " +
                                                    StringProvider.getString(
                                                        R.string.presbyopia_video_guide_2,
                                                    ),
                                            )
                                        }
                                        append(
                                            StringProvider.getString(
                                                R.string.presbyopia_video_guide_3,
                                            ),
                                        )
                                    },
                                fontWeight = FontWeight.Bold,
                                fontSize = 40.sp,
                                color = Color(0xffffffff),
                            )
                        }
                    }
                }
            }
        }
    }
}
