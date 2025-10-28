package com.pixelro.nenoonkiosk.feature.splash

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import com.pixelro.nenoonkiosk.core.permission.PermissionChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.S)
@HiltViewModel
class SplashViewModel @Inject constructor(
    private val navigator: Navigator,
    private val permissionChecker: PermissionChecker
) : ViewModel(), ContainerHost<SplashUiState, SplashSideEffect> {

    override val container: Container<SplashUiState, SplashSideEffect> =
        container(SplashUiState.Loading)

    init {
        checkPermissionsAfterDelay()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    private fun checkPermissionsAfterDelay() = intent {
        delay(3000)

        reduce { SplashUiState.Loaded(AppConstants.APP_VERSION) }

        val allGranted = permissionChecker.areAllPermissionsGranted()

        if (allGranted) {
            navigator.navigateAndClearBackStack(Route.SignIn)
            postSideEffect(SplashSideEffect.NavigateToSignIn)
        } else {
            navigator.navigateAndClearBackStack(Route.Permission)
            postSideEffect(SplashSideEffect.NavigateToPermission)
        }
    }
}
