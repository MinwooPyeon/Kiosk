package com.pixelro.nenoonkiosk.feature.license

import androidx.lifecycle.ViewModel
import com.pixelro.nenoonkiosk.core.manager.LicenseManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

/**
 * 라이선스 인증 화면 ViewModel
 */
@HiltViewModel
class LicenseViewModel @Inject constructor(
    private val licenseManager: LicenseManager
) : ViewModel() {

    private val _password = MutableStateFlow("")
    val password: StateFlow<String> = _password.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage.asStateFlow()

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    /**
     * 숫자 입력
     */
    fun onNumberClick(number: String) {
        if (_password.value.length < 10) { // 최대 10자리
            _password.value += number
        }
    }

    /**
     * 백스페이스
     */
    fun onBackspaceClick() {
        if (_password.value.isNotEmpty()) {
            _password.value = _password.value.dropLast(1)
        }
    }

    /**
     * 전체 삭제
     */
    fun onClearClick() {
        _password.value = ""
        _errorMessage.value = null
    }

    /**
     * 인증 시도
     */
    fun onAuthenticateClick() {
        if (_password.value.isBlank()) {
            _errorMessage.value = "비밀번호를 입력하세요"
            return
        }

        _isLoading.value = true
        _errorMessage.value = null

        // 잠금 상태 확인
        val lockoutRemaining = licenseManager.getLockoutTimeRemaining()
        if (lockoutRemaining > 0) {
            val minutes = (lockoutRemaining / (60 * 1000)).toInt()
            _errorMessage.value = "너무 많이 시도했습니다. ${minutes}분 후에 다시 시도하세요"
            _password.value = ""
            _isLoading.value = false
            return
        }

        // 라이선스 활성화 시도
        val success = licenseManager.activateLicense(_password.value)

        if (success) {
            _isAuthenticated.value = true
            _errorMessage.value = null
        } else {
            // 잠금되었는지 다시 확인
            val newLockoutRemaining = licenseManager.getLockoutTimeRemaining()
            if (newLockoutRemaining > 0) {
                val minutes = (newLockoutRemaining / (60 * 1000)).toInt()
                _errorMessage.value = "너무 많이 시도했습니다. ${minutes}분 후에 다시 시도하세요"
            } else {
                _errorMessage.value = "인증 실패. 비밀번호를 확인하세요"
            }
            _password.value = ""
        }

        _isLoading.value = false
    }

    /**
     * 에러 메시지 초기화
     */
    fun clearError() {
        _errorMessage.value = null
    }
}