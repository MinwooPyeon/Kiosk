package com.pixelro.nenoonkiosk.feature.inspection.externaldevicelist

import android.speech.tts.TextToSpeech
import androidx.lifecycle.ViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.navigation.InspectionRoute
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.TTS
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import com.pixelro.nenoonkiosk.feature.inspection.externaldevice.ExternalDeviceInspectionListSideEffect
import com.pixelro.nenoonkiosk.feature.inspection.externaldevice.ExternalDeviceInspectionListUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

/**
 * 외부 장비 검사 목록 화면의 비즈니스 로직을 관리하는 ViewModel
 *
 * Orbit MVI 패턴을 사용하여 단방향 데이터 플로우를 구현합니다.
 * Navigator를 직접 주입받아 네비게이션을 처리합니다.
 */
@HiltViewModel
class ExternalDeviceInspectionListViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel(),
    ContainerHost<ExternalDeviceInspectionListUiState, Nothing> {

    override val container = container<ExternalDeviceInspectionListUiState, Nothing>(
        ExternalDeviceInspectionListUiState()
    )

    /**
     * 화면 초기화 시 TTS 안내 재생
     */
    fun initializeScreen() = intent {
        TTS.tts.stop()
        TTS.speechTTS(
            StringProvider.getString(R.string.select_test_tts),
            TextToSpeech.QUEUE_ADD
        )
    }

    /**
     * 검사 완료 상태 업데이트
     */
    fun updateInspectionStatus(
        isBloodPressureDone: Boolean,
        isGripStrengthDone: Boolean,
        isSenior: Boolean
    ) = intent {
        reduce {
            state.copy(
                isBloodPressureDone = isBloodPressureDone,
                isGripStrengthDone = isGripStrengthDone,
                isSenior = isSenior
            )
        }
    }

    /**
     * 사용자 이벤트 처리
     */
    fun onSideEffect(sideEffect: ExternalDeviceInspectionListSideEffect) {
        when (sideEffect) {
            is ExternalDeviceInspectionListSideEffect.OnInspectionSelected -> {
                handleInspectionSelection(sideEffect.inspectionType)
            }

            is ExternalDeviceInspectionListSideEffect.OnDialogDismissed -> {
                dismissDialog()
            }
        }
    }

    /**
     * 검사 선택 처리
     * 이미 완료된 검사인 경우 다이얼로그를 표시하고,
     * 미완료 검사인 경우 바로 검사 화면으로 이동
     */
    private fun handleInspectionSelection(inspectionType: InspectionType) = intent {
        val isDone = when (inspectionType) {
            InspectionType.BloodPressure -> state.isBloodPressureDone
            InspectionType.GripStrength -> state.isGripStrengthDone
            else -> false
        }

        if (isDone) {
            reduce {
                state.copy(
                    isDialogShowing = true,
                    selectedInspection = inspectionType
                )
            }
        } else {
            navigateToInspection(inspectionType)
        }
    }

    /**
     * 다이얼로그 닫기
     */
    private fun dismissDialog() = intent {
        reduce {
            state.copy(
                isDialogShowing = false,
                selectedInspection = InspectionType.None
            )
        }
    }

    /**
     * 뒤로 가기 (인트로 화면으로 이동)
     */
    fun navigateBack() = intent {
        navigator.navigateBack()
    }

    /**
     * 설정 화면으로 이동
     */
    fun navigateToSettings() = intent {
        navigator.navigate(Route.Settings)
    }

    /**
     * 검사 화면으로 이동
     */
    fun navigateToInspection(inspectionType: InspectionType) = intent {
        when (inspectionType) {
            InspectionType.BloodPressure -> {
                navigator.navigate(InspectionRoute.BloodPressure)
            }

            InspectionType.GripStrength -> {
                navigator.navigate(InspectionRoute.GripStrength)
            }

            else -> {}
        }
        dismissDialog()
    }

    /**
     * 설문 화면으로 돌아가기
     */
    fun navigateToSurvey() = intent {
        navigator.navigate(Route.Survey)
        dismissDialog()
    }
}
