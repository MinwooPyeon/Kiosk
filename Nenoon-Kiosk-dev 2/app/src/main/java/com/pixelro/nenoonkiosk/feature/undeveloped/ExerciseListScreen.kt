package com.pixelro.nenoonkiosk.feature.undeveloped

import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.keyframes
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.ui.GlassesExerciseSelectionButton
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import kotlinx.coroutines.delay

// 미개발

@OptIn(ExperimentalFoundationApi::class, ExperimentalAnimationApi::class)
@Composable
fun ExerciseListScreen(
    toTestScreen: (InspectionType) -> Unit,
    toIntroScreen: () -> Unit,
) {
    val pagerState =
        rememberPagerState(
            initialPage = Int.MAX_VALUE / 2,
            initialPageOffsetFraction = 0f,
            pageCount = { Int.MAX_VALUE },
        )
    val isDescriptionShowing = remember { mutableStateOf(true) }
    LaunchedEffect(true) {
//        exoPlayer.release()
        TTS.tts.stop()
        while (true) {
            delay(5000)
            pagerState.animateScrollToPage(
                page = (pagerState.currentPage + 1),
                animationSpec = tween(1000),
            )
            for (i in 1..3) {
                isDescriptionShowing.value = false
                delay(250)
                isDescriptionShowing.value = true
                delay(250)
            }
        }
    }
    var selectedTest by remember { mutableStateOf(InspectionType.None) }
    val transition = rememberInfiniteTransition()
    val shiftVal by transition.animateFloat(
        initialValue = 0f,
        targetValue = 20f,
        animationSpec =
            infiniteRepeatable(
                animation =
                    keyframes {
                        durationMillis = 2000
                    },
                repeatMode = RepeatMode.Reverse,
            ),
    )
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    color = Color(0xffffffff),
                ),
    ) {
        Box(
            modifier =
                Modifier
                    .padding(
                        start = 40.dp,
                        top = (GlobalValue.statusBarPadding + 20).dp,
                        end = 40.dp,
                        bottom = 20.dp,
                    )
                    .fillMaxWidth()
                    .height(40.dp),
        ) {
            Box(
                modifier =
                    Modifier
                        .fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text =
                        StringProvider.getString(
                            R.string.test_list_tittle,
                        ),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        Spacer(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(
                        color = Color(0xffebebeb),
                    ),
        )

        Column {
            Box(
                modifier =
                    Modifier
                        .padding(start = 40.dp, end = 40.dp, bottom = 20.dp)
                        .fillMaxWidth()
                        .height(170.dp),
                contentAlignment = Alignment.TopCenter,
            ) {
                if (isDescriptionShowing.value) {
                    Text(
                        modifier =
                            Modifier
                                .offset(x = 0.dp, y = shiftVal.dp),
                        text =
                            StringProvider.getString(
                                R.string.wear_the_glasses,
                            ),
                        fontSize = 52.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                }
            }

            Spacer(
                modifier =
                    Modifier
                        .height(40.dp),
            )

            GlassesExerciseSelectionButton(
                modifier = Modifier.height(400.dp),
                title =
                    StringProvider.getString(
                        R.string.presbyopia_glasses,
                    ),
                onClickMethod = {
                    toTestScreen(InspectionType.Presbyopia_Glasses)
                },
                painter = painterResource(id = R.drawable.presbyopiaglasses_1),
            )
            Spacer(
                modifier =
                    Modifier
                        .height(20.dp),
            )
            GlassesExerciseSelectionButton(
                modifier = Modifier.height(400.dp),
                title =
                    StringProvider.getString(
                        R.string.concentration_glasses,
                    ),
                onClickMethod = {
                    toTestScreen(InspectionType.Concentration_Glasses)
                },
                painter = painterResource(id = R.drawable.presbyopiaglasses_2),
            )
            Spacer(
                modifier =
                    Modifier
                        .height(20.dp),
            )
            Box(
                modifier =
                    Modifier
                        .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                Row(
                    modifier =
                        Modifier
                            .padding(
                                start = 40.dp,
                                bottom = (GlobalValue.navigationBarPadding + 40).dp,
                                top = 20.dp,
                            )
                            .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Image(
                        modifier =
                            Modifier
                                .padding(end = 20.dp)
                                .width(44.dp),
                        painter = painterResource(id = R.drawable.icon_warning),
                        contentDescription = "",
                    )
                    Text(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(end = 40.dp),
                        text =
                            buildAnnotatedString {
                                withStyle(
                                    style =
                                        SpanStyle(
                                            color = Color(0xff999999),
                                            fontSize = 16.sp,
                                        ),
                                ) {
                                    append(
                                        StringProvider.getString(
                                            R.string.test_list_screen_warning1,
                                        ),
                                    )
                                }
                                withStyle(
                                    style =
                                        SpanStyle(
                                            color = Color(0xffff0000),
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold,
                                        ),
                                ) {
                                    append(
                                        StringProvider.getString(
                                            R.string.test_list_screen_warning2,
                                        ),
                                    )
                                }
                                withStyle(
                                    style =
                                        SpanStyle(
                                            color = Color(0xff999999),
                                            fontSize = 16.sp,
                                        ),
                                ) {
                                    append(
                                        StringProvider.getString(
                                            R.string.test_list_screen_warning3,
                                        ),
                                    )
                                }
                            },
                    )
                }
            }
        }
    }
}
