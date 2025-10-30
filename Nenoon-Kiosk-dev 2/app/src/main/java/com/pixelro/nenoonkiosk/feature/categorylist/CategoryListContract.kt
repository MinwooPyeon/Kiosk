package com.pixelro.nenoonkiosk.feature.categorylist

data class CategoryListUiState(
    val isDescriptionShowing: Boolean = true,
    val pid: Int = 0
)

sealed interface CategoryListSideEffect {
    data object StopTts : CategoryListSideEffect
}
