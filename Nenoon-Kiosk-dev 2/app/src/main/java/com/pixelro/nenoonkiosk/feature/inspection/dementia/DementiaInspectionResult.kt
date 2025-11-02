package com.pixelro.nenoonkiosk.feature.inspection.dementia

enum class DementiaScore {
    None, One, Two, Three, Four, Five, Six, Seven, Eight, Nine, Ten, Eleven, Twelve, Thirteen, Fourteen
}

data class DementiaInspectionResult(
    val scores: List<DementiaAnswer>
) {
    fun countActiveScore(): Int {
        return scores.count { it == DementiaAnswer.Yes }
    }
}
