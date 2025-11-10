package com.pixelro.nenoonkiosk.feature.admin.advertisement

import com.harang.data.db.entity.LocationEntity

data class AdManagementUiState(
    val availableLocations: List<LocationEntity> = emptyList(), // DB에서 가져온 location 목록
    val selectedLocation: LocationEntity? = null, // 현재 선택된 location
    val adImages: List<AdImageData> = emptyList()
)

data class AdImageData(
    val id: String,
    val fileName: String,
    val imageUri: String? = null
)