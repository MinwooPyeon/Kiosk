package com.pixelro.nenoonkiosk.feature.admin.advertisement

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harang.data.db.entity.AdImageEntity
import com.harang.data.repository.AdImageRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AdManagementViewModel @Inject constructor(
    private val adImageRepository: AdImageRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdManagementUiState())
    val uiState: StateFlow<AdManagementUiState> = _uiState.asStateFlow()

    private var loadImagesJob: kotlinx.coroutines.Job? = null

    init {
        // 초기 선택된 location의 이미지 로드
        loadAdImages(_uiState.value.selectedAdLocation)
    }

    private fun loadAdImages(location: AdLocation?) {
        // 이전 로드 작업 취소
        loadImagesJob?.cancel()

        location?.let {
            val locationId = it.toLocationId()
            loadImagesJob = adImageRepository.getAdImagesByLocation(locationId)
                .onEach { entities ->
                    _uiState.update { currentState ->
                        currentState.copy(
                            adImages = entities.map { entity -> entity.toAdImageData() }
                        )
                    }
                }
                .launchIn(viewModelScope)
        }
    }

    private fun AdImageEntity.toAdImageData(): AdImageData {
        return AdImageData(
            id = this.id.toString(),
            fileName = this.name,
            imageUri = this.url
        )
    }

    fun selectAdLocation(location: AdLocation) {
        _uiState.update { currentState ->
            currentState.copy(selectedAdLocation = location)
        }
        // location 변경 시 해당 location의 이미지 다시 로드
        loadAdImages(location)
    }

    fun deleteAdImage(imageId: String) {
        viewModelScope.launch {
            // imageId를 Int로 변환하고 DB에서 해당 이미지 찾아서 삭제
            val id = imageId.toIntOrNull() ?: return@launch
            val adImage = adImageRepository.getAdImageById(id) ?: return@launch
            adImageRepository.deleteAdImage(adImage)
            // Flow가 자동으로 UI 업데이트하므로 별도의 상태 업데이트 불필요
        }
    }

    /**
     * 갤러리에서 선택한 이미지를 광고 이미지로 추가합니다.
     * @param uri 갤러리에서 선택한 이미지 Uri
     */
    fun addAdImageFromUri(uri: Uri) {
        viewModelScope.launch {
            val selectedLocation = _uiState.value.selectedAdLocation ?: return@launch
            val locationId = selectedLocation.toLocationId()

            // 현재 해당 location의 이미지 개수를 가져와서 order 값 설정
            val currentImages = _uiState.value.adImages
            val nextOrder = currentImages.size + 1

            // 파일명 생성 (timestamp 기반)
            val fileName = "ad_image_${System.currentTimeMillis()}"

            // Repository를 통해 DB에 저장
            adImageRepository.insertAdImageFromUri(uri, locationId, nextOrder, fileName)
            // Flow가 자동으로 UI 업데이트
        }
    }

    // 드래그 종료 시: ImageListArea의 localImages 순서를 DB에 저장
    fun saveAdImagesOrder(orderedImages: List<AdImageData>) {
        viewModelScope.launch {

            // DB 업데이트: 모든 이미지의 order 값 업데이트
            orderedImages.forEachIndexed { index, adImageData ->
                val id = adImageData.id.toIntOrNull()
                if (id == null) {
                    return@forEachIndexed
                }

                val entity = adImageRepository.getAdImageById(id)
                if (entity == null) {
                    return@forEachIndexed
                }

                val updatedEntity = entity.copy(order = index + 1)
                adImageRepository.updateAdImage(updatedEntity)
            }
        }
    }
}