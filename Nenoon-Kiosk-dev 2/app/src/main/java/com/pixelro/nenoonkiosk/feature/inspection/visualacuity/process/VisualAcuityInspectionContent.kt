package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
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
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.CantSeeButton
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.DirectionSelectionButton
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.VisualAcuityChartBox
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.VisualAcuityInspectionResult
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

private val STRING_VISUAL_ACUITY_DESCRIPTION = R.string.visual_acuity_description

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
        onError = { }
    )

    val isLandscape = isLandscape()

    if (isLandscape) {
        LandscapeVisualAcuityContent(
            ansNum = ansNum,
            sightLevel = sightLevel,
            isFaceDetected = isFaceDetected,
            isFacingForward = isFacingForward,
            animatedProgress = animatedProgress,
            randomList = randomList,
            onAnswerSelected = onAnswerSelected,
            getInspectionResult = getInspectionResult,
            toResultScreen = toResultScreen,
            updateProgress = { progress = it }
        )
    } else {
        PortraitVisualAcuityContent(
            ansNum = ansNum,
            sightLevel = sightLevel,
            isFaceDetected = isFaceDetected,
            isFacingForward = isFacingForward,
            animatedProgress = animatedProgress,
            randomList = randomList,
            onAnswerSelected = onAnswerSelected,
            getInspectionResult = getInspectionResult,
            toResultScreen = toResultScreen,
            updateProgress = { progress = it }
        )
    }
}

@Composable
private fun PortraitVisualAcuityContent(
    ansNum: Int,
    sightLevel: Int,
    isFaceDetected: Boolean,
    isFacingForward: Boolean,
    animatedProgress: Float,
    randomList: List<Int>,
    onAnswerSelected: (Int, (Float) -> Unit, () -> Unit) -> Unit,
    getInspectionResult: () -> VisualAcuityInspectionResult,
    toResultScreen: (VisualAcuityInspectionResult) -> Unit,
    updateProgress: (Float) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VisualAcuityChartBox(
            ansNum = ansNum,
            sightLevel = sightLevel,
            isFaceDetected = isFaceDetected,
            isFacingForward = isFacingForward,
        )

        Text(
            modifier = Modifier.padding(top = 40.dp),
            text = stringResource(STRING_VISUAL_ACUITY_DESCRIPTION),
            fontSize = 40.sp,
            color = White,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(20.dp))

        LinearProgressIndicator(
            modifier = Modifier
                .padding(bottom = 20.dp)
                .width(490.dp)
                .height(20.dp),
            progress = animatedProgress,
            color = neNoon_blue,
        )

        Row {
            DirectionSelectionButton(
                direction = randomList[0],
                onClick = {
                    onAnswerSelected(0, updateProgress) {
                        toResultScreen(getInspectionResult())
                    }
                }
            )
            DirectionSelectionButton(
                direction = randomList[1],
                onClick = {
                    onAnswerSelected(1, updateProgress) {
                        toResultScreen(getInspectionResult())
                    }
                }
            )
            DirectionSelectionButton(
                direction = randomList[2],
                onClick = {
                    onAnswerSelected(2, updateProgress) {
                        toResultScreen(getInspectionResult())
                    }
                }
            )
        }

        CantSeeButton(
            onClick = {
                onAnswerSelected(3, updateProgress) {
                    toResultScreen(getInspectionResult())
                }
            }
        )
    }
}

@Composable
private fun LandscapeVisualAcuityContent(
    ansNum: Int,
    sightLevel: Int,
    isFaceDetected: Boolean,
    isFacingForward: Boolean,
    animatedProgress: Float,
    randomList: List<Int>,
    onAnswerSelected: (Int, (Float) -> Unit, () -> Unit) -> Unit,
    getInspectionResult: () -> VisualAcuityInspectionResult,
    toResultScreen: (VisualAcuityInspectionResult) -> Unit,
    updateProgress: (Float) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 30.dp, vertical = 20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽: 설명 텍스트 + 프로그레스 바
            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(STRING_VISUAL_ACUITY_DESCRIPTION),
                    fontSize = 28.sp,
                    color = White,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    lineHeight = 36.sp
                )

                Spacer(modifier = Modifier.height(30.dp))

                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .height(14.dp),
                    progress = animatedProgress,
                    color = neNoon_blue,
                )
            }

            // 중앙: 시력표 (카메라 위치)
            Box(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxHeight(),
                contentAlignment = Alignment.Center
            ) {
                VisualAcuityChartBox(
                    ansNum = ansNum,
                    sightLevel = sightLevel,
                    isFaceDetected = isFaceDetected,
                    isFacingForward = isFacingForward,
                    modifier = Modifier
                        .width(380.dp)
                        .height(380.dp)
                )
            }

            // 오른쪽: 방향 버튼 3개 + 안보임 버튼
            Column(
                modifier = Modifier
                    .weight(0.3f)
                    .fillMaxHeight(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 방향 버튼 3개 (세로 배치)
                Column(
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DirectionSelectionButton(
                        direction = randomList[0],
                        onClick = {
                            onAnswerSelected(0, updateProgress) {
                                toResultScreen(getInspectionResult())
                            }
                        },
                        modifier = Modifier
                            .width(110.dp)
                            .height(110.dp)
                    )
                    DirectionSelectionButton(
                        direction = randomList[1],
                        onClick = {
                            onAnswerSelected(1, updateProgress) {
                                toResultScreen(getInspectionResult())
                            }
                        },
                        modifier = Modifier
                            .width(110.dp)
                            .height(110.dp)
                    )
                    DirectionSelectionButton(
                        direction = randomList[2],
                        onClick = {
                            onAnswerSelected(2, updateProgress) {
                                toResultScreen(getInspectionResult())
                            }
                        },
                        modifier = Modifier
                            .width(110.dp)
                            .height(110.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 안보임 버튼
                CantSeeButton(
                    onClick = {
                        onAnswerSelected(3, updateProgress) {
                            toResultScreen(getInspectionResult())
                        }
                    },
                    modifier = Modifier
                        .width(260.dp)
                        .height(90.dp)
                )
            }
        }
    }
}

// Preview
@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    device = "spec:width=800dp,height=1280dp,dpi=240"
)
@Composable
private fun PreviewVisualAcuityInspectionContent_Portrait() {
    VisualAcuityInspectionContent(
        randomList = listOf(2, 5, 7),
        ansNum = 2,
        sightLevel = 5,
        isFaceDetected = true,
        isFacingForward = true,
        onAnswerSelected = { _, _, _ -> },
        getInspectionResult = { VisualAcuityInspectionResult(leftEye = 10, rightEye = 10) },
        toResultScreen = {}
    )
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    device = "spec:width=1422dp,height=888dp,dpi=240"
)
@Composable
private fun PreviewVisualAcuityInspectionContent_Landscape() {
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
            listOf(
                "모르겠",
                "모름",
                "몰라",
                "안보여",
                "안보임",
                "안보인다",
                "안보입니다"
            ).any { compactText.contains(it) }

    if (isUnknown) {
        selectedIdx = 3
    }

    if (selectedIdx != null && selectedIdx in 0..3) {
        onAnswerSelected(selectedIdx, updateProgress, onComplete)
    }
}
