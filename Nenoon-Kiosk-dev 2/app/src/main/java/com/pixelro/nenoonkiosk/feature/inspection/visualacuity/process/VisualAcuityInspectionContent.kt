package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.material.LinearProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.AnimationProvider
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.core.util.stt.SttConfig
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.CantSeeButton
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.DirectionSelectionButton
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process.components.VisualAcuityChartBox
import com.pixelro.nenoonkiosk.feature.inspection.visualacuity.result.VisualAcuityInspectionResult
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue
import kotlinx.coroutines.delay

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
    sttEnabled: Boolean = true,
    sttActive: Boolean = false,
    onStartVoiceRecognition: ((String) -> Unit) -> Unit,
    onCancelVoiceRecognition: () -> Unit,
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
            sttEnabled = sttEnabled,
            sttActive = sttActive,
            onStartVoiceRecognition = onStartVoiceRecognition,
            onCancelVoiceRecognition = onCancelVoiceRecognition,
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
    sttEnabled: Boolean = true,
    sttActive: Boolean = false,
    onStartVoiceRecognition: ((String) -> Unit) -> Unit,
    onCancelVoiceRecognition: () -> Unit,
) {
    var progress by remember { mutableFloatStateOf(0.1f) }
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = ProgressIndicatorDefaults.ProgressAnimationSpec,
    )
    val latestOnStartRecognition = rememberUpdatedState(onStartVoiceRecognition)
    val latestOnCancelRecognition = rememberUpdatedState(onCancelVoiceRecognition)
    val ansNumState = rememberUpdatedState(ansNum)
    val randomListState = rememberUpdatedState(randomList)
    val voiceHandler: (String) -> Unit = remember(randomList, ansNum, getInspectionResult, onAnswerSelected) {
        { recognized: String ->
            val currentChoices = randomListState.value
            Log.d("VoiceHandler", "recognized='$recognized', choices=$currentChoices, ans=${ansNumState.value}")
            handleVoiceAnswer(
                result = recognized,
                randomList = currentChoices,
                correctAnswer = ansNumState.value,
                currentProgress = progress,
                onAnswerSelected = onAnswerSelected,
                updateProgress = { newProgress -> progress = newProgress },
                onComplete = { toResultScreen(getInspectionResult()) }
            )
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            latestOnCancelRecognition.value.invoke()
        }
    }

    LaunchedEffect(sttEnabled, sttActive, voiceHandler) {
        if (sttEnabled) {
            if (!sttActive) {
                delay(SttConfig.Recognition.AUTO_RESTART_DELAY_MS)
                if (sttEnabled && !sttActive) {
                    latestOnStartRecognition.value.invoke(voiceHandler)
                }
            }
        } else if (sttActive) {
            latestOnCancelRecognition.value.invoke()
        }
    }

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
            updateProgress = { progress = it },
            sttEnabled = sttEnabled,
            sttActive = sttActive,
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
            updateProgress = { progress = it },
            sttEnabled = sttEnabled,
            sttActive = sttActive,
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
    sttEnabled: Boolean,
    sttActive: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // 시력표
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f),
            contentAlignment = Alignment.Center
        ) {
            VisualAcuityChartBox(
                ansNum = ansNum,
                sightLevel = sightLevel,
                isFaceDetected = isFaceDetected,
                isFacingForward = isFacingForward,
            )
        }

        // 설명 + 프로그레스 + 버튼들
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(horizontal = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = stringResource(STRING_VISUAL_ACUITY_DESCRIPTION),
                fontSize = 40.sp,
                color = White,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(20.dp))

            LinearProgressIndicator(
                modifier = Modifier
                    .width(490.dp)
                    .height(20.dp),
                progress = animatedProgress,
                color = neNoon_blue,
            )

            Spacer(modifier = Modifier.height(12.dp))

            VoiceRecognitionIndicator(
                sttActive = sttActive,
                sttEnabled = sttEnabled,
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
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

            Spacer(modifier = Modifier.height(10.dp))

            CantSeeButton(
                onClick = {
                    onAnswerSelected(3, updateProgress) {
                        toResultScreen(getInspectionResult())
                    }
                }
            )
        }
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
    sttEnabled: Boolean,
    sttActive: Boolean,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(horizontal = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // 시력표 (고정 크기로 찌부러짐 방지)
        Box(
            modifier = Modifier
                .size(400.dp),
            contentAlignment = Alignment.Center
        ) {
            VisualAcuityChartBox(
                ansNum = ansNum,
                sightLevel = sightLevel,
                isFaceDetected = isFaceDetected,
                isFacingForward = isFacingForward,
                modifier = Modifier.size(300.dp)
            )
        }

        // 설명 텍스트
        Text(
            text = stringResource(STRING_VISUAL_ACUITY_DESCRIPTION),
            fontSize = 30.sp,
            color = White,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
        )

        // 프로그레스 바
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(16.dp),
            progress = animatedProgress,
            color = neNoon_blue,
        )

        VoiceRecognitionIndicator(
            sttActive = sttActive,
            sttEnabled = sttEnabled,
        )

        // 방향 버튼 3개 (가로 배치)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DirectionSelectionButton(
                direction = randomList[0],
                onClick = {
                    onAnswerSelected(0, updateProgress) {
                        toResultScreen(getInspectionResult())
                    }
                },
                modifier = Modifier
            )
            Spacer(modifier = Modifier.width(16.dp))
            DirectionSelectionButton(
                direction = randomList[1],
                onClick = {
                    onAnswerSelected(1, updateProgress) {
                        toResultScreen(getInspectionResult())
                    }
                },
                modifier = Modifier
            )
            Spacer(modifier = Modifier.width(16.dp))
            DirectionSelectionButton(
                direction = randomList[2],
                onClick = {
                    onAnswerSelected(2, updateProgress) {
                        toResultScreen(getInspectionResult())
                    }
                },
                modifier = Modifier
            )
            Spacer(modifier = Modifier.height(16.dp))
            // 안보임 버튼
            CantSeeButton(
                onClick = {
                    onAnswerSelected(3, updateProgress) {
                        toResultScreen(getInspectionResult())
                    }
                },
                modifier = Modifier
            )
        }
    }
}

