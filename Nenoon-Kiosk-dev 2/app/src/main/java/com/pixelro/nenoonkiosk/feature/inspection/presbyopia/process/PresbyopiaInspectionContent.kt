package com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process

import android.content.Context
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.media3.exoplayer.ExoPlayer
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.BottomWarningButton
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.components.DistanceDisplay
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.components.InspectionContentBox
import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process.components.InspectionDescriptionBox
import com.pixelro.nenoonkiosk.core.ui.StyledAnnotatedText
import com.pixelro.nenoonkiosk.core.ui.TextSegment
import com.pixelro.nenoonkiosk.feature.inspection.components.WarningOverlay
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.bodyTextStyle
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

/**
 * 노안 검사 화면 컴포넌트
 *
 * ## UI 상태 (PresbyopiaInspectionUiState)
 * 1. Started: 검사 시작 (카운트다운)
 * 2. AdjustingDistance: 거리 조정 중 (40~50cm 맞추기)
 * 3. TextBlinking: 텍스트 깜빡임 안내
 * 4. ComingCloser: 가까이 다가오기 (거리 측정)
 * 5. NoPresbyopia: 노안 없음 (25cm 미만에서 텍스트 읽음, 정상 시력)
 *
 * ## 검사 흐름
 * Started → AdjustingDistance → TextBlinking → ComingCloser
 *                                                    ↓ (거리 측정)
 *                                            ┌───────┴────────┐
 *                                       25cm 이상          25cm 미만
 *                                      (노안 의심)        (정상 시력)
 *                                            ↓                ↓
 *                                       결과 화면      NoPresbyopia
 *                                                            ↓ (다음 버튼)
 *                                                        재시도 (최대 3회)
 * Note: tryCount는 0, 1, 2로 총 3회 시도. 3번 모두 25cm 미만이면 노안 없음으로 판정
 *
 * @param context Android Context
 * @param distance 얼굴 인식을 통해 측정된 현재 거리 (밀리미터 단위, 10으로 나누면 센티미터)
 * @param uiState 현재 UI 상태 (Started, AdjustingDistance, TextBlinking, ComingCloser, NoPresbyopia)
 * @param tryCount 검사 시도 횟수 (0, 1, 2로 총 3회 시도 가능)
 * @param isComingCloserTTSDone ComingCloser 상태에서 TTS 안내가 완료되었는지 여부
 * @param exoPlayer 안내 비디오 재생을 위한 ExoPlayer 인스턴스 (null이면 플레이스홀더 표시)
 * @param savedLanguage 사용자가 선택한 언어 설정 (예: "ko", "ru" 등)
 * @param videoGuideText 비디오 안내 텍스트의 폰트 크기
 * @param progress 검사 진행도 (0.0 ~ 1.0 사이의 값, LinearProgressIndicator에 표시)
 * @param isWarningShowing 경고 오버레이 표시 여부
 * @param onWarningShow 경고 오버레이 표시 상태 변경 콜백
 * @param onNextStep 다음 단계로 진행하는 콜백 (버튼 클릭 시 호출)
 * @param isTTSSpeaking TTS 음성이 현재 재생 중인지 여부 (기본값: false)
 */
