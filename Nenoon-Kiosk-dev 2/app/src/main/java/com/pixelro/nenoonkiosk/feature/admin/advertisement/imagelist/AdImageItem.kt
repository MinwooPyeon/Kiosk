package com.pixelro.nenoonkiosk.feature.admin.advertisement.imagelist

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.filled.PlayCircleOutline
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material.icons.filled.Error
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter
import coil.request.ImageRequest
import coil.request.videoFrameMillis
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.painterResource
import com.pixelro.nenoonkiosk.R
import java.io.File
import com.pixelro.nenoonkiosk.ui.theme.Gray
import com.pixelro.nenoonkiosk.ui.theme.LightGray300
import com.pixelro.nenoonkiosk.ui.theme.Red
import com.pixelro.nenoonkiosk.ui.theme.White


@Composable
fun AdImageItem(
    fileName: String,
    imageUri: String? = null,
    onDelete: () -> Unit,
    onClick: () -> Unit = {},
    isSelected: Boolean = false,
    dragModifier: Modifier = Modifier,
    modifier: Modifier = Modifier
) {
    // 파일 경로(imageUri)로 동영상 여부 판단
    val isVideo = (imageUri ?: fileName).lowercase().let {
        it.endsWith(".mp4") || it.endsWith(".avi") ||
        it.endsWith(".mov") || it.endsWith(".mkv") ||
        it.endsWith(".3gp") || it.endsWith(".webm")
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = if (isSelected) 2.dp else 1.dp,
                color = if (isSelected) Gray.copy(alpha = 0.6f) else LightGray300,
                shape = RoundedCornerShape(8.dp)
            )
            .background(White)
            .clickable(onClick = onClick)
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

        // 미디어 썸네일 (이미지 또는 동영상)
        Box(
            modifier = Modifier
                .weight(1f)
                .clip(RoundedCornerShape(8.dp))
                .background(LightGray300),
            contentAlignment = Alignment.Center
        ) {
            if (imageUri != null) {
                val context = LocalContext.current
                var imageState by remember { mutableStateOf<AsyncImagePainter.State>(AsyncImagePainter.State.Empty) }

                // File 객체로 변환하여 ImageRequest 생성
                val imageRequest = remember(imageUri) {
                    ImageRequest.Builder(context)
                        .data(File(imageUri))
                        .apply {
                            if (isVideo) {
                                videoFrameMillis(0) // 첫 프레임 (0ms)
                            }
                        }
                        .build()
                }

                // Coil이 자동으로 이미지/동영상 처리
                AsyncImage(
                    model = imageRequest,
                    contentDescription = null,
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth,
                    onState = { state -> imageState = state }
                )

                // 로딩 실패 시 에러 아이콘 표시
                if (imageState is AsyncImagePainter.State.Error) {
                    Icon(
                        imageVector = Icons.Default.Error,
                        contentDescription = "Error loading media",
                        modifier = Modifier.size(48.dp),
                        tint = Red
                    )
                }
                // 동영상일 때 Play 아이콘 오버레이 (성공 시에만)
                else if (isVideo && imageState is AsyncImagePainter.State.Success) {
                    Icon(
                        painter = painterResource(id = R.drawable.icon_play),
                        contentDescription = "Video",
                        modifier = Modifier.size(48.dp),
                        tint = Color.Unspecified
                    )
                }
            } else {
                Icon(
                    imageVector = Icons.Default.Image,
                    contentDescription = "image",
                    modifier = Modifier.size(48.dp),
                    tint = Gray
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        // 순서 변경 핸들 (오른쪽) - 드래그해서 순서 변경
        Icon(
            imageVector = Icons.Default.Menu,
            contentDescription = stringResource(R.string.admin_reorder),
            tint = Color.Gray,
            modifier = dragModifier.fillMaxHeight()
                .padding(8.dp)
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F8F8, widthDp = 400, heightDp = 200)
@Composable
private fun AdImageItemPreview() {
    AdImageItem(
        fileName = "advertisement_banner_2024.jpg",
        imageUri = null,
        onDelete = {}
    )
}

@Preview(showBackground = true, backgroundColor = 0xFFF8F8F8, widthDp = 400, heightDp = 200)
@Composable
private fun AdVideoItemPreview() {
    AdImageItem(
        fileName = "advertisement_video_2024.mp4",
        imageUri = "/data/user/0/com.pixelro.nenoonkiosk/app_ad_images/1234567890.mp4",
        onDelete = {}
    )
}
