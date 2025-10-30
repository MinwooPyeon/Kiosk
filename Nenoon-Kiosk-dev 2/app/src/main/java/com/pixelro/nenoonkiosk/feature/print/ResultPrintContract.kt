package com.pixelro.nenoonkiosk.feature.print

import com.pixelro.nenoonkiosk.core.util.dataprovider.CompoundTestResult
import com.pixelro.nenoonkiosk.feature.print.model.PrintStrings

data class ResultPrintUiState(
    val loading: Boolean,
    val summaries: List<ResultSummary>,  // 표시는 이걸로만
    val canPrint: Boolean,
    val title: String
)

data class ResultSummary(
    val label: String,
    val completed: Boolean
)

// Domain -> UI 매핑 (latest 1건만 표시)
fun mapToSummaries(
    latest: CompoundTestResult?,
    strings: PrintStrings
): List<ResultSummary> {
    if (latest == null) return emptyList()
    return listOf(
        ResultSummary(strings.visualAcuity, latest.shortVisualAcuityTestResult != null),
        ResultSummary(strings.presbyopia, latest.presbyopiaInspectionResult != null),
        ResultSummary(strings.amsler, latest.amslerGridTestResult != null),
        ResultSummary(strings.mChart, latest.mChartTestResult != null),
        ResultSummary(strings.bloodPressure, latest.bloodPressureTestResult != null),
        ResultSummary(strings.gripStrength, latest.gripStrengthTestResult != null),
        ResultSummary(strings.dementia, latest.dementiaTestResult != null),
    )
}