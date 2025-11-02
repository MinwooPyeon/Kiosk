package com.pixelro.nenoonkiosk.feature.inspection.eyeinspectionlist

import androidx.lifecycle.ViewModel
import com.pixelro.nenoonkiosk.core.navigation.InspectionRoute
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import com.pixelro.nenoonkiosk.feature.inspection.InspectionType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class EyeInspectionListViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel(), ContainerHost<EyeInspectionListUiState, Nothing> {

    override val container = container<EyeInspectionListUiState, Nothing>(
        EyeInspectionListUiState()
    )

    fun setIsSenior(isSenior: Boolean) = intent {
        reduce { state.copy(isSenior = isSenior) }
    }

    fun setDescriptionShowing(isShowing: Boolean) = intent {
        reduce { state.copy(isDescriptionShowing = isShowing) }
    }

    fun setTestDoneStatus(
        presbyopiaDone: Boolean,
        shortVisualAcuityDone: Boolean,
        amslerGridDone: Boolean,
        mChartDone: Boolean
    ) = intent {
        reduce {
            state.copy(
                isPresbyopiaDone = presbyopiaDone,
                isShortVisualAcuityDone = shortVisualAcuityDone,
                isAmslerGridDone = amslerGridDone,
                isMChartDone = mChartDone
            )
        }
    }

    fun onTestSelected(type: InspectionType) = intent {
        val isTestDone = when (type) {
            InspectionType.ShortDistanceVisualAcuity -> state.isShortVisualAcuityDone
            InspectionType.Presbyopia -> state.isPresbyopiaDone
            InspectionType.AmslerGrid -> state.isAmslerGridDone
            InspectionType.MChart -> state.isMChartDone
            else -> false
        }

        if (isTestDone) {
            reduce {
                state.copy(
                    isDialogShowing = true,
                    selectedTest = type
                )
            }
        } else {
            navigateToTest(type)
        }
    }

    fun dismissDialog() = intent {
        reduce { state.copy(isDialogShowing = false, selectedTest = InspectionType.None) }
    }

    fun navigateToTestFromDialog() = intent {
        val type = state.selectedTest
        reduce { state.copy(isDialogShowing = false, selectedTest = InspectionType.None) }
        navigateToTest(type)
    }

    private fun navigateToTest(type: InspectionType) = intent {
        when (type) {
            InspectionType.ShortDistanceVisualAcuity -> navigator.navigate(InspectionRoute.ShortVisualAcuity)
            InspectionType.Presbyopia -> navigator.navigate(InspectionRoute.Presbyopia)
            InspectionType.AmslerGrid -> navigator.navigate(InspectionRoute.AmslerGrid)
            InspectionType.MChart -> navigator.navigate(InspectionRoute.MChart)
            else -> {}
        }
    }

    fun navigateToIntro() = intent {
        navigator.navigate(Route.Intro)
    }

    fun navigateToSettings() = intent {
        navigator.navigate(Route.Settings)
    }

    fun startDescriptionBlinking() = intent {
        while (true) {
            delay(5000)
            repeat(3) {
                setDescriptionShowing(false)
                delay(250)
                setDescriptionShowing(true)
                delay(250)
            }
        }

    }
}
