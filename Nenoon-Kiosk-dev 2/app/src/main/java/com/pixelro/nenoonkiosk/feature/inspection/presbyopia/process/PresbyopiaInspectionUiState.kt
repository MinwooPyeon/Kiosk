package com.pixelro.nenoonkiosk.feature.inspection.presbyopia.process

import com.pixelro.nenoonkiosk.feature.inspection.presbyopia.PresbyopiaViewModel

/**
 * 노안 검사 UI 상태
 */
sealed class PresbyopiaInspectionUiState {
    /**
     * 검사 시작 상태 (카운트다운 표시)
     */
    data object Started : PresbyopiaInspectionUiState()

    /**
     * 거리 조정 중 (비디오/이미지 표시 + 거리 표시)
     */
    data object AdjustingDistance : PresbyopiaInspectionUiState()

    /**
     * 텍스트 깜빡임 안내 (영상 전 안내문)
     */
    data object TextBlinking : PresbyopiaInspectionUiState()

    /**
     * 가까이 다가오는 중 (거리 측정)
     */
    data object ComingCloser : PresbyopiaInspectionUiState()

    /**
     * 노안 없음 (25cm 이하)
     */
    data object NoPresbyopia : PresbyopiaInspectionUiState()
}

fun PresbyopiaViewModel.TestState.toUiState(): PresbyopiaInspectionUiState {
    return when (this) {
        PresbyopiaViewModel.TestState.Started -> PresbyopiaInspectionUiState.Started
        PresbyopiaViewModel.TestState.AdjustingDistance -> PresbyopiaInspectionUiState.AdjustingDistance
        PresbyopiaViewModel.TestState.TextBlinking -> PresbyopiaInspectionUiState.TextBlinking
        PresbyopiaViewModel.TestState.ComingCloser -> PresbyopiaInspectionUiState.ComingCloser
        PresbyopiaViewModel.TestState.NoPresbyopia -> PresbyopiaInspectionUiState.NoPresbyopia
    }
}
