package com.pixelro.nenoonkiosk.feature.admin.advertisement

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.harang.data.db.entity.AdImageEntity
import com.harang.data.db.entity.LocationEntity
import com.harang.data.repository.AdImageRepository
import com.harang.data.repository.LocationRepository
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
    private val adImageRepository: AdImageRepository,
    private val locationRepository: LocationRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(AdManagementUiState())
    val uiState: StateFlow<AdManagementUiState> = _uiState.asStateFlow()

    private var loadImagesJob: kotlinx.coroutines.Job? = null

    init {
        // DB에서 모든 location 로드
        loadAllLocations()
    }

    private fun loadAllLocations() {
        locationRepository.getAllLocations()
            .onEach { locations ->
                _uiState.update { currentState ->
                    currentState.copy(
                        availableLocations = locations,
                        selectedLocation = currentState.selectedLocation ?: locations.firstOrNull()
                    )
                }
                // 첫 번째 location의 이미지 로드 (초기화 시에만)
                if (_uiState.value.selectedLocation != null && loadImagesJob == null) {
                    loadAdImages(_uiState.value.selectedLocation)
                }
            }
            .launchIn(viewModelScope)
    }

    private fun loadAdImages(location: LocationEntity?) {
        // 이전 로드 작업 취소
        loadImagesJob?.cancel()

        location?.let {
            loadImagesJob = adImageRepository.getAdImagesByLocation(it.id)
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
            fileName = "", // 파일명은 표시하지 않음
            imageUri = this.url
        )
    }

    fun selectAdLocation(location: LocationEntity) {
        _uiState.update { currentState ->
            currentState.copy(selectedLocation = location)
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
     * 갤러리에서 선택한 이미지/동영상을 추가합니다.
     * 원본 파일명은 FileManager에서 자동으로 추출됩니다.
     * @param uri 갤러리에서 선택한 미디어 Uri
     */
    fun addAdImageFromUri(uri: Uri) {
        viewModelScope.launch {
            val selectedLocation = _uiState.value.selectedLocation ?: return@launch

            // 현재 해당 location의 이미지 개수를 가져와서 order 값 설정
            val currentImages = _uiState.value.adImages
            val nextOrder = currentImages.size + 1

            // Repository를 통해 DB에 저장 (원본 파일명은 FileManager에서 추출)
            adImageRepository.insertAdImageFromUri(uri, selectedLocation.id, nextOrder)
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

    /**
     * 미리보기 영역에 표시할 이미지를 선택합니다.
     * 현재 선택된 location별로 별도로 저장됩니다.
     * @param image 선택된 광고 이미지
     */
    fun selectPreviewImage(image: AdImageData) {
        val locationId = _uiState.value.selectedLocation?.id ?: return

        _uiState.update { currentState ->
            val updatedMap = currentState.selectedPreviewImagesByLocation.toMutableMap()
            updatedMap[locationId] = image
            currentState.copy(selectedPreviewImagesByLocation = updatedMap)
        }
    }
}