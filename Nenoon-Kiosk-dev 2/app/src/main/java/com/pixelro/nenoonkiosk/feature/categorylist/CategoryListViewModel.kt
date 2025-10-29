package com.pixelro.nenoonkiosk.feature.categorylist

import androidx.lifecycle.ViewModel
import com.pixelro.nenoonkiosk.core.navigation.AdminRoute
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import com.pixelro.nenoonkiosk.core.navigation.TestRoute
import com.pixelro.nenoonkiosk.feature.inspection.pulmonaryFunction.PulmonaryFunctionTestResult
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

    fun navigateToEyeTest() = intent {
        navigator.navigate(TestRoute.TestContent)
    }

    fun navigateToExternalDeviceTestList() = intent {
        navigator.navigate(TestRoute.ExternalDeviceTestList)
    }

    fun navigateToPulmonaryTest(pid: Int) = intent {
        postSideEffect(
            CategoryListSideEffect.LaunchPulmonaryTest(
                pid = pid,
                height = 0,
                birthday = 0,
                weight = 0,
                gender = "m"
            )
        )
    }

    fun navigateToStrabismusTestList() = intent {
        navigator.navigate(TestRoute.StrabismusTestList)
    }

    fun navigateToDementiaTest() = intent {
        navigator.navigate(TestRoute.TestContent)
    }

    fun navigateToPrint() = intent {
        navigator.navigate(Route.ResultPrint)
    }

    fun navigateToAccountManagement() = intent {
        navigator.navigate(AdminRoute.AccountManagement)
    }

    fun navigateToPulmonaryTestResult(result: PulmonaryFunctionTestResult) = intent {
        navigator.navigate(TestRoute.TestResult)
    }

    fun navigateToContact() = intent {
        navigator.navigate(Route.Contact)
    }

    fun navigateToIntro() = intent {
        navigator.navigate(Route.Intro)
    }

    fun navigateToSettings() = intent {
        navigator.navigate(Route.Settings)
    }
}