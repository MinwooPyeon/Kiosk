package com.pixelro.nenoonkiosk.feature.auth.login

import androidx.lifecycle.ViewModel
import com.pixelro.nenoonkiosk.core.navigation.AdminRoute
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import com.pixelro.nenoonkiosk.core.navigation.SignInRoute
import com.pixelro.nenoonkiosk.core.navigation.TermsOfServiceRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val navigator: Navigator
) : ViewModel(), ContainerHost<LoginState, LoginSideEffect> {

    override val container: Container<LoginState, LoginSideEffect> =
        container(LoginState())

    fun navigateToIdPassword() = intent {
        navigator.navigate(SignInRoute.IdPassword)
    }

    fun navigateToQR() = intent {
        navigator.navigate(SignInRoute.QR)
    }

    fun navigateToFaceId() = intent {
        navigator.navigate(SignInRoute.FaceId)
    }

    fun navigateToSignUpTerms() = intent {
        navigator.navigate(TermsOfServiceRoute.SignUp)
    }

    fun navigateToBluetoothManagement() = intent {
        navigator.navigate(Route.BTDeviceManagement)
    }

    fun navigateToAdminPage() = intent {
        navigator.navigate(AdminRoute.AdminPage)
    }

    fun navigateToSettings() = intent {
        navigator.navigate(Route.Settings)
    }

    fun userSignInSkip(updateIsSignedIn: (Boolean) -> Unit) = intent {
        updateIsSignedIn(true)
        navigator.navigate(TermsOfServiceRoute.Base)
    }
}
