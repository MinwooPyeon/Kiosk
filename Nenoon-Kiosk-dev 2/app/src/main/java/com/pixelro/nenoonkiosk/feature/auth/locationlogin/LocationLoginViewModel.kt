package com.pixelro.nenoonkiosk.feature.auth.locationlogin

import androidx.annotation.OptIn
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import com.harang.data.repository.SignInRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.Route
import com.pixelro.nenoonkiosk.core.navigation.SignInRoute
import com.pixelro.nenoonkiosk.core.navigation.TestRoute
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LocationLoginViewModel @Inject constructor(
    private val signInRepository: SignInRepository,
    private val navigator: Navigator
) : ViewModel(), ContainerHost<LocationLoginState, LocationLoginSideEffect> {

    override val container: Container<LocationLoginState, LocationLoginSideEffect> =
        container(LocationLoginState())

    fun updateId(id: String) = intent {
        reduce { state.copy(id = id, loginError = false) }
    }

    fun updatePassword(password: String) = intent {
        reduce { state.copy(password = password, loginError = false) }
    }

    fun signIn() = intent {
        if (!validateLocationSignIn(state.id, state.password)) {
            return@intent
        }

        runCatching {
            val result = signInRepository.locationSignIn(state.id, state.password)

            if (result != null && result.data["success"] as Boolean) {
                signInRepository.updateLocationId((result.data["pid"] as Double).toInt())
                signInRepository.updateScreenSaverVideoURI(result.data["video"] as String)
                true
            } else {
                false
            }
        }.onSuccess { success ->
            if (success) {
                postSideEffect(LocationLoginSideEffect.LoginSuccess)
                navigator.navigate(TestRoute.CategoryList)
            }
        }
    }

    @OptIn(UnstableApi::class)
    fun skipSignIn(updateIsSignedIn: (Boolean) -> Unit) {
        viewModelScope.launch(Dispatchers.IO) {
            signInRepository.updateLocationId(AppConstants.DEFAULT_LOCATION_ID)
            signInRepository.updateScreenSaverVideoURI(
                RawResourceDataSource.buildRawResourceUri(R.raw.ad_sub).toString()
            )
        }
        intent {
            updateIsSignedIn(true)
            navigator.navigate(SignInRoute.UserSignIn)
        }
    }

    fun navigateToSettings() = intent {
        navigator.navigate(Route.Settings)
    }

    private fun validateLocationSignIn(id: String, password: String): Boolean {
        return !(id.isBlank() || password.isBlank())
    }
}
