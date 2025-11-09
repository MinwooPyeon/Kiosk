package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.harang.data.db.entity.AdImageEntity
import com.pixelro.nenoonkiosk.ui.theme.LightGray400
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
                    .fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            adImage?.let {
                AsyncImage(
                    model = it.url,
                    contentDescription = "광고",
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(shape = RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.FillWidth
                )
            }
        }
    }
}
