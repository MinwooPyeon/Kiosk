package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process

/**
 * 시력 검사 화면 컴포넌트 (란돌트 C 검사)
 *
 * ## UI 흐름
 * 얼굴 인식 확인 → 란돌트 C 이미지 표시 → 사용자 답변 선택 → 정답 여부 판정
 *                                                    ↓
 *                                            ┌───────┴────────┐
 *                                         정답              오답
 *                                      다음 레벨로         진행도 감소
 *                                      (sightLevel++)      (progress--)
 *                                            ↓                ↓
 *                                    레벨 10 도달?        3회 오답?
 *                                    (최고 난이도)        (검사 종료)
 *                                            ↓                ↓
 *                                        결과 화면 ←──────────┘
 *
 * ## 검사 구조
 * - **ansNum (정답 방향)**: 2, 3, 4, 5, 6, 7 (6가지 방향)
 *   - 2: 2시 방향 (↗)
 *   - 3: 3시 방향 (→)
 *   - 4: 5시 방향 (↘)
 *   - 5: 7시 방향 (↙)
 *   - 6: 9시 방향 (←)
 *   - 7: 11시 방향 (↖)
 *
 * - **sightLevel (시력 난이도)**: 1~10 (단계가 넘어갈수록 작아짐)
 *
 * - **진행 로직**:
 *   - 정답: sightLevel 증가 (최대 10)
 *   - 오답: progress 감소, 3회 오답 시 검사 종료
 *
 * ## 답변 선택지
 * - 4개의 선택지: 3개의 랜덤 방향 + "잘 안보여요" 버튼
 * - 랜덤 방향은 randomList로 관리 (중복 없이 섞임)
 *
 * ## 얼굴 인식
 * - 얼굴이 감지되지 않거나 정면을 보지 않으면 경고 메시지 표시
 * - 카메라를 통해 실시간으로 사용자의 얼굴 방향 추적
 */

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.AnimationProvider
import com.pixelro.nenoonkiosk.core.util.AutoStartSTT
import com.pixelro.nenoonkiosk.core.util.STT
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.CantSeeButton
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.DirectionSelectionButton
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.VisualAcuityChartBox
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.VisualAcuityInspectionResult
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

private val STRING_VISUAL_ACUITY_DESCRIPTION = R.string.visual_acuity_description

/**
 * 시력 검사 화면 AnimatedVisibility 래퍼
 *
 * Box 안에서 AnimatedVisibility를 사용하기 위한 래퍼 함수
 *
 * @param visualAcuityInspectionCommonContentVisibleState 화면 표시 여부 상태 (AnimatedVisibility 전환 제어)
 * @param randomList 랜덤 방향 리스트 (3개)
 * @param ansNum 정답 방향 (2~7)
 * @param sightLevel 시력 난이도 (1~10)
 * @param isFaceDetected 얼굴 인식 여부
 * @param isFacingForward 정면 응시 여부
 * @param onAnswerSelected 답변 선택 시 호출되는 콜백
 * @param getInspectionResult 검사 결과를 가져오는 함수
 * @param toResultScreen 결과 화면으로 이동하는 콜백
 */
@Composable
fun VisualAcuityInspectionCommonContent(
    visualAcuityInspectionCommonContentVisibleState: MutableTransitionState<Boolean>,
    randomList: List<Int>,
    ansNum: Int,
    sightLevel: Int,
    isFaceDetected: Boolean,
    isFacingForward: Boolean,
    onAnswerSelected: (Int, (Float) -> Unit, () -> Unit) -> Unit,
    getInspectionResult: () -> VisualAcuityInspectionResult,
    toResultScreen: (VisualAcuityInspectionResult) -> Unit,
) {
    AnimatedVisibility(
        visibleState = visualAcuityInspectionCommonContentVisibleState,
        enter = AnimationProvider.enterTransition,
        exit = AnimationProvider.exitTransition,
    ) {
        VisualAcuityInspectionContent(
            randomList = randomList,
            ansNum = ansNum,
            sightLevel = sightLevel,
            isFaceDetected = isFaceDetected,
            isFacingForward = isFacingForward,
            onAnswerSelected = onAnswerSelected,
            getInspectionResult = getInspectionResult,
            toResultScreen = toResultScreen,
        )
    }
}

