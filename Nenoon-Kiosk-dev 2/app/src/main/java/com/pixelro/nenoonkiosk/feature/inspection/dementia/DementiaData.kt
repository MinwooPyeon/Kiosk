package com.pixelro.nenoonkiosk.feature.inspection.dementia

data class DementiaData(
    val scores: List<DementiaViewModel.DementiaAnswer>
) {
    fun countActiveScore(): Int {
        return scores.count { it == DementiaViewModel.DementiaAnswer.Yes }
    }
}