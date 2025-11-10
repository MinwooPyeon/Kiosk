package com.pixelro.nenoonkiosk.feature.admin

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.harang.data.db.entity.LocationEntity
import com.harang.data.db.entity.MediaType
import com.pixelro.nenoonkiosk.feature.admin.advertisement.AdImageData
import com.pixelro.nenoonkiosk.feature.admin.advertisement.AdManagementContent
import com.pixelro.nenoonkiosk.feature.admin.advertisement.AdManagementUiState
import com.pixelro.nenoonkiosk.feature.admin.advertisement.AdManagementViewModel
import com.pixelro.nenoonkiosk.feature.admin.advertisement.sidebar.SidebarArea
import com.pixelro.nenoonkiosk.feature.admin.password.PasswordManagementContent
import com.pixelro.nenoonkiosk.ui.theme.LightGray200
import com.pixelro.nenoonkiosk.ui.theme.White


@Composable
fun AdminDashboardScreen(
    isOutClick: () -> Unit,
    viewModel: AdminDashboardViewModel = hiltViewModel(),
    adManagementViewModel: AdManagementViewModel = hiltViewModel()
) {
    val dashboardUiState by viewModel.uiState.collectAsState()
    val adManagementUiState by adManagementViewModel.uiState.collectAsState()

    // 갤러리 미디어 선택 launcher
    val mediaPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri ->
            uri?.let { adManagementViewModel.addAdImageFromUri(it) }
        }
    )

    AdminDashboardContent(
        dashboardUiState = dashboardUiState,
        adManagementUiState = adManagementUiState,
        onTabSelected = viewModel::selectTab,
        onSelectLocation = adManagementViewModel::selectAdLocation,
        onDeleteImage = adManagementViewModel::deleteAdImage,
        onAddImage = {
            // 선택된 location의 mediaType에 따라 이미지 또는 동영상만 선택
            val mediaType = when (adManagementUiState.selectedLocation?.mediaType) {
                MediaType.IMAGE -> ActivityResultContracts.PickVisualMedia.ImageOnly
                MediaType.VIDEO -> ActivityResultContracts.PickVisualMedia.VideoOnly
                else -> ActivityResultContracts.PickVisualMedia.ImageOnly // 기본값
            }
            mediaPickerLauncher.launch(PickVisualMediaRequest(mediaType))
        },
        onSaveOrder = adManagementViewModel::saveAdImagesOrder,
        isOutClick = isOutClick
    )
}

@Composable
private fun AdminDashboardContent(
    dashboardUiState: AdminDashboardUiState,
    adManagementUiState: AdManagementUiState,
    onTabSelected: (AdminTab) -> Unit,
    onSelectLocation: (LocationEntity) -> Unit,
    onDeleteImage: (String) -> Unit,
    onAddImage: () -> Unit,
    onSaveOrder: (List<AdImageData>) -> Unit,
    isOutClick: () -> Unit
) {
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
                    selectedTab = dashboardUiState.selectedTab,
                    onTabSelected = onTabSelected,
                    isOutClick = isOutClick
                )

                // 메인 컨텐츠 영역
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightGray200)
                ) {
                    when (dashboardUiState.selectedTab) {
                        AdminTab.AD_MANAGEMENT -> AdManagementContent(
                            uiState = adManagementUiState,
                            onSelectLocation = onSelectLocation,
                            onDeleteImage = onDeleteImage,
                            onAddImage = onAddImage,
                            onSaveOrder = onSaveOrder
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
                    selectedTab = dashboardUiState.selectedTab,
                    onTabSelected = onTabSelected,
                    isOutClick = isOutClick
                )

                // 메인 컨텐츠 영역
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(LightGray200)
                ) {
                    when (dashboardUiState.selectedTab) {
                        AdminTab.AD_MANAGEMENT -> AdManagementContent(
                            uiState = adManagementUiState,
                            onSelectLocation = onSelectLocation,
                            onDeleteImage = onDeleteImage,
                            onAddImage = onAddImage,
                            onSaveOrder = onSaveOrder
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
fun AdminDashboardContentPreview() {
    AdminDashboardContent(
        dashboardUiState = AdminDashboardUiState(selectedTab = AdminTab.AD_MANAGEMENT),
        adManagementUiState = AdManagementUiState(
            availableLocations = listOf(
                LocationEntity(id = 1, name = "TEST_LIST_SCREEN", mediaType = MediaType.IMAGE),
                LocationEntity(id = 2, name = "SCREENSAVER", mediaType = MediaType.VIDEO)
            ),
            selectedLocation = LocationEntity(id = 1, name = "TEST_LIST_SCREEN", mediaType = MediaType.IMAGE),
            adImages = listOf(
                AdImageData(
                    id = "1",
                    fileName = "ad_lens.jpg",
                    imageUri = null
                ),
                AdImageData(
                    id = "2",
                    fileName = "ad_hades.jpg",
                    imageUri = null
                ),
                AdImageData(
                    id = "3",
                    fileName = "ad_hades_en.jpg",
                    imageUri = null
                )
            )
        ),
        onTabSelected = {},
        onSelectLocation = {},
        onDeleteImage = {},
        onAddImage = {},
        onSaveOrder = {},
        isOutClick = {}
    )
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
fun AdminDashboardContentPortraitPreview() {
    AdminDashboardContent(
        dashboardUiState = AdminDashboardUiState(selectedTab = AdminTab.AD_MANAGEMENT),
        adManagementUiState = AdManagementUiState(
            availableLocations = listOf(
                LocationEntity(id = 1, name = "TEST_LIST_SCREEN", mediaType = MediaType.IMAGE),
                LocationEntity(id = 2, name = "SCREENSAVER", mediaType = MediaType.VIDEO)
            ),
            selectedLocation = LocationEntity(id = 2, name = "SCREENSAVER", mediaType = MediaType.VIDEO),
            adImages = listOf(
                AdImageData(
                    id = "1",
                    fileName = "ad_sample_1.jpg",
                    imageUri = null
                ),
                AdImageData(
                    id = "2",
                    fileName = "ad_sample_2.jpg",
                    imageUri = null
                )
            )
        ),
        onTabSelected = {},
        onSelectLocation = {},
        onDeleteImage = {},
        onAddImage = {},
        onSaveOrder = {},
        isOutClick = {}
    )
}