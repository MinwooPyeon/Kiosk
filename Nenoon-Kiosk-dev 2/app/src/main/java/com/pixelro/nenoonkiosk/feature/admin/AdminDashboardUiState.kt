package com.pixelro.nenoonkiosk.feature.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.pixelro.nenoonkiosk.R

data class AdminDashboardUiState(
    val selectedTab: AdminTab = AdminTab.AD_MANAGEMENT,
    val selectedAdLocation: AdLocation? = AdLocation.TEST_LIST_SCREEN,
    val adImages: List<AdImageData> = emptyList()
)

enum class AdminTab(val icon: ImageVector) {
    AD_MANAGEMENT(Icons.Default.Image),
    PASSWORD_MANAGEMENT(Icons.Default.Lock)
}

@Composable
fun AdminTab.getTitle(): String {
    return when (this) {
        AdminTab.AD_MANAGEMENT -> stringResource(R.string.admin_ad_management)
        AdminTab.PASSWORD_MANAGEMENT -> stringResource(R.string.admin_password_management)
    }
}

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