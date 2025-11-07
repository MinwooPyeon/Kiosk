package com.pixelro.nenoonkiosk.feature.admin.advertisement.area.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.feature.admin.AdImageData
import com.pixelro.nenoonkiosk.feature.admin.advertisement.imagelist.AdImageItem
import com.pixelro.nenoonkiosk.ui.theme.LightGray300
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue


@Composable
fun ImageListArea(
    images: List<AdImageData>,
    onDeleteImage: (String) -> Unit,
    onAddImage: (String, String?) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .border(1.dp, LightGray300, RoundedCornerShape(12.dp))
            .background(White, RoundedCornerShape(12.dp))
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.admin_ad_image_list),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Button(
                onClick = { onAddImage("새_이미지.jpg", null) },
                colors = ButtonDefaults.buttonColors(
                    containerColor = neNoon_blue
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text(stringResource(R.string.admin_add))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 이미지 목록
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(images) { image ->
                AdImageItem(
                    fileName = image.fileName,
                    imageUri = image.imageUri,
                    onDelete = { onDeleteImage(image.id) },
                    onReorder = { /* TODO: 순서 변경 로직 */ }
                )
            }
        }
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F8F8, widthDp = 400, heightDp = 400)
@Composable
private fun ImageListAreaWithImagesPreview() {
    ImageListArea(
        images = listOf(
            AdImageData(
                id = "1",
                fileName = "advertisement_banner_1.jpg",
                imageUri = null
            ),
            AdImageData(
                id = "2",
                fileName = "promo_image_2024.png",
                imageUri = null
            ),
            AdImageData(
                id = "3",
                fileName = "special_offer.jpg",
                imageUri = null
            ),
            AdImageData(
                id = "4",
                fileName = "new_product_banner.jpg",
                imageUri = null
            )
        ),
        onDeleteImage = {},
        onAddImage = { _, _ -> }
    )
}
