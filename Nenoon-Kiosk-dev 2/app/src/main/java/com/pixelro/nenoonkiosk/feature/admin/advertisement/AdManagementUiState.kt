package com.pixelro.nenoonkiosk.feature.admin.advertisement

import com.harang.data.db.entity.AdImageEntity
import com.harang.data.db.entity.LocationEntity

data class AdManagementUiState(
    val availableLocations: List<LocationEntity> = emptyList(), // DB에서 가져온 location 목록
    val selectedLocation: LocationEntity? = null, // 현재 선택된 location
    val adImages: List<AdImageData> = emptyList(),
    val selectedPreviewImagesByLocation: Map<Int, AdImageData> = emptyMap() // location별 선택된 미리보기 이미지
)

data class AdImageData(
    val id: String,
    val fileName: String,
    val imageUri: String? = null
)

/**
 * AdImageData를 AdImageEntity로 변환
 * @param locationId 현재 선택된 location의 ID
 */
fun AdImageData.toAdImageEntity(locationId: Int): AdImageEntity? {
    val imageUrl = this.imageUri ?: return null
    val numericId = this.id.toIntOrNull() ?: return null

    return AdImageEntity(
        id = numericId,
        locationId = locationId,
        url = imageUrl,
        order = 0,
        language = null
    )
}