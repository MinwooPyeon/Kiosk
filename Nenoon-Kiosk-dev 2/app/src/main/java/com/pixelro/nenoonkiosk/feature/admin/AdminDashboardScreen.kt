package com.pixelro.nenoonkiosk.feature.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.admin.advertisement.AdManagementContent
import com.pixelro.nenoonkiosk.feature.admin.advertisement.area.components.SidebarArea
import com.pixelro.nenoonkiosk.feature.admin.password.PasswordManagementContent
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.LightGray200
import com.pixelro.nenoonkiosk.ui.theme.LightGray300
import com.pixelro.nenoonkiosk.ui.theme.White



@Composable
fun AdminDashboardScreen(
    isOutClick: () -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(White)
    ) {
        val isPortrait = maxHeight > maxWidth

        if (isPortrait) {
            // 세로 모드: 상단 바 + 메인 컨텐츠 (세로로 배치)
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 상단 바
                SidebarArea(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = viewModel::selectTab,
                    isOutClick = isOutClick
                )

                // 메인 컨텐츠 영역
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightGray200)
                ) {
                    when (uiState.selectedTab) {
                        AdminTab.AD_MANAGEMENT -> AdManagementContent(
                            uiState = uiState,
                            onSelectLocation = viewModel::selectAdLocation,
                            onDeleteImage = viewModel::deleteAdImage,
                            onAddImage = viewModel::addAdImage,
                            onSaveOrder = viewModel::saveAdImagesOrder
                        )

                        AdminTab.PASSWORD_MANAGEMENT -> PasswordManagementContent()
                    }
                }
            }
        } else {
            // 가로 모드: 좌측 사이드바 + 메인 컨텐츠 (가로로 배치)
            Row(
                modifier = Modifier.fillMaxSize()
            ) {
                // 좌측 사이드바
                SidebarArea(
                    selectedTab = uiState.selectedTab,
                    onTabSelected = viewModel::selectTab,
                    isOutClick = isOutClick
                )

                // 메인 컨텐츠 영역
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightGray200)
                ) {
                    when (uiState.selectedTab) {
                        AdminTab.AD_MANAGEMENT -> AdManagementContent(
                            uiState = uiState,
                            onSelectLocation = viewModel::selectAdLocation,
                            onDeleteImage = viewModel::deleteAdImage,
                            onAddImage = viewModel::addAdImage,
                            onSaveOrder = viewModel::saveAdImagesOrder
                        )

                        AdminTab.PASSWORD_MANAGEMENT -> PasswordManagementContent()
                    }
                }
            }
        }
    }
}


@Preview(showBackground = true, widthDp = 1280, heightDp = 800, apiLevel = 34)
@Composable
fun AdminDashboardScreenPreview() {
    AdminDashboardScreen(
        isOutClick = {},
    )
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
fun AdminDashboardScreenPortraitPreview() {
    AdminDashboardScreen(
        isOutClick = {},
    )
}