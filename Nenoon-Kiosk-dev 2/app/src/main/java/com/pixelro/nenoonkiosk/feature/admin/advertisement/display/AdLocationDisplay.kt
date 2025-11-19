package com.pixelro.nenoonkiosk.feature.admin.advertisement.display

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.remember
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.harang.data.db.entity.AdImageEntity
import java.io.File
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.LightGray300
import com.pixelro.nenoonkiosk.ui.theme.Red
import com.pixelro.nenoonkiosk.ui.theme.White


@Composable
fun AdLocationDisplay(
    modifier: Modifier = Modifier,
    adImages: List<AdImageEntity> = emptyList(),
    locationId: Int = 1,
    selectedImageUri: String? = null
) {
    Column(
        modifier = modifier
            .border(1.dp, LightGray300, RoundedCornerShape(12.dp))
            .background(White, RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Text(
            text = stringResource(R.string.admin_ad_location_preview),
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // 미리보기 화면 영역
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .padding(30.dp),
            contentAlignment = Alignment.Center
        ) {
            val isLandscape = maxWidth > maxHeight

            // location과 방향에 따른 이미지 리소스 결정
            val imageRes = when (locationId) {
                1 -> if (isLandscape) R.drawable.display_1_horizontal else R.drawable.display_1_vertical
                2 -> if (isLandscape) R.drawable.display_2_horizontal else R.drawable.display_2_vertical
                else -> if (isLandscape) R.drawable.display_1_horizontal else R.drawable.display_1_vertical
            }

            // 이미지와 오버레이 표시
            Box(

            ) {
                val painter = painterResource(id = imageRes)

                Image(
                    painter = painter,
                    contentDescription = "광고 위치 미리보기",
                    contentScale = ContentScale.Fit,
                    modifier = Modifier.border(1.dp, Color.Gray)
                )

                // 광고 위치 오버레이 - location과 방향에 따라 다른 영역 표시
                BoxWithConstraints(
                    modifier = Modifier.matchParentSize()
                ) {
                    val imageWidth = maxWidth
                    val imageHeight = maxHeight

                    // location과 방향에 따른 광고 영역 계산
                    val overlayModifier = when (locationId) {
                        1 -> if (isLandscape) {
                            // Location 1 가로: 왼쪽 광고 배너
                            Modifier
                                .fillMaxWidth(0.41f)
                                .fillMaxHeight(0.27f)
                                .offset(x = imageWidth * 0.05f, y = imageHeight * 0.26f)
                        } else {
                            // Location 1 세로: 상단 광고 배너
                            Modifier
                                .fillMaxWidth(0.9f)
                                .fillMaxHeight(0.235f)
                                .offset(x = imageWidth * 0.05f,y = imageHeight * 0.116f)
                        }
                        2 -> if (isLandscape) {
                            // Location 2 가로: 상단 텍스트 광고 (0-10%)
                            Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.9f)
                                .offset(y = imageHeight * 0.11f)
                        } else {
                            // Location 2 세로: 중앙 텍스트 광고 (20-40%)
                            Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(0.362f)
                                .offset(y = imageHeight * 0.358f)
                        }
                        else -> Modifier.fillMaxSize()
                    }

                    Box(
                        modifier = overlayModifier,
                        contentAlignment = Alignment.Center
                    ){
                        // 선택된 광고 이미지 표시 (빨간 오버레이 뒤에)
                        if (selectedImageUri != null) {
                            val context = LocalContext.current
                            val imageRequest = remember(selectedImageUri) {
                                ImageRequest.Builder(context)
                                    .data(selectedImageUri)
                                    .build()
                            }

                            AsyncImage(
                                model = imageRequest,
                                contentDescription = "선택된 광고 이미지",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        // 빨간 오버레이 (이미지 위에)
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(Red.copy(alpha = 0.3f))
                                .border(2.dp, Color.Red)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400, name = "Location 1 - 가로")
@Composable
private fun AdLocationDisplay1HorizontalPreview() {
    AdLocationDisplay(
        adImages = emptyList(),
        locationId = 1
    )
}

@Preview(showBackground = true, widthDp = 400, heightDp = 600, name = "Location 1 - 세로")
@Composable
private fun AdLocationDisplay1VerticalPreview() {
    AdLocationDisplay(
        adImages = emptyList(),
        locationId = 1
    )
}

@Preview(showBackground = true, widthDp = 800, heightDp = 400, name = "Location 2 - 가로")
@Composable
private fun AdLocationDisplay2HorizontalPreview() {
    AdLocationDisplay(
        adImages = emptyList(),
        locationId = 2
    )
}

@Preview(showBackground = true, widthDp = 400, heightDp = 600, name = "Location 2 - 세로")
@Composable
private fun AdLocationDisplay2VerticalPreview() {
    AdLocationDisplay(
        adImages = emptyList(),
        locationId = 2
    )
}