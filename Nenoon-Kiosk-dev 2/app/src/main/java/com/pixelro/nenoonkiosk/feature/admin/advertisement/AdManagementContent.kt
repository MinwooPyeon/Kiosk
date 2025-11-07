package com.pixelro.nenoonkiosk.feature.admin.advertisement

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.admin.AdImageData
import com.pixelro.nenoonkiosk.feature.admin.AdLocation
import com.pixelro.nenoonkiosk.feature.admin.AdminDashboardScreen
import com.pixelro.nenoonkiosk.feature.admin.AdminDashboardUiState
import com.pixelro.nenoonkiosk.feature.admin.AdminTab
import com.pixelro.nenoonkiosk.feature.admin.advertisement.area.components.ImageListArea
import com.pixelro.nenoonkiosk.feature.admin.advertisement.area.components.LocationOptionArea
import com.pixelro.nenoonkiosk.feature.admin.advertisement.area.components.AdLocationPreview


@Composable
fun AdManagementContent(
    uiState: AdminDashboardUiState,
    onSelectLocation: (AdLocation) -> Unit,
    onDeleteImage: (String) -> Unit,
    onAddImage: (String, String?) -> Unit
) {
    BoxWithConstraints(
        modifier = Modifier.fillMaxSize()
    ) {
        val isPortrait = maxHeight > maxWidth

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                // 헤더
                Text(
                    text = stringResource(R.string.admin_ad_image_management),
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                )

                Spacer(modifier = Modifier.width(8.dp))

                Icon(
                    imageVector = Icons.Default.Build,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
            }

            Text(
                text = stringResource(R.string.admin_ad_image_description),
                fontSize = 16.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            if (isPortrait) {
                PortraitLayout(
                    uiState = uiState,
                    onSelectLocation = onSelectLocation,
                    onDeleteImage = onDeleteImage,
                    onAddImage = onAddImage
                )
            } else {
                LandscapeLayout(
                    uiState = uiState,
                    onSelectLocation = onSelectLocation,
                    onDeleteImage = onDeleteImage,
                    onAddImage = onAddImage
                )
            }
        }
    }
}

@Composable
private fun PortraitLayout(
    uiState: AdminDashboardUiState,
    onSelectLocation: (AdLocation) -> Unit,
    onDeleteImage: (String) -> Unit,
    onAddImage: (String, String?) -> Unit
) {
    // 세로 모드: 위에 광고 위치 선택, 아래에 미리보기와 목록
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 광고 위치 선택
        LocationOptionArea(
            selectedLocation = uiState.selectedAdLocation,
            onSelectLocation = onSelectLocation
        )

        // 미리보기와 이미지 목록
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AdLocationPreview(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            ImageListArea(
                images = uiState.adImages,
                onDeleteImage = onDeleteImage,
                onAddImage = onAddImage,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun LandscapeLayout(
    uiState: AdminDashboardUiState,
    onSelectLocation: (AdLocation) -> Unit,
    onDeleteImage: (String) -> Unit,
    onAddImage: (String, String?) -> Unit
) {
    // 가로 모드: 좌측에 광고 위치 선택과 미리보기, 우측에 목록
    Row(
        modifier = Modifier.fillMaxSize(),
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 좌측: 광고 위치 선택
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 광고 위치 선택 섹션
            LocationOptionArea(
                selectedLocation = uiState.selectedAdLocation,
                onSelectLocation = onSelectLocation
            )

            // 광고 위치 미리보기 섹션
            AdLocationPreview()
        }

        // 우측: 광고 이미지 목록
        ImageListArea(
            images = uiState.adImages,
            onDeleteImage = onDeleteImage,
            onAddImage = onAddImage,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F8F8, widthDp = 1280, heightDp = 800)
@Composable
private fun AdManagementContentLandscapePreview() {
    val sampleUiState = AdminDashboardUiState(
        selectedTab = AdminTab.AD_MANAGEMENT,
        selectedAdLocation = AdLocation.SCREENSAVER,
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
            ),
            AdImageData(
                id = "3",
                fileName = "ad_sample_3.jpg",
                imageUri = null
            ),
            AdImageData(
                id = "4",
                fileName = "ad_sample_4.jpg",
                imageUri = null
            )
        )
    )

    AdManagementContent(
        uiState = sampleUiState,
        onSelectLocation = {},
        onDeleteImage = {},
        onAddImage = { _, _ -> }
    )
}


@Preview(showBackground = true, backgroundColor = 0xFFF8F8F8, widthDp = 800, heightDp = 1280)
@Composable
private fun AdManagementContentPortraitPreview() {
    val sampleUiState = AdminDashboardUiState(
        selectedTab = AdminTab.AD_MANAGEMENT,
        selectedAdLocation = AdLocation.TEST_LIST_SCREEN,
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
            ),
            AdImageData(
                id = "3",
                fileName = "ad_sample_3.jpg",
                imageUri = null
            )
        )
    )

    AdManagementContent(
        uiState = sampleUiState,
        onSelectLocation = {},
        onDeleteImage = {},
        onAddImage = { _, _ -> }
    )
}
