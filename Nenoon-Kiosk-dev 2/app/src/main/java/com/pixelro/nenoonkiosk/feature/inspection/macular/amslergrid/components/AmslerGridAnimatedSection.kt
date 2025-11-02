package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
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
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.AnimationProvider
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetection
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridEvent
import com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.AmslerGridUiState
import kotlin.math.tan

@Composable
fun AmslerGridAnimatedSection(
    visibleState: MutableTransitionState<Boolean>,
    state: AmslerGridUiState,
    onEvent: (AmslerGridEvent) -> Unit,
    guideVideo: @Composable (() -> Unit)?,
    isFaceCenter: Boolean
) {

    val isPreview = LocalInspectionMode.current

    AnimatedVisibility(
        visibleState = visibleState,
        enter = AnimationProvider.enterTransition,
        exit = AnimationProvider.exitTransition,
    ) {
        if (!isPreview) {
            FaceDetection()
        }
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // 상단 안내문
            Text(
                modifier = Modifier
                    .padding(20.dp)
                    .height(160.dp),
                text = state.headerAnnotated,
                fontSize = when {
                    !state.isBlinkingDone -> 40.sp
                    isFaceCenter -> 32.sp
                    else -> 40.sp
                },
                color = Color(0xffffffff),
                fontWeight = FontWeight.Medium,
            )

            // 격자 박스
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .background(color = Color(0xffffffff), shape = RoundedCornerShape(12.dp))
                    .width(700.dp)
                    .height(700.dp),
            ) {
                if (state.shouldPlayGuideVideo && guideVideo != null) {
                    guideVideo()
                } else {
                    // 정적 격자 & 포인트 표시
                    Image(
                        modifier = Modifier
                            .padding(40.dp)
                            .width(600.dp)
                            .height(600.dp),
                        painter = painterResource(id = R.drawable.amsler_grid),
                        contentDescription = null,
                    )
                    Canvas(
                        modifier = Modifier
                            .padding(40.dp)
                            .width(600.dp)
                            .height(600.dp),
                    ) {
                        if (state.isDotShowing) {
                            drawCircle(
                                color = Color(0xff000000),
                                radius = 50f,
                                center = Offset(450f, 450f),
                            )
                        }
                        // 얼굴 각도 시각화 포인트(블루)
                        drawCircle(
                            color = Color(0xff0000ff),
                            radius = 20f,
                            center = Offset(
                                450f - (400f * tan(state.rotY * 0.0174533)).toFloat(),
                                450f - (400f * tan((state.rotX + 10) * 0.0174533)).toFloat(),
                            ),
                        )
                    }

                    // 터치 영역(9분할) 오버레이
                    if (state.isTestStarted) {
                        NineAreaOverlay(
                            areas = state.selectedAreas,
                            onPress = { onEvent(AmslerGridEvent.AreaPressed(it)) },
                        )
                    }
                }
            }

            // 하단 버튼/가이드
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                if (state.isTestStarted) {
                    PrimaryCompleteButton(onClick = { onEvent(AmslerGridEvent.CompletePressed) })
                } else if (isFaceCenter && state.isLeftEye) {
                    TopCenterGuide()
                }
            }
        }
    }
}