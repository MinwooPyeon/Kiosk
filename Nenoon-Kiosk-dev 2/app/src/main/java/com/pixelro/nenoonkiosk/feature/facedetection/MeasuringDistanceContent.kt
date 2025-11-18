package com.pixelro.nenoonkiosk.feature.facedetection

import android.graphics.PointF
import android.graphics.Rect
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
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.util.AnimationProvider
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.facedetection.components.FaceDetectionScreenContentWithPreview
import com.pixelro.nenoonkiosk.feature.facedetection.components.MeasuringDistanceDialog
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.ui.theme.Black
import com.pixelro.nenoonkiosk.ui.theme.Red
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import kotlin.math.roundToInt

/**
 * 상단 안내문 텍스트 결정
 */
@Composable
private fun getTopGuideText(
    isFaceDetected: Boolean,
    isLeftEye: Boolean,
    isLeftEyeCovered: Boolean,
    isRightEyeCovered: Boolean,
    isNenoonTextDetected: Boolean,
    isDistanceOK: Int
): String {
    return when (isFaceDetected) {
        true ->
            when (!isLeftEye) {
                true ->
                    when (isLeftEyeCovered && isNenoonTextDetected) {
                        true -> {
                            when (isDistanceOK) {
                                1 -> stringResource(R.string.measuring_distance_description1)
                                else -> stringResource(R.string.measuring_distance_description2)
                            }
                        }
                        false -> stringResource(R.string.measuring_distance_description3_cover_left)
                    }
                false ->
                    when (isRightEyeCovered && isNenoonTextDetected) {
                        true -> {
                            when (isDistanceOK) {
                                1 -> stringResource(R.string.measuring_distance_description1)
                                else -> stringResource(R.string.measuring_distance_description2)
                            }
                        }
                        false -> stringResource(R.string.measuring_distance_description4_cover_right)
                    }
            }
        false -> stringResource(R.string.measuring_distance_description5_face_center)
    }
}

/**
 * 경고 박스 텍스트 결정
 */
@Composable
private fun getWarningText(warningType: Int) = when (warningType) {
    0 -> buildAnnotatedString {
        append(stringResource(R.string.dialog_description3_distance_further))
    }
    1 -> buildAnnotatedString {
        append(stringResource(R.string.dialog_description2_announcement1))
        withStyle(
            style = SpanStyle(
                color = neNoon_blue,
                fontWeight = FontWeight.Bold,
            )
        ) {
            append(stringResource(R.string.dialog_description2_announcement2))
        }
        append(stringResource(R.string.dialog_description2_announcement3))
    }
    else -> buildAnnotatedString {
        append(stringResource(R.string.dialog_description4_closer))
    }
}

/**
 * 거리 안내 텍스트 결정 (하단 박스)
 */
@Composable
private fun getDistanceGuideText(selectedTestType: InspectionType) = when (selectedTestType) {
    InspectionType.ShortDistanceVisualAcuity ->
        buildAnnotatedString {
            append(stringResource(R.string.measuring_distance_description6_start))
            withStyle(
                style = SpanStyle(
                    color = neNoon_blue,
                    fontWeight = FontWeight.Bold,
                )
            ) {
                append(" 40~50cm")
            }
            append(stringResource(R.string.measuring_distance_description6_end))
        }
    else ->
        buildAnnotatedString {
            append(stringResource(R.string.measuring_distance_description6_start))
            withStyle(
                style = SpanStyle(
                    color = neNoon_blue,
                    fontWeight = FontWeight.Bold,
                )
            ) {
                append(" 25~35cm")
            }
            append(stringResource(R.string.measuring_distance_description6_end))
        }
}

/**
 * 거리에 따른 색상 결정
 */
private fun getDistanceColor(
    selectedTestType: InspectionType,
    screenToFaceDistance: Float
): Color = when (selectedTestType) {
    InspectionType.ShortDistanceVisualAcuity -> {
        when (screenToFaceDistance) {
            in 396.0..505.0 -> neNoon_blue
            else -> Red
        }
    }
    else -> {
        when (screenToFaceDistance) {
            in 246.0..355.0 -> neNoon_blue
            else -> Red
        }
    }
}

/**
 * 거리에 따른 Border 스타일 결정
 */
