package com.pixelro.nenoonkiosk.feature.undeveloped.exerciseglasses.concentration_exercise

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
fun ConcentrationExerciseContent(
    toResultScreen: (ConcentrationExerciseResult) -> Unit,
    concentrationExerciseViewModel: ConcentrationExerciseViewModel = hiltViewModel(),
) {
    LaunchedEffect(true) {
        concentrationExerciseViewModel.init()
    }
    val concentrationExerciseContentVisibleState = remember { MutableTransitionState(true) }
    concentrationExerciseContentVisibleState.targetState = concentrationExerciseViewModel.isMChartContentVisible.collectAsState().value

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
            ConcentrationexerciseContent(
                mChartContentVisibleState = concentrationExerciseContentVisibleState,
                toResultScreen = toResultScreen,
            )
        }
    }
}

@OptIn(UnstableApi::class)
@Composable
fun ConcentrationexerciseContent(
    mChartContentVisibleState: MutableTransitionState<Boolean>,
    toResultScreen: (ConcentrationExerciseResult) -> Unit,
    concentrationExerciseViewModel: ConcentrationExerciseViewModel = hiltViewModel(),
    faceDetectionViewModel: FaceDetectionViewModel = hiltViewModel(),
) {
    AnimatedVisibility(
        visibleState = mChartContentVisibleState,
        enter = AnimationProvider.enterTransition,
        exit = AnimationProvider.exitTransition,
    ) {
        val context = LocalContext.current
        val coroutineScope = rememberCoroutineScope()
        val isTesting = concentrationExerciseViewModel.isTesting.collectAsState().value

        LaunchedEffect(true) {
            concentrationExerciseViewModel.updateIsTesting(true)
        }
        FaceDetection()
        val currentLevel = concentrationExerciseViewModel.currentLevel.collectAsState().value
        val imageId = concentrationExerciseViewModel.mChartImageId.collectAsState().value
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
                //                text = "화면에 40cm 이내로 가깝게 오세요.\n사각 프레임 안으로 천천히 읽어보세요.\n글씨가 더욱 선명해지며 몰입하는\n느낌을 경험할 수 있습니다.",
                text =
                    StringProvider.getString(R.string.within_40cm) +
                        StringProvider.getString(
                            R.string.square_frame,
                        ) + StringProvider.getString(R.string.feel_more_immersive),
                fontSize = 35.sp,
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
                    Text(
                        modifier =
                            Modifier
                                .padding(bottom = 340.dp),
                        text =
                            StringProvider.getString(
                                R.string.concentration_test_chart,
                            ),
                        fontSize = 30.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xff000000),
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
                                    concentrationExerciseViewModel.updateConcentrationExerciseValue()
                                    toResultScreen(concentrationExerciseViewModel.getConcentrationExerciseResult())
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            modifier =
                                Modifier
                                    .padding(bottom = 4.dp),
                            text =
                                StringProvider.getString(
                                    R.string.test_over,
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
                                    if (currentLevel == 0) {
                                        concentrationExerciseViewModel.updateCurrentLevel(
                                            currentLevel + 1,
                                        )
                                    } else {
                                        concentrationExerciseViewModel.updateCurrentLevel(
                                            currentLevel - 1,
                                        )
                                    }
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            modifier =
                                Modifier
                                    .padding(bottom = 4.dp),
                            text =
                                if (currentLevel == 0) {
                                    StringProvider.getString(
                                        R.string.chart_en,
                                    )
                                } else {
                                    StringProvider.getString(
                                        R.string.chart_ko,
                                    )
                                },
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
