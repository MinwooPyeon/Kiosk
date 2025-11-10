package com.pixelro.nenoonkiosk.feature.admin.advertisement

data class AdManagementUiState(
    val selectedAdLocation: AdLocation? = AdLocation.TEST_LIST_SCREEN,
    val adImages: List<AdImageData> = emptyList()
)

enum class AdLocation {
    TEST_LIST_SCREEN,
    SCREENSAVER;

    fun toLocationId(): Int {
        return when (this) {
            TEST_LIST_SCREEN -> 1
            SCREENSAVER -> 2
        }
    }
}

data class AdImageData(
    val id: String,
    val fileName: String,
    val imageUri: String? = null
)