private fun getDistanceBorderStroke(
    selectedTestType: InspectionType,
    screenToFaceDistance: Float
): BorderStroke = when (selectedTestType) {
    InspectionType.ShortDistanceVisualAcuity -> {
        if (screenToFaceDistance > 505.0 || screenToFaceDistance < 396.0) {
            BorderStroke(3.dp, Red)
        } else {
            BorderStroke(1.dp, neNoon_blue)
        }
    }
    else -> {
        if (screenToFaceDistance > 355.0 || screenToFaceDistance < 246.0) {
            BorderStroke(3.dp, Red)
        } else {
            BorderStroke(1.dp, neNoon_blue)
        }
    }
}

@Composable
fun MeasuringDistanceScreen(
    measuringDistanceContentVisibleState: MutableTransitionState<Boolean>,
    toNextContent: () -> Unit,
    onStartButtonClick: () -> Unit,
    selectedTestType: InspectionType,
    isLeftEye: Boolean,
    faceDetectionTextSize: TextUnit,
    warningBoxTextSize: TextUnit,
    testStartTextSize: TextUnit,
    isFaceDetected: Boolean,
    isRightEyeCovered: Boolean,
    isLeftEyeCovered: Boolean,
    isDistanceOK: Int,
    isNenoonTextDetected: Boolean,
    screenToFaceDistance: Float,
    leftEyePosition: PointF,
    rightEyePosition: PointF,
    inputImageSizeX: Float,
    faceBoundingBox: Rect?,
    onUpdateIsDistanceOK: (Int) -> Unit,
) {
    val isPreviewMode = LocalInspectionMode.current

    // GlobalValue에 현재 화면 방향 저장
    GlobalValue.isLandscape = isLandscape()

    AnimatedVisibility(
        visibleState = measuringDistanceContentVisibleState,
        enter = AnimationProvider.enterTransition,
        exit = AnimationProvider.exitTransition,
    ) {
        /**
         * 0 = true - 거리 부족
         * 1 = true - 모든 조건 충족
         * 2 = true - 거리 초과
         * 5 = false
         */
        val isWarningShowing = remember { mutableIntStateOf(5) }

        val transition = rememberInfiniteTransition()
        val shiftVal by transition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec =
                infiniteRepeatable(
                    animation =
                        keyframes {
                            durationMillis = 700
                            delayMillis = 0
                        },
                    repeatMode = RepeatMode.Reverse,
                ),
        )
        var isDialogShowing by remember { mutableStateOf(true) }
        if (!isPreviewMode && isDialogShowing && isLeftEye) {
            MeasuringDistanceDialog(
                onDismissRequest = {
                    isDialogShowing = false
                },
            )
        }

        val isLandscapeMode = isLandscape()

        Box(
            modifier =
                Modifier
                    .fillMaxSize(),
        ) {
            /**
             * 카메라 preview 영역 (모든 요소 포함)
             */
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                // 카메라 프리뷰와 face_frame
            Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    FaceDetectionScreenContentWithPreview(isPreviewShowing = !isPreviewMode)

                    // face_frame만 표시
                    val faceFrameSize = remember(isLandscapeMode) {
                        if (isLandscapeMode) {
                            if (GlobalValue.screenWidthDp > 1500) 450.dp else 340.dp
                        } else {
                            if (GlobalValue.screenHeightDp > 900) 600.dp else 480.dp
                        }
                    }

                    Image(
                        modifier = Modifier
                            .width(faceFrameSize)
                            .height(faceFrameSize),
                        painter = painterResource(id = R.drawable.face_frame),
                        contentDescription = "",
                        colorFilter = ColorFilter.tint(neNoon_blue),
                    )

                    // 거리 안내 박스 - 오버레이로 분리
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = 100.dp),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        Box(
                            modifier =
                                Modifier
                                    .background(
                                        color = Black.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(50),
                                    )
                                    .border(
                                        border = getDistanceBorderStroke(selectedTestType, screenToFaceDistance),
                                        shape = RoundedCornerShape(50),
                                    )
                                    .padding(start = 20.dp, top = 8.dp, end = 20.dp, bottom = 12.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = getDistanceGuideText(selectedTestType),
                                fontSize = 32.sp,
                                color = White,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }

                    // 디버깅: 눈 위치 확인
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.TopStart,
                    ) {
                        Text(
                            text = "L: ${leftEyePosition.x.toInt()}, R: ${rightEyePosition.x.toInt()}, isLeft: $isLeftEye",
                            color = White,
                            fontSize = 20.sp,
                            modifier = Modifier.background(Black.copy(alpha = 0.7f)).padding(8.dp)
                        )
                    }

                    // 눈가리개 이미지 - 맨 위에 렌더링되도록 마지막에 배치
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center,
                    ) {
                        // 눈가리개 위치 계산 (원래 공식 사용)
                        val eyePosition = if (!isLeftEye) rightEyePosition else leftEyePosition

                        // ML Kit 좌표를 faceFrameSize에 맞춰 스케일
                        val scale = faceFrameSize.value / 1088f

                        // ML Kit 중앙 좌표 (1088 / 2 = 544)
                        val mlKitCenterX = 544f

                        // 중앙으로부터의 거리를 계산하여 offset 적용
                        val offsetX = ((eyePosition.x - mlKitCenterX) * scale).dp

                        // 눈가리개 크기
                        val validDistance = screenToFaceDistance.coerceAtLeast(30f)
                        val occluderWidth = (300 * 300 / validDistance).dp
                        val occluderHeight = (600 * 300 / validDistance).dp

                        // 항상 렌더링하되 alpha로 표시/숨김 제어
                        Image(
                            modifier =
                                Modifier
                                    .width(occluderWidth)
                                    .height(occluderHeight)
                                    .offset(
                                        x = offsetX,
                                    )
                                    .alpha(
                                        when {
                                            isPreviewMode -> 0.5f
                                            isFaceDetected -> shiftVal
                                            else -> 0f
                                        }
                                    ),
                            painter = painterResource(id = R.drawable.occluder),
                            contentDescription = null,
                        )
                    }
                }

                // 상단 안내문
                Box(
                    modifier =
                        Modifier
                            .align(Alignment.TopCenter)
                            .fillMaxWidth()
                            .height(80.dp)
                            .background(color = Black),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        text = getTopGuideText(
                            isFaceDetected, isLeftEye, isLeftEyeCovered,
                            isRightEyeCovered, isNenoonTextDetected, isDistanceOK
                        ),
                        color = White,
                        fontSize = faceDetectionTextSize,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center,
                    )

                    // 디버깅: 눈 위치 확인
                    Text(
                        text = "L: ${leftEyePosition.x.toInt()}, R: ${rightEyePosition.x.toInt()}, isLeft: $isLeftEye",
                        color = Color.Yellow,
                        fontSize = 16.sp,
                        modifier = Modifier.align(Alignment.BottomStart).padding(8.dp)
                    )
                }

                // 경고 박스
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    if (isWarningShowing.intValue in 0..4) {
                        Text(
                            modifier =
                                Modifier
                                    .padding(start = 40.dp, end = 40.dp)
                                    .border(
                                        border = BorderStroke(2.dp, Black),
                                        shape = RoundedCornerShape(8.dp),
                                    )
                                    .background(
                                        color = White,
                                        shape = RoundedCornerShape(8.dp),
                                    )
                                    .padding(20.dp)
                                    .fillMaxWidth(),
                            text = getWarningText(isWarningShowing.intValue),
                            fontSize = warningBoxTextSize,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                        )
                    }
                }
            }

            // 하단 안내문 (가로모드일 때만 표시) - 독립 레이어
            if (isLandscapeMode) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    contentAlignment = Alignment.BottomStart,
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth(0.3f)
                                .padding(start = 40.dp, bottom = 120.dp),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text =
                                stringResource(
                                    R.string.test_screen_current_distance,
                                ),
                            color = White,
                            fontSize = 24.sp,
                        )
                        Text(
                            color = getDistanceColor(selectedTestType, screenToFaceDistance),
                            text = "${(screenToFaceDistance / 10).roundToInt()}cm",
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }

            /**
             * 하단 영역 (거리 표시 + 검사 시작 버튼) - 오버레이
             */
            Box(
                modifier =
                    Modifier
                        .fillMaxSize(),
                contentAlignment = Alignment.BottomCenter,
            ) {
                /**
                 * 하단 안내문 (세로모드일 때만 표시)
                 */
                if (!isLandscape()) {
                    Row(
                        modifier =
                            Modifier
                                .padding(bottom = 120.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text =
                                stringResource(
                                    R.string.test_screen_current_distance,
                                ),
                            color = White,
                            fontSize = 24.sp,
                        )
                        Text(
                            color = getDistanceColor(selectedTestType, screenToFaceDistance),
                            text = "${(screenToFaceDistance / 10).roundToInt()}cm",
                            fontSize = 80.sp,
                            fontWeight = FontWeight.Bold,
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
                    isNenoonTextDetected &&
                    when (!isLeftEye) {
                        true -> isLeftEyeCovered
                        false -> isRightEyeCovered
                    }
                ) {
                    when (selectedTestType) {
                        InspectionType.ShortDistanceVisualAcuity -> {
                            when (screenToFaceDistance) {
                                in 0.1..396.0 -> {
                                    onUpdateIsDistanceOK(0)
                                }

                                in 505.0..995.0 -> {
                                    onUpdateIsDistanceOK(2)
                                }

                                else -> {
                                    onUpdateIsDistanceOK(1)
                                }
                            }
                        }

                        else -> {
                            when (screenToFaceDistance) {
                                in 0.1..246.0 -> {
                                    onUpdateIsDistanceOK(0)
                                }

                                in 355.0..995.0 -> {
                                    onUpdateIsDistanceOK(2)
                                }

                                else -> {
                                    onUpdateIsDistanceOK(1)
                                }
                            }
                        }
                    }
                } else {
                    onUpdateIsDistanceOK(4)
                }

                /**
                 * 검사 시작 버튼
                 */
                if (isDistanceOK in 0..2) {
                    Box(
                        modifier =
                            Modifier
                                .padding(
                                    start = 40.dp,
                                    end = 40.dp,
                                    bottom = (GlobalValue.navigationBarPadding + 20).dp,
                                )
                                .fillMaxWidth()
                                .height(80.dp)
                                .clip(
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .background(
//                            color = Color(0xff1d71e1),
                                    color =
                                        when (isDistanceOK) {
                                            1 -> neNoon_blue
                                            else -> Red
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                )
                                .clickable {
                                    onStartButtonClick()
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text =
                                when (isDistanceOK) {
                                    0 -> {
                                        stringResource(
                                            R.string.dialog_description3_distance_further,
                                        )
                                    }

                                    1 -> {
                                        stringResource(
                                            R.string.measuring_distance_start_button,
                                        )
                                    }

                                    else -> {
                                        stringResource(
                                            R.string.dialog_description4_closer,
                                        )
                                    }
                                },
                            fontSize = testStartTextSize,
                            color = White,
                            textAlign = TextAlign.Center,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}

@Preview(widthDp = 1920, heightDp = 600, showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MeasuringDistanceScreenHorizentalPreview() {
    val visibleState = remember { MutableTransitionState(true) }

    MeasuringDistanceScreen(
        measuringDistanceContentVisibleState = visibleState,
        toNextContent = {},
        onStartButtonClick = {},
        selectedTestType = InspectionType.Presbyopia,
        isLeftEye = false,
        faceDetectionTextSize = 35.sp,
        warningBoxTextSize = 50.sp,
        testStartTextSize = 40.sp,
        isFaceDetected = false,
        isRightEyeCovered = true,
        isLeftEyeCovered = false,
        isDistanceOK = 1,
        isNenoonTextDetected = true,
        screenToFaceDistance = 300f,
        leftEyePosition = PointF(100f, 100f),
        rightEyePosition = PointF(200f, 100f),
        inputImageSizeX = 1088f,
        faceBoundingBox = null,
        onUpdateIsDistanceOK = {},
    )
}

@Preview(widthDp = 800, heightDp = 1280, showBackground = true, backgroundColor = 0xFF000000)
@Composable
fun MeasuringDistanceScreenVerticalPreview() {
    val visibleState = remember { MutableTransitionState(true) }

    MeasuringDistanceScreen(
        measuringDistanceContentVisibleState = visibleState,
        toNextContent = {},
        onStartButtonClick = {},
        selectedTestType = InspectionType.Presbyopia,
        isLeftEye = false,
        faceDetectionTextSize = 35.sp,
        warningBoxTextSize = 50.sp,
        testStartTextSize = 40.sp,
        isFaceDetected = false,
        isRightEyeCovered = true,
        isLeftEyeCovered = false,
        isDistanceOK = 1,
        isNenoonTextDetected = true,
        screenToFaceDistance = 300f,
        leftEyePosition = PointF(100f, 100f),
        rightEyePosition = PointF(200f, 100f),
        inputImageSizeX = 1088f,
        faceBoundingBox = null,
        onUpdateIsDistanceOK = {},
    )
}
