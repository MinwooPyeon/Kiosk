package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.text.AnnotatedString

data class AmslerGridUiState(
    val showMeasuringDistance: Boolean,
    val showAmslerGrid: Boolean,

    val isBlinkingDone: Boolean,
    val isDotShowing: Boolean,
    val isSelectTtsDone: Boolean,
    val isTestStarted: Boolean,
    val isLeftEye: Boolean,

    val rotX: Float,
    val rotY: Float,

    /** 9칸 영역 선택 상태 */
    val selectedAreas: List<MacularDisorderType>,
    /** 안내문 문자열(이미 계산된 상태로 제공 가능) */
    val headerAnnotated: AnnotatedString,
    /** 동영상 가이드 재생 여부(좌안/중앙 정렬/테스트 시작 전/ TTS 진행 중) */
    val shouldPlayGuideVideo: Boolean,
)

sealed class AmslerGridEvent {
    /** 측정 거리 → 격자 진입 */
    data object ProceedFromDistance : AmslerGridEvent()

    /** 격자 9칸 중 탭 좌표 보고 */
    data class AreaPressed(val position: Offset) : AmslerGridEvent()

    /** 완료 버튼 */
    data object CompletePressed : AmslerGridEvent()
}

enum class MacularDisorderType {
    Normal,
    Distorted,
    Blacked,
    Whited,
}

data class AmslerGridTestResult(
    val leftEyeDisorderType: List<MacularDisorderType> = listOf(),
    val rightEyeDisorderType: List<MacularDisorderType> = listOf(),
)