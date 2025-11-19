package com.pixelro.nenoonkiosk.feature.admin

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Lock
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import com.pixelro.nenoonkiosk.R

data class AdminDashboardUiState(
    val selectedTab: AdminTab = AdminTab.AD_MANAGEMENT
)

enum class AdminTab(val icon: ImageVector) {
    AD_MANAGEMENT(Icons.Default.Image),
}

@Composable
fun AdminTab.getTitle(): String {
    return when (this) {
        AdminTab.AD_MANAGEMENT -> stringResource(R.string.admin_ad_management)
    }
}