package com.pixelro.nenoonkiosk.test.dementia

import com.pixelro.nenoonkiosk.test.macular.amslergrid.MacularDisorderType

data class DementiaTestResult(
    val scores: List<DementiaViewModel.DementiaAnswer>
) {
    fun countActiveScore(): Int {
        return scores.count { it == DementiaViewModel.DementiaAnswer.Yes }
    }
}