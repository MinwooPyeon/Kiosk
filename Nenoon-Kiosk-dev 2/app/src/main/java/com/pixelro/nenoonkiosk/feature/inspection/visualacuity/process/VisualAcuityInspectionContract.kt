package com.pixelro.nenoonkiosk.feature.inspection.visualacuity.process

/**
 * 시력 검사 UI 상태
 */
sealed class VisualAcuityInspectionUiState {

    //거리 조정 중 (40~50cm 맞추기)
    data object MeasuringDistance : VisualAcuityInspectionUiState()


    // 시력 검사 중 (란돌트 C 검사)
    data object VisualAcuityTest : VisualAcuityInspectionUiState()
}