/**
 * 시력 검사 메인 컴포넌트 
 *
 * @param randomList 랜덤 방향 리스트 (3개)
 * @param ansNum 정답 방향 (2~7)
 * @param sightLevel 시력 난이도 (1~10)
 * @param isFaceDetected 얼굴 인식 여부
 * @param isFacingForward 정면 응시 여부
 * @param onAnswerSelected 답변 선택 시 호출되는 콜백
 * @param getInspectionResult 검사 결과를 가져오는 함수
 * @param toResultScreen 결과 화면으로 이동하는 콜백
 */
@Composable
fun VisualAcuityInspectionContent(
    randomList: List<Int>,
    ansNum: Int,
    sightLevel: Int,
    isFaceDetected: Boolean,
    isFacingForward: Boolean,
    onAnswerSelected: (Int, (Float) -> Unit, () -> Unit) -> Unit,
    getInspectionResult: () -> VisualAcuityInspectionResult,
    toResultScreen: (VisualAcuityInspectionResult) -> Unit,
) {
    var progress by remember { mutableStateOf(0.1f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )
    
    AutoStartSTT(
        onResult = { result ->
            handleVoiceAnswer(
                result = result,
                randomList = randomList,
                currentProgress = progress,
                onAnswerSelected = onAnswerSelected,
                updateProgress = { newProgress -> progress = newProgress },
                onComplete = { toResultScreen(getInspectionResult()) }
            )
        },
        enabled = true,
        onError = { error ->
        }
    )
    
    Column(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        /**
         * 시력표 박스
         */
        VisualAcuityChartBox(
            ansNum = ansNum,
            sightLevel = sightLevel,
            isFaceDetected = isFaceDetected,
            isFacingForward = isFacingForward,
        )
        Text(
            modifier =
                Modifier
                    .padding(top = 40.dp),
            text = stringResource(STRING_VISUAL_ACUITY_DESCRIPTION),
            fontSize = 40.sp,
            color = White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
        Spacer(
            modifier =
                Modifier
                    .height(20.dp),
        )
        //프로그레스 바
        LinearProgressIndicator(
            modifier =
                Modifier
                    .padding(bottom = 20.dp)
                    .width(490.dp)
                    .height(20.dp),
            progress = animatedProgress,
            color = neNoon_blue,
        )
        /**
         * 선택지
         */
        Row {
            DirectionSelectionButton(
                direction = randomList[0],
                onClick = {
                    onAnswerSelected(0, { progress = it }) {
                        toResultScreen(getInspectionResult())
                    }
                }
            )
            DirectionSelectionButton(
                direction = randomList[1],
                onClick = {
                    onAnswerSelected(1, { progress = it }) {
                        toResultScreen(getInspectionResult())
                    }
                }
            )
            DirectionSelectionButton(
                direction = randomList[2],
                onClick = {
                    onAnswerSelected(2, { progress = it }) {
                        toResultScreen(getInspectionResult())
                    }
                }
            )
        }
        //안보임 선택지
        CantSeeButton(
            onClick = {
                onAnswerSelected(3, { progress = it }) {
                    toResultScreen(getInspectionResult())
                }
            }
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PreviewVisualAcuityInspectionContent_Level1() {
    // sightLevel = 1 (가장 큰 크기)
    VisualAcuityInspectionContent(
        randomList = listOf(2, 5, 7),
        ansNum = 2,
        sightLevel = 1,
        isFaceDetected = true,
        isFacingForward = true,
        onAnswerSelected = { _, _, _ -> },
        getInspectionResult = { VisualAcuityInspectionResult(leftEye = 10, rightEye = 10) },
        toResultScreen = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PreviewVisualAcuityInspectionContent_Level5() {
    // sightLevel = 5 (중간 크기)
    VisualAcuityInspectionContent(
        randomList = listOf(3, 4, 6),
        ansNum = 3,
        sightLevel = 5,
        isFaceDetected = true,
        isFacingForward = true,
        onAnswerSelected = { _, _, _ -> },
        getInspectionResult = { VisualAcuityInspectionResult(leftEye = 5, rightEye = 5) },
        toResultScreen = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFF000000, device = "spec:width=800dp,height=1280dp,dpi=240")
@Composable
private fun PreviewVisualAcuityInspectionContent_Level10_WithWarning() {
    // sightLevel = 10 (가장 작은 크기) + 얼굴 인식 실패 경고
    VisualAcuityInspectionContent(
        randomList = listOf(2, 4, 7),
        ansNum = 7,
        sightLevel = 10,
        isFaceDetected = false,
        isFacingForward = false,
        onAnswerSelected = { _, _, _ -> },
        getInspectionResult = { VisualAcuityInspectionResult(leftEye = 10, rightEye = 10) },
        toResultScreen = {}
    )
}

// 음성 답변 처리
private fun handleVoiceAnswer(
    result: String,
    randomList: List<Int>,
    currentProgress: Float,
    onAnswerSelected: (Int, (Float) -> Unit, () -> Unit) -> Unit,
    updateProgress: (Float) -> Unit,
    onComplete: () -> Unit
) {
    val voiceText = result.lowercase().trim()
    val compactText = voiceText.replace("\\s+".toRegex(), "")
    
    // 숫자 인식
    val numberMap = mapOf(
        "이" to 2, "둘" to 2,
        "삼" to 3, "셋" to 3,
        "사" to 4, "넷" to 4,
        "오" to 5, "다섯" to 5,
        "육" to 6, "여섯" to 6,
        "칠" to 7, "일곱" to 7
    )
    
    var selectedIdx: Int? = null
    
    val normalized = voiceText.replace("\\s+".toRegex(), " ")
    val digitsInOrder = mutableListOf<Int>()
    for (ch in normalized) {
        val v = when (ch) {
            '2' -> 2
            '3' -> 3
            '4' -> 4
            '5' -> 5
            '6' -> 6
            '7' -> 7
            else -> null
        }
        if (v != null) digitsInOrder.add(v)
    }
    val matchValue = digitsInOrder.firstOrNull { randomList.contains(it) }
    if (matchValue != null) {
        val idx = randomList.indexOf(matchValue)
        if (idx != -1) {
            selectedIdx = idx
        }
    }
    
    if (selectedIdx == null) {
        val tokens = voiceText.split("\\s+".toRegex()).filter { it.isNotBlank() }
        tokens.firstOrNull { tok -> numberMap.containsKey(tok) }?.let { tok ->
            val numberValue = numberMap[tok]!!
            val idx = randomList.indexOf(numberValue)
            if (idx != -1) {
                selectedIdx = idx
            }
        }
    }

    if (selectedIdx == null) {
        val engMap = mapOf(
            "two" to 2, "three" to 3, "four" to 4, "five" to 5, "six" to 6, "seven" to 7
        )
        val tokens = voiceText.split("\\s+".toRegex()).filter { it.isNotBlank() }
        tokens.firstOrNull { tok -> engMap.containsKey(tok) }?.let { tok ->
            val v = engMap[tok]!!
            val idx = randomList.indexOf(v)
            if (idx != -1) {
                selectedIdx = idx
            }
        }
    }

    val dontKnowPhrases = listOf(
        "모르겠다", "모르겠어요", "모르겠습니다", "모름",
        "모르겠", "몰라", "몰라요",
        "안보여요", "안 보여요", "안보입니다", "안 보입니다", "안보임", "안 보임", "안보인다", "안 보인다"
    )

    val isUnknown = dontKnowPhrases.any { voiceText.contains(it) } ||
            listOf("모르겠", "모름", "몰라", "안보여", "안보임", "안보인다", "안보입니다").any { compactText.contains(it) }

    if (isUnknown) {
        selectedIdx = 3
    }
    
    if (selectedIdx != null && selectedIdx in 0..3) {
        onAnswerSelected(selectedIdx, updateProgress, onComplete)
    }
}
