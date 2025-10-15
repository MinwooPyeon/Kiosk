package com.pixelro.nenoonkiosk.test.presbyopia

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import androidx.media3.common.Player
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.TTS
import com.pixelro.nenoonkiosk.constants.NavConstants
import com.pixelro.nenoonkiosk.data.StringProvider
import com.pixelro.nenoonkiosk.facedetection.FaceDetection
import com.pixelro.nenoonkiosk.facedetection.FaceDetectionViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@androidx.annotation.OptIn(androidx.media3.common.util.UnstableApi::class)
@Composable
fun PresbyopiaTestContent(
    toResultScreen: (PresbyopiaTestResult) -> Unit,
    presbyopiaViewModel: PresbyopiaViewModel = hiltViewModel(),
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel()
) {

    val distance = faceDetectionViewModel.screenToFaceDistance.collectAsState().value
    val testState = presbyopiaViewModel.testState.collectAsState().value
    val tryCount = presbyopiaViewModel.tryCount.collectAsState().value
    val isNumberShowing = presbyopiaViewModel.isTextShowing.collectAsState().value
    val context = LocalContext.current
    val exoPlayer = presbyopiaViewModel.exoPlayer
    val isComingCloserTTSDone = presbyopiaViewModel.isComingCloserTTSDone.collectAsState().value
    val coroutineScope = rememberCoroutineScope()
    val isWarningShowing = remember { mutableStateOf(false) }

    val sharedPreferences = remember { context.getSharedPreferences(NavConstants.PREFERENCE_NAME, Context.MODE_PRIVATE) }
    val savedLanguage = sharedPreferences.getString("language", "defaultLanguage")
    val videoGuideText = if (savedLanguage == "ru") 30.sp else 40.sp

    /**
     * 얼굴 인식
     */
    FaceDetection()
    DisposableEffect(true) {
        onDispose {
            exoPlayer.release()
            TTS.clearOnDoneListener()
        }
    }
    presbyopiaViewModel.checkCondition(distance)

    var progress by remember { mutableStateOf(0.1f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec
    )
    /**
     * 검사 방법 안내 문구
     */
    Box(
        modifier = Modifier
            .padding(start = 40.dp, top = 10.dp, end = 40.dp)
            .fillMaxWidth()
            .height(160.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = when (testState) {
                PresbyopiaViewModel.TestState.Started,
                PresbyopiaViewModel.TestState.AdjustingDistance ->
                    buildAnnotatedString {
                        append(StringProvider.getString(
                            R.string.presbyopia_description1_1))
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xff1d71e1),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(" 40~50cm ")
                        }
                        append(StringProvider.getString(
                            R.string.presbyopia_description1_3))
                    }
                PresbyopiaViewModel.TestState.TextBlinking ->
                    buildAnnotatedString {
                    append(StringProvider.getString(
                        R.string.presbyopia_video_guide_1))
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xff1d71e1),
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(" " + StringProvider.getString(
                            R.string.presbyopia_video_guide_2))
                    }
                    append(StringProvider.getString(
                        R.string.presbyopia_video_guide_3))
                }
                PresbyopiaViewModel.TestState.ComingCloser ->
                    buildAnnotatedString {
                        append(StringProvider.getString(
                            R.string.presbyopia_description2_1))
                        withStyle(
                            style = SpanStyle(
                                color = Color(0xff1d71e1),
                                fontWeight = FontWeight.Bold
                            )
                        ) {
                            append(" " + StringProvider.getString(
                                R.string.presbyopia_description2_2,
                                
                            ))
                        }
                        append(" " + StringProvider.getString(
                            R.string.presbyopia_description2_3))
                    }
                PresbyopiaViewModel.TestState.NoPresbyopia -> {
                    when (tryCount) {
                        0 ->
                            buildAnnotatedString {
                                append(StringProvider.getString(
                                    R.string.presbyopia_under_25cm_description1,
                                    
                                ))
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xff1d71e1),
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(" " + StringProvider.getString(
                                        R.string.presbyopia_description2_2,
                                        
                                    ))
                                }
                                append(" " + StringProvider.getString(
                                    R.string.presbyopia_description2_3,
                                    
                                ))
                            }
                        1 ->
                            buildAnnotatedString {
                                append(StringProvider.getString(
                                    R.string.presbyopia_under_25cm_description2,
                                    
                                ))
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xff1d71e1),
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(" " + StringProvider.getString(
                                        R.string.presbyopia_description2_2,
                                        
                                    ))
                                }
                                append(" " + StringProvider.getString(
                                    R.string.presbyopia_description2_3,
                                    
                                ))
                            }
                        else ->
                            buildAnnotatedString {
                                append(StringProvider.getString(
                                    R.string.presbyopia_under_25cm_description3,
                                    
                                ))
                                withStyle(
                                    style = SpanStyle(
                                        color = Color(0xff1d71e1),
                                        fontWeight = FontWeight.Bold
                                    )
                                ) {
                                    append(" " + StringProvider.getString(
                                        R.string.presbyopia_description2_2,
                                        
                                    ))
                                }
                                append(" " + StringProvider.getString(
                                    R.string.presbyopia_description2_3,
                                    
                                ))
                            }
                    }
                }
            },
            color = Color(0xffffffff),
            fontSize = when (testState) {
                PresbyopiaViewModel.TestState.ComingCloser -> if (savedLanguage == "ru") 20.sp else 32.sp
                PresbyopiaViewModel.TestState.TextBlinking -> if (savedLanguage == "ru") 30.sp else 44.sp
                else -> if (savedLanguage == "ru") 30.sp else 48.sp
            },
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
    }

    /**
     * 진행도
     */
    LinearProgressIndicator(
        modifier = Modifier
            .padding(bottom = 20.dp)
            .width(600.dp)
            .height(20.dp),
        progress = animatedProgress,
        color = Color(0xff1d71e1),
    )

    /**
     * 가운데 큰 박스
     */
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .padding(top = 20.dp)
            .height(600.dp)
            .width(600.dp)
            .background(
                color = Color(0xffffffff),
                shape = RoundedCornerShape(8.dp)
            )
    ) {
        if (isWarningShowing.value) {
            Text(
                modifier = Modifier
                    .padding(start = 40.dp, end = 40.dp, bottom = 360.dp)
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
                        R.string.dialog_description2_announcement1))
                    withStyle(
                        style = SpanStyle(
                            color = Color(0xff1d71e1),
                            fontWeight = FontWeight.Bold
                        )
                    ) {
                        append(StringProvider.getString(
                            R.string.dialog_description2_announcement2))
                    }
                    append(StringProvider.getString(
                        R.string.dialog_description2_announcement3))
                },
                fontSize = 40.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }

        when (testState to TTS.tts.isSpeaking) {
            PresbyopiaViewModel.TestState.Started to true,
            PresbyopiaViewModel.TestState.Started to false -> {

                Text(
                    text =
                    when (tryCount) {
                        0 -> StringProvider.getString(
                            R.string.presbyopia_test_start1)//"조절력 검사를\n시작하겠습니다"
                        1 -> StringProvider.getString(
                            R.string.presbyopia_test_start2)//"두번째 검사를\n시작하겠습니다"
                        else -> StringProvider.getString(
                            R.string.presbyopia_test_start3)//"마지막 검사를\n시작하겠습니다"
                    },
                    fontWeight = FontWeight.Bold,
                    fontSize = 60.sp,
                    textAlign = TextAlign.Center
                )
            }
            PresbyopiaViewModel.TestState.AdjustingDistance to true,
            PresbyopiaViewModel.TestState.AdjustingDistance to false -> {
                /**
                 * 검사 시작에 영상 on
                 */
                when (tryCount) {
                    0 -> {
                        Surface(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            AndroidView(
                                modifier = Modifier
                                    .fillMaxSize(),
                                factory = {
                                    PlayerView(context).apply {
                                        exoPlayer.repeatMode = Player.REPEAT_MODE_ONE
                                        resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                        player = exoPlayer
                                        useController = false
                                        exoPlayer.setMediaItem(
                                            MediaItem.fromUri(
                                                RawResourceDataSource.buildRawResourceUri(R.raw.measuring_distance_video_2)
                                            )
                                        )
                                        exoPlayer.prepare()
                                        exoPlayer.play()
                                    }
                                }
                            )
                        }
                    }

                    1 -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .padding(40.dp)
                                    .height(70.dp)
                                    .width(70.dp),
                                painter = painterResource(id = R.drawable.img_test_presbyopia_2),
                                contentDescription = ""
                            )
                        }
                    }

                    else -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .padding(40.dp)
                                    .height(70.dp)
                                    .width(70.dp),
                                painter = painterResource(id = R.drawable.img_test_presbyopia_3),
                                contentDescription = ""
                            )
                        }
                    }
                }

            }

            PresbyopiaViewModel.TestState.ComingCloser to true -> {
                when (tryCount) {
                    0 -> {
                        if (isComingCloserTTSDone) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Image(
                                    modifier = Modifier
                                        .padding(40.dp)
                                        .height(50.dp)
                                        .width(50.dp),
                                    painter = painterResource(id = R.drawable.img_test_presbyopia_1),
                                    contentDescription = ""
                                )
                            }
                        } else {
                            Surface(
                                modifier = Modifier
                                    .fillMaxSize()
                            ) {
                                AndroidView(
                                    modifier = Modifier
                                        .fillMaxSize(),
                                    factory = {
                                        PlayerView(context).apply {
                                            useController = false
                                            exoPlayer.repeatMode = Player.REPEAT_MODE_OFF
                                            resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                                            player = exoPlayer
                                            exoPlayer.setMediaItem(
                                                MediaItem.fromUri(
                                                    RawResourceDataSource.buildRawResourceUri(R.raw.presbyopia_video_2_new)
                                                )
                                            )
                                            exoPlayer.prepare()
                                            exoPlayer.play()
                                        }
                                    }
                                )
                            }
                        }
                    }

                    1 -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .padding(40.dp)
                                    .height(70.dp)
                                    .width(70.dp),
                                painter = painterResource(id = R.drawable.img_test_presbyopia_2),
                                contentDescription = ""
                            )
                        }
                    }

                    else -> {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                modifier = Modifier
                                    .padding(40.dp)
                                    .height(70.dp)
                                    .width(70.dp),
                                painter = painterResource(id = R.drawable.img_test_presbyopia_3),
                                contentDescription = ""
                            )
                        }
                    }
                }
            }

            else -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    when (testState) {
                        /**
                         * 25cm 이하 일 때
                         */
                        PresbyopiaViewModel.TestState.NoPresbyopia -> {
                            Text(
                                text = when (tryCount) {
                                    0 ->
                                        buildAnnotatedString {
                                            append(StringProvider.getString(
                                                R.string.presbyopia_under_25cm_description1,
                                                
                                            ))
                                            withStyle(
                                                style = SpanStyle(
                                                    color = Color(0xff1d71e1),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            ) {
                                                append(" " + StringProvider.getString(
                                                    R.string.presbyopia_description2_2,
                                                    
                                                ))
                                            }
                                            append(" " + StringProvider.getString(
                                                R.string.presbyopia_description2_3,
                                                
                                            ))
                                        }//"첫 번째 측정에서\n노안이 발견되지 않았습니다\n아래의 다음을 눌러주세요"
                                    1 ->
                                        buildAnnotatedString {
                                            append(StringProvider.getString(
                                                R.string.presbyopia_under_25cm_description2,
                                                
                                            ))
                                            withStyle(
                                                style = SpanStyle(
                                                    color = Color(0xff1d71e1),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            ) {
                                                append(" " + StringProvider.getString(
                                                    R.string.presbyopia_description2_2,
                                                    
                                                ))
                                            }
                                            append(" " + StringProvider.getString(
                                                R.string.presbyopia_description2_3,
                                                
                                            ))
                                        }//"두 번째 측정에서\n노안이 발견되지 않았습니다\n아래의 다음을 눌러주세요"
                                    else ->
                                        buildAnnotatedString {
                                            append(StringProvider.getString(
                                                R.string.presbyopia_under_25cm_description3,
                                                
                                            ))
                                            withStyle(
                                                style = SpanStyle(
                                                    color = Color(0xff1d71e1),
                                                    fontWeight = FontWeight.Bold
                                                )
                                            ) {
                                                append(" " + StringProvider.getString(
                                                    R.string.presbyopia_description2_2,
                                                    
                                                ))
                                            }
                                            append(" " + StringProvider.getString(
                                                R.string.presbyopia_description2_3,
                                                
                                            ))
                                        }//"마지막 측정에서\n노안이 발견되지 않았습니다\n아래의 다음을 눌러주세요"
                                },
                                fontSize = 44.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                        }

                        /**
                         * 영상 전의 안내문
                         */
                        PresbyopiaViewModel.TestState.TextBlinking -> {
                            Text(
                                text =
                                buildAnnotatedString {
                                    append(StringProvider.getString(
                                        R.string.presbyopia_video_guide_1,
                                        
                                    ))
                                    withStyle(
                                        style = SpanStyle(
                                            color = Color(0xff1d71e1),
                                            fontWeight = FontWeight.Bold
                                        )
                                    ) {
                                        append(" " + StringProvider.getString(
                                            R.string.presbyopia_video_guide_2,
                                            
                                        ))
                                    }
                                    append(StringProvider.getString(
                                        R.string.presbyopia_video_guide_3,
                                        
                                    ))
                                },
                                fontWeight = FontWeight.Bold,
                                fontSize = 60.sp,
                                textAlign = TextAlign.Center
                            )
                        }

                        else -> {
                            Image(
                                modifier = when (tryCount) {
                                    0 -> {
                                        Modifier
                                            .padding(40.dp)
                                            .height(50.dp)
                                            .width(50.dp)
                                    }
                                    else -> {
                                        Modifier
                                            .padding(40.dp)
                                            .height(70.dp)
                                            .width(70.dp)
                                    }
                                },
                                painter = when (tryCount) {
                                    0 -> painterResource(id = R.drawable.img_test_presbyopia_1)
                                    1 -> painterResource(id = R.drawable.img_test_presbyopia_2)
                                    else -> painterResource(id = R.drawable.img_test_presbyopia_3)
                                },
                                contentDescription = ""
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * 하단 거리 표시
     */
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        when (testState) {
            PresbyopiaViewModel.TestState.Started -> {}
            PresbyopiaViewModel.TestState.AdjustingDistance -> {
                Text(
                    modifier = Modifier
                        .padding(bottom = 304.dp),
                    text = StringProvider.getString(
                        R.string.test_screen_current_distance),
                    fontSize = 24.sp,
                    color = Color(0xffffffff)
                )
                Text(
                    modifier = Modifier
                        .padding(bottom = 120.dp),
                    text = "${(faceDetectionViewModel.screenToFaceDistance.collectAsState().value / 10).roundToInt()}cm",
                    fontSize = 140.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xffffffff)
                )
            }

            PresbyopiaViewModel.TestState.ComingCloser -> {
                if (!isComingCloserTTSDone && tryCount == 0) {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 304.dp),
                        text =
                        buildAnnotatedString {
                            append(StringProvider.getString(
                                R.string.presbyopia_video_guide_above_1,
                                
                            ))
                            withStyle(
                                style = SpanStyle(
                                    color = Color(0xff1d71e1),
                                    fontWeight = FontWeight.Bold
                                )
                            ) {
                                append(" " + StringProvider.getString(
                                    R.string.presbyopia_video_guide_above_2,
                                    
                                ))
                            }
                            append(StringProvider.getString(
                                R.string.presbyopia_video_guide_above_3,
                                
                            ))
                        },
                        color = Color(0xFFFFFFFF),
                        fontSize = videoGuideText
                    )
                } else {
                    Text(
                        modifier = Modifier
                            .padding(bottom = 304.dp),
                        text = StringProvider.getString(
                            R.string.test_screen_current_distance),
                        fontSize = 24.sp,
                        color = Color(0xffffffff)
                    )
                    Text(
                        modifier = Modifier
                            .padding(bottom = 120.dp),
                        text = "${(faceDetectionViewModel.screenToFaceDistance.collectAsState().value / 10).roundToInt()}cm",
                        fontSize = 140.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xffffffff)
                    )
                }

            }

            else -> {}
        }

        /**
         * 다음 버튼
         */
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.BottomCenter
        ) {
            if (((testState == PresbyopiaViewModel.TestState.ComingCloser && ((tryCount == 0 && !TTS.tts.isSpeaking)|| tryCount == 1 || tryCount == 2)) || testState == PresbyopiaViewModel.TestState.NoPresbyopia)) {
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
                            RoundedCornerShape(8.dp)
                        )
                        .background(
                            color = Color(0xff1d71e1),
                            shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)
                        )
                        .clickable {
                            if (!TTS.tts.isSpeaking) {
                                presbyopiaViewModel.moveToNextStep(
                                    distance,
                                    handleProgress = {
                                        progress = it
                                    }
                                ) {
                                    toResultScreen(
                                        presbyopiaViewModel.getPresbyopiaTestResult()
                                    )
                                }
                            } else {
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
                            }
                        },
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = StringProvider.getString(R.string.next, ),
                        fontSize = 40.sp,
                        color = Color(0xffffffff),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}