package com.pixelro.nenoonkiosk.feature.admin.advertisement.area.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideOutHorizontally
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import com.pixelro.nenoonkiosk.util.dragHandle
import com.pixelro.nenoonkiosk.util.rememberReorderableState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun ImageListArea(
    images: List<AdImageData>,
    onDeleteImage: (String) -> Unit,
    onAddImage: (String, String?) -> Unit,
    onSaveOrder: (List<AdImageData>) -> Unit,
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

        // 이미지 목록 - 로컬 상태로 관리
        val localImages = remember { mutableStateListOf<AdImageData>() }

        // 각 아이템의 visible 상태 추적 (삭제 애니메이션용)
        val visibleItems = remember { mutableStateMapOf<String, Boolean>() }
        val coroutineScope = rememberCoroutineScope()

        // images가 변경되면 localImages 동기화
        LaunchedEffect(images) {
            if (localImages != images) {
                localImages.clear()
                localImages.addAll(images)
                // 새로운 아이템들은 모두 visible 상태로 초기화
                images.forEach { image ->
                    if (!visibleItems.containsKey(image.id)) {
                        visibleItems[image.id] = true
                    }
                }
            }
        }

        val lazyListState = rememberLazyListState()
        val reorderableState = rememberReorderableState(
            list = localImages,
            getKey = { it.id },
            onMove = { fromIndex, toIndex ->
                // 드래그 중에는 아무것도 하지 않음 (localImages는 ReorderableState가 자동으로 업데이트)
            },
            lazyListState = lazyListState
        )

        LazyColumn(
            state = lazyListState,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            itemsIndexed(
                items = localImages,
                key = { _, item -> item.id }
            ) { index, image ->
                // AnimatedVisibility로 감싸서 삭제 시 애니메이션 적용
                AnimatedVisibility(
                    visible = visibleItems[image.id] ?: true,
                    exit = slideOutHorizontally(
                        targetOffsetX = { -it }, // 왼쪽으로 슬라이드 아웃
                        animationSpec = tween(durationMillis = 300)
                    ) + fadeOut(
                        animationSpec = tween(durationMillis = 300)
                    )
                ) {
                    AdImageItem(
                        fileName = image.fileName,
                        imageUri = image.imageUri,
                        onDelete = {
                            // 애니메이션 먼저 실행
                            visibleItems[image.id] = false
                            // 애니메이션이 끝난 후 실제 삭제
                            coroutineScope.launch {
                                delay(300) // 애니메이션 duration과 동일
                                onDeleteImage(image.id)
                            }
                        },
                        dragModifier = Modifier.dragHandle(
                            state = reorderableState,
                            key = image.id,
                            onDragEnd = {
                                // 드래그 종료 시 현재 localImages 순서를 DB에 저장
                                onSaveOrder(localImages.toList())
                            }
                        ),
                        modifier = reorderableState.getItemModifier(index)
                    )
                }
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
        onAddImage = { _, _ -> },
        onSaveOrder = {}
    )
}