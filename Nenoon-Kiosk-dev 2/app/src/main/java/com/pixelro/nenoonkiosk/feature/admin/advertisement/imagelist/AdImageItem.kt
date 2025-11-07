package com.pixelro.nenoonkiosk.feature.admin.advertisement.imagelist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.LightGray300


@Composable
fun AdImageItem(
    fileName: String,
    imageUri: String? = null,
    onDelete: () -> Unit,
    onReorder: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LightGray300, RoundedCornerShape(8.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 삭제 버튼 (왼쪽)
        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.RemoveCircleOutline,
                contentDescription = stringResource(R.string.admin_delete),
                tint = Color.Gray
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 이미지 썸네일과 파일명 (수직 배치)
        Column(
            modifier = Modifier.weight(1f)
        ) {
            // 이미지 썸네일
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(LightGray300),
                contentAlignment = Alignment.Center
            ) {
                if (imageUri != null) {
//                    AsyncImage(
//                        model = imageUri,
//                        contentDescription = fileName,
//                        modifier = Modifier.fillMaxSize(),
//                        contentScale = ContentScale.Crop
//                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = null,
                        modifier = Modifier.size(48.dp),
                        tint = Gray
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 파일명
            Text(
                text = fileName,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 순서 변경 버튼 (오른쪽)
        IconButton(
            onClick = onReorder,
            modifier = Modifier.size(40.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Menu,
                contentDescription = stringResource(R.string.admin_reorder),
                tint = Color.Gray
            )
        }
    }
}
