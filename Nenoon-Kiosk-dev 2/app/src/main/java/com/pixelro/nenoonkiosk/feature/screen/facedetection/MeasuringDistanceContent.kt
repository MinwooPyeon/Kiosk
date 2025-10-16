package com.pixelro.nenoonkiosk.feature.screen.facedetection

import android.content.Context
import android.speech.tts.TextToSpeech
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.TTS
import com.pixelro.nenoonkiosk.constants.NavConstants
import com.pixelro.nenoonkiosk.util.AnimationProvider
import com.pixelro.nenoonkiosk.constants.GlobalValue
import com.pixelro.nenoonkiosk.util.StringProvider
import com.pixelro.nenoonkiosk.util.dataprovider.TestType
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@Composable
fun MeasuringDistanceContent(
    measuringDistanceContentVisibleState: MutableTransitionState<Boolean>,
    toNextContent: () -> Unit,
    selectedTestType: TestType,
    isLeftEye: Boolean,
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val sharedPreferences = remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")
    val faceDetectionTextSize = if (savedLanguage == "ru") 20.sp else 35.sp
    val warningBoxTextSize = if (savedLanguage == "ru") 25.sp else 50.sp
    val testStartTextSize = if (savedLanguage == "ru") 20.sp else 40.sp

    AnimatedVisibility(
        visibleState = measuringDistanceContentVisibleState,
        enter = AnimationProvider.enterTransition,
        exit = AnimationProvider.exitTransition
    ) {
        val isFaceDetected = faceDetectionViewModel.isFaceDetected.collectAsState().value
        val isRightEyeCovered = faceDetectionViewModel.isRightEyeCovered.collectAsState().value
        val isLeftEyeCovered = faceDetectionViewModel.isLeftEyeCovered.collectAsState().value
        val isDistanceOK = faceDetectionViewModel.isDistanceOK.collectAsState().value

        val isOccluderPickedTTSDone =
            faceDetectionViewModel.isOccluderPickedTTSDone.collectAsState().value
        val isFaceDetectedTTSDone =
            faceDetectionViewModel.isFaceDetectedTTSDone.collectAsState().value
        val isEyeCoveredTTSDone =
            faceDetectionViewModel.isEyeCoveredTTSDone.collectAsState().value
        val isDistanceMeasuredTTSDone =
            faceDetectionViewModel.isDistanceMeasuredTTSDone.collectAsState().value
        val isPressStartButtonTTSDone =
            faceDetectionViewModel.isPressStartButtonTTSDone.collectAsState().value


        val coroutineScope = rememberCoroutineScope()
        /**
         * 0 = true - 거리 부족
         * 1 = true - 모든 조건 충족
         * 2 = true - 거리 초과
         * 5 = false
         */
        val isWarningShowing = remember { mutableStateOf(5) }

        LaunchedEffect(isLeftEye) {
            if (isLeftEye) {
                faceDetectionViewModel.updateIsOccluderPickedTTSDone(false)
            }
            faceDetectionViewModel.updateIsFaceDetectedTTSDone(false)
            faceDetectionViewModel.updateIsEyeCoveredTTSDone(false)
            faceDetectionViewModel.updateIsDistanceMeasuredTTSDone(false)
            faceDetectionViewModel.updateIsPressStartButtonTTSDone(false)
        }

        /**
         * TTS
         */
        if (
            isOccluderPickedTTSDone
            && !isFaceDetectedTTSDone
            && !TTS.tts.isSpeaking
        ) {
            if (isLeftEye) {
                TTS.speechTTS(
                    StringProvider.getString(
                        R.string.tts_align_middle_center),
                    TextToSpeech.QUEUE_ADD
                )
            }
            faceDetectionViewModel.updateIsFaceDetectedTTSDone(true)
        }

        if (
            isFaceDetectedTTSDone
            && isFaceDetected
            && !isEyeCoveredTTSDone
            && !TTS.tts.isSpeaking
        ) {
            faceDetectionViewModel.updateIsEyeCoveredTTSDone(true)
            when (isLeftEye) {
                true -> TTS.speechTTS(
                    StringProvider.getString(R.string.tts_cover_right_eye, ),
                    TextToSpeech.QUEUE_ADD
                )

                false -> TTS.speechTTS(
                    StringProvider.getString(R.string.tts_cover_left_eye, ),
                    TextToSpeech.QUEUE_ADD
                )
            }
        }
        if (
            isEyeCoveredTTSDone
            && when (isLeftEye) {
                true -> isRightEyeCovered
                false -> isLeftEyeCovered
            }
            && !isDistanceMeasuredTTSDone
            && !TTS.tts.isSpeaking
        ) {
            faceDetectionViewModel.updateIsDistanceMeasuredTTSDone(true)
            TTS.speechTTS(
                StringProvider.getString(R.string.tts_cover_distance, ),
                TextToSpeech.QUEUE_ADD
            )
        }
        if (
            isDistanceMeasuredTTSDone
            && isDistanceOK == 1
            && !isPressStartButtonTTSDone
            && !TTS.tts.isSpeaking
        ) {
            faceDetectionViewModel.updateIsPressStartButtonTTSDone(true)
            TTS.speechTTS(
                StringProvider.getString(R.string.tts_start_button, ),
                TextToSpeech.QUEUE_ADD
            )
        }

        val transition = rememberInfiniteTransition()
        val shiftVal by transition.animateFloat(
            initialValue = 0f, targetValue = 1f, animationSpec = infiniteRepeatable(
                animation = keyframes {
                    durationMillis = 700
                    delayMillis = 0
                },
                repeatMode = RepeatMode.Reverse
            )
        )
        var isDialogShowing by remember { mutableStateOf(true) }
        if (isDialogShowing && isLeftEye) {
            MeasuringDistanceDialog(
                onDismissRequest = {
                    isDialogShowing = false
                }
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Box(
                modifier = Modifier
                    .padding(top = 40.dp)
                    .fillMaxWidth()
                    .height(740.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                /**
                 * 카메라 preview
                 */
                Box(
                    modifier = Modifier
                        .height(740.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    FaceDetectionWithPreview(measuringDistanceContentVisibleState.targetState)

                    Image(
                        modifier = Modifier
                            .width(450.dp)
                            .height(660.dp),
                        painter = painterResource(id = R.drawable.face_frame),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(Color(0xff1d71e1))
                    )
                }
                /**
                 * 눈가리개 이미지
                 */
                Box(
                    modifier = Modifier
                        .padding(top = 80.dp)
                        .fillMaxWidth()
                        .height(600.dp),
                    contentAlignment = Alignment.Center
                ) {
                    if (faceDetectionViewModel.isFaceDetected.collectAsState().value) {
                        if (!isLeftEye) {
                            Image(
                                modifier = Modifier
                                    .width((300 * 300 / faceDetectionViewModel.screenToFaceDistance.collectAsState().value).dp)
                                    .height((600 * 300 / faceDetectionViewModel.screenToFaceDistance.collectAsState().value).dp)
                                    .offset(
                                        x = (310 - (faceDetectionViewModel.rightEyePosition.collectAsState().value.x / 1.75f)).dp,
                                        y = (faceDetectionViewModel.rightEyePosition.collectAsState().value.y / 1.75f - 360).dp
                                    )
                                    .alpha(shiftVal),
                                painter = painterResource(id = R.drawable.occluder),
                                contentDescription = null,
                            )
                        } else {
                            Image(
                                modifier = Modifier
                                    .width((300 * 300 / faceDetectionViewModel.screenToFaceDistance.collectAsState().value).dp)
                                    .height((600 * 300 / faceDetectionViewModel.screenToFaceDistance.collectAsState().value).dp)
                                    .offset(
                                        x = (310 - (faceDetectionViewModel.leftEyePosition.collectAsState().value.x / 1.75f)).dp,
                                        y = (faceDetectionViewModel.leftEyePosition.collectAsState().value.y / 1.75f - 360).dp
                                    )
                                    .alpha(shiftVal),
                                painter = painterResource(id = R.drawable.occluder),
                                contentDescription = null,
                            )
                        }
                    }
                }

                /**
                 * 상단 안내문
                 */
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .background(
                            color = Color(0xff000000)
                        ),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        modifier = Modifier
                            .padding(start = 20.dp, end = 20.dp, bottom = 20.dp),
                        text = when (faceDetectionViewModel.isFaceDetected.collectAsState().value) {
                            true -> when (!isLeftEye) {
                                true -> when (faceDetectionViewModel.isLeftEyeCovered.collectAsState().value && faceDetectionViewModel.isNenoonTextDetected.collectAsState().value) {
                                    true -> {
                                        when (faceDetectionViewModel.isDistanceOK.collectAsState().value) {
                                            1 -> StringProvider.getString(
                                                R.string.measuring_distance_description1,
                                                
                                            )
                                            else -> StringProvider.getString(
                                                R.string.measuring_distance_description2,
                                                
                                            )
                                        }
                                    }

                                    false -> StringProvider.getString(
                                        R.string.measuring_distance_description3_cover_left,
                                        
                                    )
                                }

                                false -> when (faceDetectionViewModel.isRightEyeCovered.collectAsState().value && faceDetectionViewModel.isNenoonTextDetected.collectAsState().value) {
                                    true -> {
                                        when (faceDetectionViewModel.isDistanceOK.collectAsState().value) {
                                            1 -> StringProvider.getString(
                                                R.string.measuring_distance_description1,
                                                
                                            )
                                            else -> StringProvider.getString(
                                                R.string.measuring_distance_description2,
                                                
                                            )
                                        }
                                    }

                                    false -> StringProvider.getString(
                                        R.string.measuring_distance_description4_cover_right,
                                        
                                    )
                                }
                            }

                            false -> StringProvider.getString(
                                R.string.measuring_distance_description5_face_center,
                                
                            )
                        },
                        color = Color(0xffffffff),
                        fontSize = faceDetectionTextSize,
                        fontWeight = FontWeight.ExtraBold
                    )
                }

                /**
                 * 경고 박스
                 */
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    if (isWarningShowing.value in 0..4) {
                        Text(
                            modifier = Modifier
                                .padding(start = 40.dp, end = 40.dp)
                                .border(
                                    border = BorderStroke(2.dp, Color(0xFF000000)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    color = Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(20.dp)
                                .fillMaxWidth(),
                            text = when (isWarningShowing.value) {
                                0 -> buildAnnotatedString {append(StringProvider.getString(
                                    R.string.dialog_description3_distance_further,
                                    
                                ))}
                                1 ->
                                    buildAnnotatedString {
                                        append(StringProvider.getString(
                                            R.string.dialog_description2_announcement1,
                                            
                                        ))
                                        withStyle(
                                            style = SpanStyle(
                                                color = Color(0xff1d71e1),
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append(StringProvider.getString(
                                                R.string.dialog_description2_announcement2,
                                                
                                            ))
                                        }
                                        append(StringProvider.getString(
                                            R.string.dialog_description2_announcement3,
                                            
                                        ))
                                    }
                                else -> buildAnnotatedString {append(StringProvider.getString(
                                    R.string.dialog_description4_closer,
                                    
                                ))}
                            },
                            fontSize = warningBoxTextSize,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                /**
                 * 하단 안내문
                 */
                Box(
                    modifier = Modifier
                        .fillMaxWidth(),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = (GlobalValue.navigationBarPadding + 284).dp),
                        text = StringProvider.getString(
                            R.string.test_screen_current_distance),
                        color = Color(0xffffffff),
                        fontSize = 24.sp
                    )
                    Text(
                        modifier = Modifier
                            .padding(bottom = (GlobalValue.navigationBarPadding + 100).dp),
                        color = when (selectedTestType) {
                            TestType.ShortDistanceVisualAcuity -> {
                                when (faceDetectionViewModel.screenToFaceDistance.collectAsState().value) {
                                    in 396.0..505.0 -> Color(0xff1d71e1)
                                    else -> Color(0xffff0000)
                                }
                            }

                            else -> {
                                when (faceDetectionViewModel.screenToFaceDistance.collectAsState().value) {
                                    in 246.0..355.0 -> Color(0xff1d71e1)
                                    else -> Color(0xffff0000)
                                }
                            }
                        },
                        text = "${(faceDetectionViewModel.screenToFaceDistance.collectAsState().value / 10).roundToInt()}cm",
                        fontSize = 140.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Box(
                        modifier = Modifier
                            .padding(
                                start = 40.dp,
                                end = 40.dp,
                                bottom = (GlobalValue.navigationBarPadding + 340).dp
                            )
                            .border(
                                border = when (selectedTestType) {
                                    TestType.ShortDistanceVisualAcuity -> {
                                        if (faceDetectionViewModel.screenToFaceDistance.collectAsState().value > 505.0
                                            || faceDetectionViewModel.screenToFaceDistance.collectAsState().value < 396.0
                                        ) {
                                            BorderStroke(3.dp, Color(0xffff0000))
                                        } else {
                                            BorderStroke(1.dp, Color(0xff1d71e1))
                                        }
                                    }

                                    else -> {
                                        if (faceDetectionViewModel.screenToFaceDistance.collectAsState().value > 355.0
                                            || faceDetectionViewModel.screenToFaceDistance.collectAsState().value < 246.0
                                        ) {
                                            BorderStroke(3.dp, Color(0xffff0000))
                                        } else {
                                            BorderStroke(1.dp, Color(0xff1d71e1))
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(50)
                            )
                            .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 12.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (selectedTestType) {
                                TestType.ShortDistanceVisualAcuity -> buildAnnotatedString {
                                    append(StringProvider.getString(
                                        R.string.measuring_distance_description6_start,
                                        
                                    ))
                                    withStyle(
                                        style = SpanStyle(
                                            color = Color(0xff1d71e1),
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(" 40~50cm")
                                    }
                                    append(StringProvider.getString(
                                        R.string.measuring_distance_description6_end,
                                        
                                    ))
                                }
                                else -> buildAnnotatedString {
                                    append(StringProvider.getString(
                                        R.string.measuring_distance_description6_start,
                                        
                                    ))
                                    withStyle(
                                        style = SpanStyle(
                                            color = Color(0xff1d71e1),
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(
                                            " 25~35cm"
                                        )
                                    }
                                    append(StringProvider.getString(
                                        R.string.measuring_distance_description6_end,
                                        
                                    ))
                                }
                            },
                            fontSize = 32.sp,
                            color = Color(0xffffffff),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                /**
                 * isDistanceOK값 지정
                 * 0 = 거리 부족
                 * 1 = 모든 조건 충족
                 * 2 = 거리 초과
                 * 4 = 눈가리개 인식 X
                 */
                if (
                    /**
                     * 조건 1: 눈가리개 인식
                     * 조건 2: 눈가리개 위치
                     * 조건 1 & 2
                     */
                    faceDetectionViewModel.isNenoonTextDetected.collectAsState().value
                    && when (!isLeftEye) {
                        true -> faceDetectionViewModel.isLeftEyeCovered.collectAsState().value
                        false -> faceDetectionViewModel.isRightEyeCovered.collectAsState().value
                    }
                ) {
                    when (selectedTestType) {
                        TestType.ShortDistanceVisualAcuity -> {
                            when (faceDetectionViewModel.screenToFaceDistance.collectAsState().value) {
                                in 0.1..396.0 -> {
                                    faceDetectionViewModel.updateIsDistanceOK(0)
                                }

                                in 505.0..995.0 -> {
                                    faceDetectionViewModel.updateIsDistanceOK(2)
                                }

                                else -> {
                                    faceDetectionViewModel.updateIsDistanceOK(1)
                                }
                            }
                        }

                        else -> {
                            when (faceDetectionViewModel.screenToFaceDistance.collectAsState().value) {
                                in 0.1..246.0 -> {
                                    faceDetectionViewModel.updateIsDistanceOK(0)
                                }

                                in 355.0..995.0 -> {
                                    faceDetectionViewModel.updateIsDistanceOK(2)
                                }

                                else -> {
                                    faceDetectionViewModel.updateIsDistanceOK(1)
                                }
                            }
                        }
                    }
                } else {
                    faceDetectionViewModel.updateIsDistanceOK(4)
                }

                /**
                 * 검사 시작 버튼
                 */
                if (isDistanceOK in 0..2) {
                    Box(
                        modifier = Modifier
                            .padding(
                                start = 40.dp,
                                end = 40.dp,
                                bottom = 40.dp
                            )
                            .fillMaxWidth()
                            .height(80.dp)
                            .clip(
                                shape = RoundedCornerShape(8.dp)
                            )
                            .background(
//                            color = Color(0xff1d71e1),
                                color = when (isDistanceOK) {
                                    1 -> Color(0xff1d71e1)
                                    else -> Color(0xffff0000)
                                },
                                shape = RoundedCornerShape(8.dp),
                            )
                            .clickable {
                                when (isDistanceOK) {
                                    0 -> {
                                        coroutineScope.launch {
                                            for (i in 1..2) {
                                                isWarningShowing.value = 0
                                                delay(400)
                                                isWarningShowing.value = 5
                                                delay(400)
                                            }
                                            isWarningShowing.value = 0
                                            delay(1500)
                                            isWarningShowing.value = 5
                                        }
                                    }

                                    1 -> {
                                        if (!TTS.tts.isSpeaking) {
                                            toNextContent()
                                        } else {
                                            coroutineScope.launch {
                                                for (i in 1..3) {
                                                    isWarningShowing.value = 1
                                                    delay(400)
                                                    isWarningShowing.value = 5
                                                    delay(400)
                                                }
                                                isWarningShowing.value = 1
                                                delay(2000)
                                                isWarningShowing.value = 5
                                            }
                                        }
                                    }

                                    else -> {
                                        coroutineScope.launch {
                                            for (i in 1..2) {
                                                isWarningShowing.value = 2
                                                delay(400)
                                                isWarningShowing.value = 5
                                                delay(400)
                                            }
                                            isWarningShowing.value = 2
                                            delay(1500)
                                            isWarningShowing.value = 5
                                        }
                                    }
                                }
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (isDistanceOK) {
                                0 -> {
                                    StringProvider.getString(
                                        R.string.dialog_description3_distance_further,
                                        
                                    )
                                }

                                1 -> {
                                    StringProvider.getString(
                                        R.string.measuring_distance_start_button,
                                        
                                    )
                                }

                                else -> {
                                    StringProvider.getString(
                                        R.string.dialog_description4_closer,
                                        
                                    )
                                }

                            },
                            fontSize = testStartTextSize,
                            color = Color(0xffffffff),
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

/**
 * 눈가리개 사용 dialog
 */
@Composable
fun MeasuringDistanceDialog(
    onDismissRequest: () -> Unit,
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel(),
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties()
    ) {
        val coroutineScope = rememberCoroutineScope()
        val isWarningShowing = remember { mutableStateOf(false) }
        LaunchedEffect(true) {
            TTS.speechTTS(StringProvider.getString(R.string.tts_dialog, ), TextToSpeech.QUEUE_ADD)
        }
        Column(
            modifier = Modifier
                .width(800.dp)
                .height(1000.dp)
                .background(
                    color = Color(0xffffffff),
                    shape = RoundedCornerShape(8.dp)
                )
        ) {
            Column(
                modifier = Modifier
                    .height(800.dp)
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 20.dp, end = 20.dp, bottom = 8.dp)
                        .fillMaxWidth(),
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) {
                            append(StringProvider.getString(
                                R.string.dialog_description1_pickup1,
                                
                            ))
                        }
                        withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = colorResource(R.color.main))) {
                            append(StringProvider.getString(
                                R.string.dialog_description1_pickup2,
                                
                            ))
                        }
                        append(StringProvider.getString(
                            R.string.dialog_description1_pickup3))
                    },
                    fontSize = 40.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .fillMaxSize()
                            .rotate(-25f),
                        painter = painterResource(id = R.drawable.occluder2),
                        contentDescription = null
                    )
                    if (isWarningShowing.value) {
                        Text(
                            modifier = Modifier
                                .padding(start = 40.dp, end = 40.dp)
                                .border(
                                    border = BorderStroke(2.dp, Color(0xFF000000)),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .background(
                                    color = Color(0xFFFFFFFF),
                                    shape = RoundedCornerShape(8.dp)
                                )
                                .padding(20.dp)
                                .fillMaxWidth(),
                            text = buildAnnotatedString {
                                append(StringProvider.getString(
                                    R.string.dialog_description2_announcement1,
                                    
                                ))
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xff1d71e1),
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(StringProvider.getString(
                                        R.string.dialog_description2_announcement2,
                                        
                                    ))
                                }
                                append(StringProvider.getString(
                                    R.string.dialog_description2_announcement3,
                                    
                                ))
                            },
                            fontSize = 50.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            Box(
                modifier = Modifier
                    .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Text(
                    modifier = Modifier
                        .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 20.dp)
                        .fillMaxWidth()
                        .clip(
                            shape = RoundedCornerShape(8.dp)
                        )
                        .border(
                            border = BorderStroke(1.dp, Color(0xffc3c3c3)),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            if (TTS.tts.isSpeaking) {
                                coroutineScope.launch {
                                    for (i in 1..3) {
                                        isWarningShowing.value = true
                                        delay(400)
                                        isWarningShowing.value = false
                                        delay(400)
                                    }
                                    isWarningShowing.value = true
                                    delay(2000)
                                    isWarningShowing.value = false
                                }
                            } else {
                                faceDetectionViewModel.updateIsOccluderPickedTTSDone(true)
                                onDismissRequest()
                            }
                        }
                        .padding(20.dp),
                    text = StringProvider.getString(R.string.confirm, ),
                    fontSize = 50.sp,
                    color = Color(0xff1d71e1),
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}