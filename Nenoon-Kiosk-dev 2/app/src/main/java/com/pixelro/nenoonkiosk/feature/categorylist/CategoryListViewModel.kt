package com.pixelro.nenoonkiosk.feature.categorylist

import androidx.lifecycle.ViewModel
import com.pixelro.nenoonkiosk.core.navigation.AdminRoute
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import com.pixelro.nenoonkiosk.core.navigation.InspectionRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class CategoryListViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel(), ContainerHost<CategoryListUiState, CategoryListSideEffect> {

    override val container: Container<CategoryListUiState, CategoryListSideEffect> =
        container(CategoryListUiState())

    fun setPid(pid: Int) = intent {
        reduce { state.copy(pid = pid) }
    }

    fun startDescriptionAnimation() = intent {
        postSideEffect(CategoryListSideEffect.StopTts)

        while (true) {
            delay(5000)
            repeat(3) {
                reduce { state.copy(isDescriptionShowing = false) }
                delay(250)
                reduce { state.copy(isDescriptionShowing = true) }
                delay(250)
            }
        }
    }

    // 눈검사
    fun navigateToEyeTest() = intent {
        navigator.navigate(InspectionRoute.EyeInspectionList)
    }

    // 외부 연결(혈압, 악력)
    fun navigateToExternalDeviceTestList() = intent {
        navigator.navigate(InspectionRoute.ExternalDeviceInspectionList)
    }

    // 사시
    fun navigateToStrabismusTestList() = intent {
        navigator.navigate(InspectionRoute.StrabismusInspectionList)
    }

    // 치매
    fun navigateToDementiaTest() = intent {
        navigator.navigate(InspectionRoute.DementiaInspection)
    }

    // 출력?
    fun navigateToPrint() = intent {
        navigator.navigate(Route.ResultPrint)
    }

    // 계정 관리
    fun navigateToAccountManagement() = intent {
        navigator.navigate(AdminRoute.AccountManagement)
    }

    // 셋팅화면
    fun navigateToSettings() = intent {
        navigator.navigate(Route.Settings)
    }
}
