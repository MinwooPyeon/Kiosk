package com.pixelro.nenoonkiosk.test.visualacuity

import android.speech.tts.TextToSpeech
import androidx.compose.animation.*
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.TTS
import com.pixelro.nenoonkiosk.util.AnimationProvider
import com.pixelro.nenoonkiosk.util.StringProvider
import com.pixelro.nenoonkiosk.feature.screen.facedetection.FaceDetection
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun VisualAcuityTestCommonContent(
    visualAcuityTestCommonContentVisibleState: MutableTransitionState<Boolean>,
    toResultScreen: (VisualAcuityTestResult) -> Unit
) {
    AnimatedVisibility(
        visibleState = visualAcuityTestCommonContentVisibleState,
        enter = AnimationProvider.enterTransition,
        exit = AnimationProvider.exitTransition
    ) {
        FaceDetection()
        VisualAcuityTestContent(
            toResultScreen = toResultScreen
        )
    }
}

@Composable
fun VisualAcuityTestContent(
    toResultScreen: (VisualAcuityTestResult) -> Unit,
    visualAcuityViewModel: VisualAcuityViewModel = hiltViewModel(),
) {
    val randomList = visualAcuityViewModel.randomList.collectAsState().value
    val ansNum = visualAcuityViewModel.ansNum.collectAsState().value
    val sightLevel = visualAcuityViewModel.sightLevel.collectAsState().value
    var progress by remember { mutableStateOf(0.1f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    val coroutineScope = rememberCoroutineScope()
    val isWarning = remember { mutableStateOf(false) }
    LaunchedEffect(true) {
        TTS.speechTTS(StringProvider.getString(
            R.string.tts_short_visualacuity), TextToSpeech.QUEUE_ADD)
    }
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        /**
         * 시력표 박스
         */
        Box(
            modifier = Modifier
                .padding(top = 40.dp)
                .height(500.dp)
                .width(500.dp)
                .background(
                    color = Color(0xffffffff),
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable {
                    coroutineScope.launch {
                        for (i in 1..4) {
                            isWarning.value = true
                            delay(600)
                            isWarning.value = false
                            delay(400)
                        }
                        isWarning.value = true
                        delay(4000)
                        isWarning.value = false
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Image(
                imageVector = ImageVector.vectorResource(id =
                    when (ansNum) {
                        2 -> when (sightLevel) {
                            1 -> R.drawable._50cm_2_1
                            2 -> R.drawable._50cm_2_2
                            3 -> R.drawable._50cm_2_3
                            4 -> R.drawable._50cm_2_4
                            5 -> R.drawable._50cm_2_5
                            6 -> R.drawable._50cm_2_6
                            7 -> R.drawable._50cm_2_7
                            8 -> R.drawable._50cm_2_8
                            9 -> R.drawable._50cm_2_9
                            else -> R.drawable._50cm_2_10
                        }
                        3 -> when (sightLevel) {
                            1 -> R.drawable._50cm_3_1
                            2 -> R.drawable._50cm_3_2
                            3 -> R.drawable._50cm_3_3
                            4 -> R.drawable._50cm_3_4
                            5 -> R.drawable._50cm_3_5
                            6 -> R.drawable._50cm_3_6
                            7 -> R.drawable._50cm_3_7
                            8 -> R.drawable._50cm_3_8
                            9 -> R.drawable._50cm_3_9
                            else -> R.drawable._50cm_3_10
                        }
                        4 -> when (sightLevel) {
                            1 -> R.drawable._50cm_4_1
                            2 -> R.drawable._50cm_4_2
                            3 -> R.drawable._50cm_4_3
                            4 -> R.drawable._50cm_4_4
                            5 -> R.drawable._50cm_4_5
                            6 -> R.drawable._50cm_4_6
                            7 -> R.drawable._50cm_4_7
                            8 -> R.drawable._50cm_4_8
                            9 -> R.drawable._50cm_4_9
                            else -> R.drawable._50cm_4_10
                        }
                        5 -> when (sightLevel) {
                            1 -> R.drawable._50cm_5_1
                            2 -> R.drawable._50cm_5_2
                            3 -> R.drawable._50cm_5_3
                            4 -> R.drawable._50cm_5_4
                            5 -> R.drawable._50cm_5_5
                            6 -> R.drawable._50cm_5_6
                            7 -> R.drawable._50cm_5_7
                            8 -> R.drawable._50cm_5_8
                            9 -> R.drawable._50cm_5_9
                            else -> R.drawable._50cm_5_10
                        }
                        6 -> when (sightLevel) {
                            1 -> R.drawable._50cm_6_1
                            2 -> R.drawable._50cm_6_2
                            3 -> R.drawable._50cm_6_3
                            4 -> R.drawable._50cm_6_4
                            5 -> R.drawable._50cm_6_5
                            6 -> R.drawable._50cm_6_6
                            7 -> R.drawable._50cm_6_7
                            8 -> R.drawable._50cm_6_8
                            9 -> R.drawable._50cm_6_9
                            else -> R.drawable._50cm_6_10
                        }
                        else -> when (sightLevel) {
                            1 -> R.drawable._50cm_7_1
                            2 -> R.drawable._50cm_7_2
                            3 -> R.drawable._50cm_7_3
                            4 -> R.drawable._50cm_7_4
                            5 -> R.drawable._50cm_7_5
                            6 -> R.drawable._50cm_7_6
                            7 -> R.drawable._50cm_7_7
                            8 -> R.drawable._50cm_7_8
                            9 -> R.drawable._50cm_7_9
                            else -> R.drawable._50cm_7_10
                        }
//                        8 -> when (sightLevel) {
//                            1 -> R.drawable._50cm_up_1
//                            2 -> R.drawable._50cm_up_2
//                            3 -> R.drawable._50cm_up_3
//                            4 -> R.drawable._50cm_up_4
//                            5 -> R.drawable._50cm_up_5
//                            6 -> R.drawable._50cm_up_6
//                            7 -> R.drawable._50cm_up_7
//                            8 -> R.drawable._50cm_up_8
//                            9 -> R.drawable._50cm_up_9
//                            else -> R.drawable._50cm_up_10
//                        }
//                        9 -> when (sightLevel) {
//                            1 -> R.drawable._50cm_right_1
//                            2 -> R.drawable._50cm_right_2
//                            3 -> R.drawable._50cm_right_3
//                            4 -> R.drawable._50cm_right_4
//                            5 -> R.drawable._50cm_right_5
//                            6 -> R.drawable._50cm_right_6
//                            7 -> R.drawable._50cm_right_7
//                            8 -> R.drawable._50cm_right_8
//                            9 -> R.drawable._50cm_right_9
//                            else -> R.drawable._50cm_right_10
//                        }
//                        10 -> when (sightLevel) {
//                            1 -> R.drawable._50cm_down_1
//                            2 -> R.drawable._50cm_down_2
//                            3 -> R.drawable._50cm_down_3
//                            4 -> R.drawable._50cm_down_4
//                            5 -> R.drawable._50cm_down_5
//                            6 -> R.drawable._50cm_down_6
//                            7 -> R.drawable._50cm_down_7
//                            8 -> R.drawable._50cm_down_8
//                            9 -> R.drawable._50cm_down_9
//                            else -> R.drawable._50cm_down_10
//                        }
//                        else -> when (sightLevel) {
//                            1 -> R.drawable._50cm_left_1
//                            2 -> R.drawable._50cm_left_2
//                            3 -> R.drawable._50cm_left_3
//                            4 -> R.drawable._50cm_left_4
//                            5 -> R.drawable._50cm_left_5
//                            6 -> R.drawable._50cm_left_6
//                            7 -> R.drawable._50cm_left_7
//                            8 -> R.drawable._50cm_left_8
//                            9 -> R.drawable._50cm_left_9
//                            else -> R.drawable._50cm_left_10
//                        }
                    }
                ),
                contentDescription = ""
            )
            if (isWarning.value) {
                Text(
                    modifier = Modifier
                        .padding(top = 300.dp),
                    text = StringProvider.getString(
                        R.string.visual_acuity_warning),
                    textAlign = TextAlign.Center,
                    fontSize = 30.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }
        Text(
            modifier = Modifier
                .padding(top = 40.dp),
            text = StringProvider.getString(
                R.string.visual_acuity_description),
            fontSize = 40.sp,
            color = Color(0xffffffff),
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )
        Spacer(
            modifier = Modifier
                .height(20.dp)
        )
        LinearProgressIndicator(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .width(490.dp)
                .height(20.dp),
            progress = animatedProgress,
            color = Color(0xff1d71e1),
        )
        /**
         * 선택지
         */
        Row() {
            /**
             * 1 번 박스
             */
            Box(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .width(150.dp)
                        .background(
                            color = Color(0xffffffff),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            visualAcuityViewModel.processAnswerSelected(
                                idx = 0,
                                handleWrong = {
                                    progress = it
                                }
                            ) {
                                toResultScreen(
                                    visualAcuityViewModel.getVisualAcuityTestResult()
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .padding(10.dp)
                            .height(100.dp),
                        imageVector = ImageVector.vectorResource(id =
                        when (randomList[0]) {
                            2 -> R.drawable.two
                            3 -> R.drawable.three
                            4 -> R.drawable.four
                            5 -> R.drawable.five
                            6 -> R.drawable.six
                            else -> R.drawable.seven
//                            8 -> R.drawable._50cm_up_1
//                            9 -> R.drawable._50cm_right_1
//                            10 -> R.drawable._50cm_down_1
//                            else -> R.drawable._50cm_left_1
                        }
                        ),
                        contentDescription = ""
                    )
                }
            }
            /**
             * 2번 박스
             */
            Box(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .width(150.dp)
                        .background(
                            color = Color(0xffffffff),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            visualAcuityViewModel.processAnswerSelected(
                                1,
                                handleWrong = {
                                    progress = it
                                }
                            ) {
                                toResultScreen(
                                    visualAcuityViewModel.getVisualAcuityTestResult()
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .padding(10.dp)
                            .height(100.dp),
                        imageVector = ImageVector.vectorResource(id =
                        when (randomList[1]) {
                            2 -> R.drawable.two
                            3 -> R.drawable.three
                            4 -> R.drawable.four
                            5 -> R.drawable.five
                            6 -> R.drawable.six
                            else -> R.drawable.seven
//                            8 -> R.drawable._50cm_up_1
//                            9 -> R.drawable._50cm_right_1
//                            10 -> R.drawable._50cm_down_1
//                            else -> R.drawable._50cm_left_1
                        }
                        ),
                        contentDescription = ""
                    )
                }
            }
            /**
             * 3번 박스
             */
            Box(
                modifier = Modifier
                    .padding(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .height(150.dp)
                        .width(150.dp)
                        .background(
                            color = Color(0xffffffff),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .clickable {
                            visualAcuityViewModel.processAnswerSelected(
                                2,
                                handleWrong = {
                                    progress = it
                                }
                            ) {
                                toResultScreen(
                                    visualAcuityViewModel.getVisualAcuityTestResult()
                                )
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        modifier = Modifier
                            .padding(10.dp)
                            .height(100.dp),
                        imageVector = ImageVector.vectorResource(id =
                        when (randomList[2]) {
                            2 -> R.drawable.two
                            3 -> R.drawable.three
                            4 -> R.drawable.four
                            5 -> R.drawable.five
                            6 -> R.drawable.six
                            else -> R.drawable.seven
//                            8 -> R.drawable._50cm_up_1
//                            9 -> R.drawable._50cm_right_1
//                            10 -> R.drawable._50cm_down_1
//                            else -> R.drawable._50cm_left_1
                        }
                        ),
                        contentDescription = ""
                    )
                }
            }
        }
        /**
         * 4번 박스
         */
        Box(
            modifier = Modifier
                .padding(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .height(150.dp)
                    .width(490.dp)
                    .background(
                        color = Color(0xffffffff),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .clickable {
                        visualAcuityViewModel.processAnswerSelected(
                            3,
                            handleWrong = {
                                progress = it
                            }
                        ) {
                            toResultScreen(
                                visualAcuityViewModel.getVisualAcuityTestResult()
                            )
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = StringProvider.getString(
                        R.string.visual_acuity_undefinable),
                    fontSize = 60.sp,
                )
            }
        }
    }
}