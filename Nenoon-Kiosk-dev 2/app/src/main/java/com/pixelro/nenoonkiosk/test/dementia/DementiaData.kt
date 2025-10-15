package com.pixelro.nenoonkiosk.test.dementia

data class DementiaData(
    val scores: List<DementiaViewModel.DementiaAnswer>
) {
    fun countActiveScore(): Int {
        return scores.count { it == DementiaViewModel.DementiaAnswer.Yes }
    }
}