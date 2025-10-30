package com.pixelro.nenoonkiosk.feature.undeveloped.exerciseglasses.presbyopia_exercise

import androidx.annotation.OptIn
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.media3.common.util.UnstableApi
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.AnimationProvider
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetection
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetectionViewModel
import kotlin.math.roundToInt

@Composable
fun PresbyopiaExerciseContent(
    toResultScreen: (PresbyopiaExerciseResult) -> Unit,
    presbyopiaExerciseViewModel: PresbyopiaExerciseViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        presbyopiaExerciseViewModel.init()
    }
    val presbyopiaExerciseContentVisibleState = remember { MutableTransitionState(true) }
    presbyopiaExerciseContentVisibleState.targetState =
        presbyopiaExerciseViewModel.isMChartContentVisible.collectAsState().value

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    color = Color(0xff000000),
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier =
                Modifier
                    .fillMaxWidth(),
        ) {
            PresbyopiaexerciseContent(
                mChartContentVisibleState = presbyopiaExerciseContentVisibleState,
                toResultScreen = toResultScreen,
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun PresbyopiaexerciseContent(
    mChartContentVisibleState: MutableTransitionState<Boolean>,
    toResultScreen: (PresbyopiaExerciseResult) -> Unit,
    presbyopiaExerciseViewModel: PresbyopiaExerciseViewModel = hiltViewModel(),
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel(),
) {
    AnimatedVisibility(
        visibleState = mChartContentVisibleState,
        enter = AnimationProvider.enterTransition,
        exit = AnimationProvider.exitTransition,
    ) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val isTesting = presbyopiaExerciseViewModel.isTesting.collectAsState().value

        LaunchedEffect(true) {
            presbyopiaExerciseViewModel.updateIsTesting(true)
        }
        FaceDetection()
        val currentLevel = presbyopiaExerciseViewModel.currentLevel.collectAsState().value
        val imageId = presbyopiaExerciseViewModel.mChartImageId.collectAsState().value
        Column(
            modifier =
                Modifier
                    .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                modifier =
                    Modifier
                        .padding(top = 20.dp, bottom = 20.dp),
                //                text = "화면에 40cm 이내로 가깝게 오세요.\n줄무늬 패치를 글씨와 평행시켜\n 줄 사이로 읽어주세요.",
                text =
                    StringProvider.getString(R.string.within_40cm) +
                        StringProvider.getString(
                            R.string.read_between,
                        ),
                fontSize = 35.sp,
                color = Color(0xffffffff),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
            Text(
                modifier =
                    Modifier
                        .padding(top = 20.dp, bottom = 20.dp),
                text = StringProvider.getString(R.string.compare_and_test),
                fontSize = 30.sp,
                color = Color(0xffffffff),
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )
            Column(
                modifier =
                    Modifier
                        .width(700.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier =
                        Modifier
                            .background(
                                color = Color(0xffffffff),
                                shape = RoundedCornerShape(12.dp),
                            )
                            .fillMaxWidth()
                            .width(700.dp)
                            .height(420.dp),
                ) {
                    Image(
                        modifier =
                            Modifier
                                .padding(5.dp)
                                .fillMaxSize(),
                        painter = painterResource(id = imageId),
                        contentDescription = "",
                    )
                }
            }
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(top = 20.dp, bottom = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    verticalAlignment = Alignment.Top,
                    horizontalArrangement = Arrangement.Center,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .padding(
                                    start = 20.dp,
                                    end = 10.dp,
                                    bottom = 10.dp,
                                )
                                .width(340.dp)
                                .height(120.dp)
                                .clip(
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .background(
                                    color = Color(0xffffffff),
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .clickable {
                                    presbyopiaExerciseViewModel.updatePresbyopiaExerciseValue()
                                    toResultScreen(presbyopiaExerciseViewModel.getPresbyopiaExerciseResult())
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            modifier =
                                Modifier
                                    .padding(bottom = 4.dp),
                            text =
                                StringProvider.getString(
                                    R.string.not_visiable_test_complete,
                                ),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xffB90000),
                        )
                    }
                    Box(
                        modifier =
                            Modifier
                                .padding(
                                    start = 10.dp,
                                    end = 20.dp,
                                    bottom = 10.dp,
                                )
                                .width(340.dp)
                                .height(120.dp)
                                .clip(
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .background(
                                    color = Color(0xffffffff),
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .clickable {
                                    if (currentLevel >= 8) {
                                        presbyopiaExerciseViewModel.updatePresbyopiaExerciseValue()
                                        toResultScreen(presbyopiaExerciseViewModel.getPresbyopiaExerciseResult())
                                    } else {
                                        presbyopiaExerciseViewModel.updateCurrentLevel(currentLevel + 1)
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
                                    R.string.next_stage,
                                ),
                            fontSize = 30.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color(0xff000000),
                        )
                    }
                }
                Box(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        modifier =
                            Modifier
                                .padding(bottom = 4.dp),
                        text =
                            "※" +
                                StringProvider.getString(
                                    R.string.source,
                                ),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xff999999),
                    )
                }
                Column(
                    modifier =
                        Modifier
                            .padding(20.dp)
                            .fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        modifier =
                            Modifier
                                .padding(bottom = 10.dp),
                        text =
                            StringProvider.getString(
                                R.string.test_screen_current_distance,
                            ),
                        fontSize = 24.sp,
                        color = Color(0xffffffff),
                    )
                    Text(
                        modifier =
                            Modifier
                                .padding(bottom = 10.dp),
                        text = "${(faceDetectionViewModel.screenToFaceDistance.collectAsState().value / 10).roundToInt()}cm",
                        fontSize = 100.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xffffffff),
                    )
                }
            }
        }
    }
}
