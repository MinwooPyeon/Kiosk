package com.pixelro.nenoonkiosk.feature.admin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class AdminDashboardViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AdminDashboardUiState())
    val uiState: StateFlow<AdminDashboardUiState> = _uiState.asStateFlow()

    init {
        // 초기 더미 데이터
        _uiState.update { currentState ->
            currentState.copy(
                adImages = List(7) { index ->
                    AdImageData(
                        id = "image_$index",
                        fileName = "사진이름_$index.jpg",
                        imageUri = null
                    )
                }
            )
        }
    }

    fun selectTab(tab: AdminTab) {
        _uiState.update { currentState ->
            currentState.copy(selectedTab = tab)
        }
    }

    fun selectAdLocation(location: AdLocation) {
        _uiState.update { currentState ->
            currentState.copy(selectedAdLocation = location)
        }
    }

    fun deleteAdImage(imageId: String) {
        _uiState.update { currentState ->
            currentState.copy(
                adImages = currentState.adImages.filter { it.id != imageId }
            )
        }
    }

    fun addAdImage(fileName: String, imageUri: String?) {
        val newImage = AdImageData(
            id = "image_${System.currentTimeMillis()}",
            fileName = fileName,
            imageUri = imageUri
        )
        _uiState.update { currentState ->
            currentState.copy(
                adImages = currentState.adImages + newImage
            )
        }
    }

    fun reorderAdImage(fromIndex: Int, toIndex: Int) {
        _uiState.update { currentState ->
            val newList = currentState.adImages.toMutableList()
            val item = newList.removeAt(fromIndex)
            newList.add(toIndex, item)
            currentState.copy(adImages = newList)
        }
    }
}