@Composable
fun PresbyopiaInspectionContent(
    context: Context,
    distance: Float,
    uiState: PresbyopiaInspectionUiState,
    tryCount: Int,
    isComingCloserTTSDone: Boolean,
    exoPlayer: ExoPlayer?,
    savedLanguage: String?,
    videoGuideText: TextUnit,
progress: Float,
    isWarningShowing: Boolean,
    onWarningShow: (Boolean) -> Unit,
    onNextStep: () -> Unit,
    isTTSSpeaking: Boolean = false,
    isFaceDetected: Boolean = true,
) {
    // 리소스
    val description1_1 = stringResource(R.string.presbyopia_description1_1)
    val description1_3 = stringResource(R.string.presbyopia_description1_3)
    val videoGuide1 = stringResource(R.string.presbyopia_video_guide_1)
    val videoGuide2 = stringResource(R.string.presbyopia_video_guide_2)
    val videoGuide3 = stringResource(R.string.presbyopia_video_guide_3)
    val description2_1 = stringResource(R.string.presbyopia_description2_1)
    val description2_2 = stringResource(R.string.presbyopia_description2_2)
    val description2_3 = stringResource(R.string.presbyopia_description2_3)
    val under25cmDesc1 = stringResource(R.string.presbyopia_under_25cm_description1)
    val under25cmDesc2 = stringResource(R.string.presbyopia_under_25cm_description2)
    val under25cmDesc3 = stringResource(R.string.presbyopia_under_25cm_description3)
    val dialogAnnouncement1 = stringResource(R.string.dialog_description2_announcement1)
    val dialogAnnouncement2 = stringResource(R.string.dialog_description2_announcement2)
    val dialogAnnouncement3 = stringResource(R.string.dialog_description2_announcement3)
    val testStart1 = stringResource(R.string.presbyopia_test_start1)
    val testStart2 = stringResource(R.string.presbyopia_test_start2)
    val testStart3 = stringResource(R.string.presbyopia_test_start3)
    val videoGuideAbove1 = stringResource(R.string.presbyopia_video_guide_above_1)
    val videoGuideAbove2 = stringResource(R.string.presbyopia_video_guide_above_2)
    val videoGuideAbove3 = stringResource(R.string.presbyopia_video_guide_above_3)
    val nextButton = stringResource(R.string.next)

    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        /**
         * 검사 방법 안내 문구
         */
        InspectionDescriptionBox(
            uiState = uiState,
            tryCount = tryCount,
            savedLanguage = savedLanguage,
            description1_1 = description1_1,
            description1_3 = description1_3,
            videoGuide1 = videoGuide1,
            videoGuide2 = videoGuide2,
            videoGuide3 = videoGuide3,
            description2_1 = description2_1,
            description2_2 = description2_2,
            description2_3 = description2_3,
            under25cmDesc = when (tryCount) {
                0 -> under25cmDesc1
                1 -> under25cmDesc2
                else -> under25cmDesc3
            },
        )

        /**
         * 진행도 프로그레스 바
         */
        LinearProgressIndicator(
            modifier =
                Modifier
                    .padding(bottom = 20.dp)
                    .fillMaxWidth()
                    .height(20.dp),
            progress = animatedProgress,
            color = neNoon_blue,
        )

        /**
         * 가운데 큰 박스
         */
        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .padding(top = 20.dp)
                    .height(500.dp)
                    .width(600.dp)
                    .background(
                        color = White,
                        shape = RoundedCornerShape(8.dp),
                    ),
        ) {
            InspectionContentBox(
                context = context,
                uiState = uiState,
                tryCount = tryCount,
                isTTSSpeaking = isTTSSpeaking,
                isComingCloserTTSDone = isComingCloserTTSDone,
                exoPlayer = exoPlayer,
                testStartText = when (tryCount) {
                    0 -> testStart1
                    1 -> testStart2
                    else -> testStart3
                },
                videoGuide1 = videoGuide1,
                videoGuide2 = videoGuide2,
                videoGuide3 = videoGuide3,
                under25cmDesc = when (tryCount) {
                    0 -> under25cmDesc1
                    1 -> under25cmDesc2
                    else -> under25cmDesc3
                },
                description2_2 = description2_2,
                description2_3 = description2_3,
            )
        }

        /**
         * 하단 거리 표시
         */
        when (uiState) {
            PresbyopiaInspectionUiState.Started -> {}

            PresbyopiaInspectionUiState.AdjustingDistance -> {
                DistanceDisplay(
                    distance = distance,
                    modifier = Modifier.padding(top = 40.dp),
                )
            }

            PresbyopiaInspectionUiState.ComingCloser -> {
                if (!isComingCloserTTSDone && tryCount == 0) {
                    StyledAnnotatedText(
                        modifier = Modifier.padding(top = 40.dp),
                        segments = listOf(
                            TextSegment.fromStyle(
                                text = videoGuideAbove1,
                                color = White,
                                fontSize = videoGuideText,
                                baseStyle = bodyTextStyle,
                            ),
                            TextSegment.create(
                                text = " $videoGuideAbove2",
                                color = neNoon_blue,
                                fontSize = videoGuideText,
                                fontWeight = FontWeight.Bold,
                                fontFamily = bodyTextStyle.fontFamily,
                            ),
                            TextSegment.fromStyle(
                                text = videoGuideAbove3,
                                color = White,
                                fontSize = videoGuideText,
                                baseStyle = bodyTextStyle,
                            ),
                        ),
                        textAlign = TextAlign.Center,
                    )
                } else {
                    DistanceDisplay(
                        distance = distance,
                        modifier = Modifier.padding(top = 40.dp),
                    )
                }
            }

            else -> {}
        }

        //다음 버튼
        // 1. 가까이 다가오는 검사 (총 세 단계) 중 첫 번째 시도일 때는 TTS가 끝나야 버튼이 보임
        // 2. 두 번째, 세 번째 시도에서는 바로 버튼이 보임
        // 3. 25cm 이하로 측정되어 노안이 없다고 판단된 경우에도 버튼이 보임
        if (((uiState == PresbyopiaInspectionUiState.ComingCloser && ((tryCount == 0 && !isTTSSpeaking) || tryCount == 1 || tryCount == 2)) || uiState == PresbyopiaInspectionUiState.NoPresbyopia)) {
            BottomWarningButton(
                onClick = onNextStep,
                text = nextButton,
                enabled = !isTTSSpeaking,
                showWarningWhenDisabled = true,
                onWarningShow = onWarningShow,
            )
        }
    }

    // 전체 화면 경고 오버레이 (중앙 배치)
    if (isWarningShowing) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                !isFaceDetected -> {
                    // 얼굴 인식 실패 경고 (최우선)
                    WarningOverlay(
                        text1 = "화면에 ",
                        text2 = "얼굴",
                        text3 = "을 비춰주세요",
                    )
                }
                isTTSSpeaking -> {
                    // TTS 재생 중 경고 (버튼 클릭 시에만)
                    WarningOverlay(
                        text1 = dialogAnnouncement1,
                        text2 = dialogAnnouncement2,
                        text3 = dialogAnnouncement3,
                    )
                }
                else -> {
                    // 거리 측정 중 (기본값)
                    WarningOverlay(
                        text1 = "거리를 ",
                        text2 = "측정",
                        text3 = "하고 있습니다",
                    )
                }
            }
        }
    }
  }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PreviewPresbyopiaInspectionContentStarted() {
    val context = LocalContext.current

    PresbyopiaInspectionContent(
        context = context,
        distance = 450f,
        uiState = PresbyopiaInspectionUiState.Started,
        tryCount = 0,
        isComingCloserTTSDone = false,
        exoPlayer = null,
        savedLanguage = "ko",
        videoGuideText = 40.sp,
        progress = 0.3f,
        isWarningShowing = false,
        onWarningShow = {},
        onNextStep = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PreviewPresbyopiaInspectionContentAdjustingDistance() {
    val context = LocalContext.current

    PresbyopiaInspectionContent(
        context = context,
        distance = 450f,
        uiState = PresbyopiaInspectionUiState.AdjustingDistance,
        tryCount = 0,
        isComingCloserTTSDone = false,
        exoPlayer = null,
        savedLanguage = "ko",
        videoGuideText = 40.sp,
        progress = 0.4f,
        isWarningShowing = false,
        onWarningShow = {},
        onNextStep = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PreviewPresbyopiaInspectionContentComingCloser() {
    val context = LocalContext.current

    PresbyopiaInspectionContent(
        context = context,
        distance = 450f,
        uiState = PresbyopiaInspectionUiState.ComingCloser,
        tryCount = 2,
        isComingCloserTTSDone = true,
        exoPlayer = null,
        savedLanguage = "ko",
        videoGuideText = 40.sp,
        progress = 0.5f,
        isWarningShowing = false,
        onWarningShow = {},
        onNextStep = {}
    )
}
