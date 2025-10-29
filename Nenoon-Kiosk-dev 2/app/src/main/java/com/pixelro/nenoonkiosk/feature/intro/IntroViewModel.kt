package com.pixelro.nenoonkiosk.feature.intro

import androidx.lifecycle.ViewModel
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class IntroViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel(), ContainerHost<IntroUiState, Nothing> {

    override val container: Container<IntroUiState, Nothing> =
        container(IntroUiState())

    fun navigateToSurvey() = intent {
        navigator.navigate(Route.Survey)
    }

    fun navigateToSettings() = intent {
        navigator.navigate(Route.Settings)
    }
}
