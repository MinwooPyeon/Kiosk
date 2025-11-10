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
import com.harang.data.db.entity.LocationEntity
import com.harang.data.db.entity.MediaType
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.admin.advertisement.display.AdLocationDisplay
import com.pixelro.nenoonkiosk.feature.admin.advertisement.imagelist.ImageListArea
import com.pixelro.nenoonkiosk.feature.admin.advertisement.location.LocationOptionArea


@Composable
fun AdManagementContent(
    uiState: AdManagementUiState,
    onSelectLocation: (LocationEntity) -> Unit,
    onDeleteImage: (String) -> Unit,
    onAddImage: () -> Unit,
    onSaveOrder: (List<AdImageData>) -> Unit
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
                    onAddImage = onAddImage,
                    onSaveOrder = onSaveOrder
                )
            } else {
                LandscapeLayout(
                    uiState = uiState,
                    onSelectLocation = onSelectLocation,
                    onDeleteImage = onDeleteImage,
                    onAddImage = onAddImage,
                    onSaveOrder = onSaveOrder
                )
            }
        }
    }
}

@Composable
private fun PortraitLayout(
    uiState: AdManagementUiState,
    onSelectLocation: (LocationEntity) -> Unit,
    onDeleteImage: (String) -> Unit,
    onAddImage: () -> Unit,
    onSaveOrder: (List<AdImageData>) -> Unit
) {
    // 세로 모드: 위에 광고 위치 선택, 아래에 미리보기와 목록
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // 광고 위치 선택
        LocationOptionArea(
            availableLocations = uiState.availableLocations,
            selectedLocation = uiState.selectedLocation,
            onSelectLocation = onSelectLocation
        )

        // 미리보기와 이미지 목록
        Row(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            AdLocationDisplay(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )

            ImageListArea(
                images = uiState.adImages,
                onDeleteImage = onDeleteImage,
                onAddImage = onAddImage,
                onSaveOrder = onSaveOrder,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
            )
        }
    }
}

@Composable
private fun LandscapeLayout(
    uiState: AdManagementUiState,
    onSelectLocation: (LocationEntity) -> Unit,
    onDeleteImage: (String) -> Unit,
    onAddImage: () -> Unit,
    onSaveOrder: (List<AdImageData>) -> Unit
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
                availableLocations = uiState.availableLocations,
                selectedLocation = uiState.selectedLocation,
                onSelectLocation = onSelectLocation
            )

            // 광고 위치 미리보기 섹션
            AdLocationDisplay()
        }

        // 우측: 광고 이미지 목록
        ImageListArea(
            images = uiState.adImages,
            onDeleteImage = onDeleteImage,
            onAddImage = onAddImage,
            onSaveOrder = onSaveOrder,
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F8F8, widthDp = 1280, heightDp = 800)
@Composable
private fun AdManagementContentLandscapePreview() {
    val sampleUiState = AdManagementUiState(
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
        onAddImage = {},
        onSaveOrder = {}
    )
}


@Preview(showBackground = true, backgroundColor = 0xFFF8F8F8, widthDp = 800, heightDp = 1280)
@Composable
private fun AdManagementContentPortraitPreview() {
    val sampleUiState = AdManagementUiState(
        availableLocations = listOf(
            LocationEntity(id = 1, name = "TEST_LIST_SCREEN", mediaType = MediaType.IMAGE),
            LocationEntity(id = 2, name = "SCREENSAVER", mediaType = MediaType.VIDEO)
        ),
        selectedLocation = LocationEntity(id = 1, name = "TEST_LIST_SCREEN", mediaType = MediaType.IMAGE),
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
        onAddImage = {},
        onSaveOrder = {}
    )
}