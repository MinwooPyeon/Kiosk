package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.SubcomposeAsyncImage
import com.harang.data.db.entity.AdImageEntity
import com.pixelro.nenoonkiosk.ui.theme.White

/**
 * 광고 내용
 * @param adImage 표시할 광고 이미지 엔티티
 */
@Composable
fun Advertisement(adImage: AdImageEntity?) {
    Card(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(300.dp),
        elevation = CardDefaults.cardElevation(0.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = White,
            ),
        shape = RoundedCornerShape(8.dp),
    ) {
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .background(White),
            contentAlignment = Alignment.Center,
        ) {
            if (adImage != null) {
                SubcomposeAsyncImage(
                    model = adImage.url,
                    contentDescription = "광고",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(shape = RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    error = {
                        Icon(
                            imageVector = Icons.Default.Image,
                            contentDescription = "이미지 로드 실패",
                            modifier = Modifier.size(80.dp),
                            tint = Color.Gray
                        )
                    }
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "광고 없음",
                    modifier = Modifier.size(80.dp),
                    tint = Color.Gray
                )
            }
        }
    }
}