@Composable
private fun VoiceRecognitionIndicator(
    sttActive: Boolean,
    sttEnabled: Boolean,
) {
    val statusText = when {
        !sttEnabled -> "음성 인식 준비 중"
        sttActive -> "음성을 인식하고 있습니다"
        else -> "음성 입력을 기다리는 중입니다"
    }
    val statusColor = when {
        !sttEnabled -> Color.Gray
        sttActive -> neNoon_blue
        else -> White
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = statusText,
            color = statusColor,
            fontSize = 32.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
        )
    }
}

// Preview
@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 800,
    heightDp = 1280
)
@Composable
private fun PreviewVisualAcuityInspectionContent_Portrait() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        PortraitVisualAcuityContent(
            ansNum = 3,
            sightLevel = 5,
            isFaceDetected = true,
            isFacingForward = true,
            animatedProgress = 0.7f,
            randomList = listOf(2, 5, 7),
            onAnswerSelected = { _, _, _ -> },
            getInspectionResult = { VisualAcuityInspectionResult(leftEye = 10, rightEye = 10) },
            toResultScreen = {},
            updateProgress = {},
            sttEnabled = true,
            sttActive = false,
        )
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFF000000,
    widthDp = 1280,
    heightDp = 800
)
@Composable
private fun PreviewVisualAcuityInspectionContent_Landscape() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
    ) {
        LandscapeVisualAcuityContent(
            ansNum = 3,
            sightLevel = 5,
            isFaceDetected = true,
            isFacingForward = true,
            animatedProgress = 0.7f,
            randomList = listOf(3, 4, 6),
            onAnswerSelected = { _, _, _ -> },
            getInspectionResult = { VisualAcuityInspectionResult(leftEye = 5, rightEye = 5) },
            toResultScreen = {},
            updateProgress = {},
            sttEnabled = true,
            sttActive = false,
        )
    }
}

private fun handleVoiceAnswer(
    result: String,
    randomList: List<Int>,
    correctAnswer: Int,
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
        Log.d("VoiceHandler", "selectedIdx=$selectedIdx, correct=$correctAnswer, invoking onAnswerSelected")
        onAnswerSelected(selectedIdx, updateProgress, onComplete)
        Log.d("VoiceHandler", "onAnswerSelected invoked for index=$selectedIdx")
    }
}
