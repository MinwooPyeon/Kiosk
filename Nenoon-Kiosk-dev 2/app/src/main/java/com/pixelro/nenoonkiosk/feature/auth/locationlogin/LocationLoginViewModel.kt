package com.pixelro.nenoonkiosk.feature.auth.locationlogin

import androidx.lifecycle.ViewModel
import com.harang.data.repository.SignInRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class LocationLoginViewModel @Inject constructor(
    private val signInRepository: SignInRepository
) : ViewModel(), ContainerHost<LocationLoginState, LocationLoginSideEffect> {

    override val container: Container<LocationLoginState, LocationLoginSideEffect> =
        container(LocationLoginState())

    fun updateId(id: String) = intent {
        reduce {
            state.copy(id = id, loginError = false)
        }
    }

    fun updatePassword(password: String) = intent {
        reduce {
            state.copy(password = password, loginError = false)
        }
    }

    fun signIn() = intent {
        if (!validateLocationSignIn(state.id, state.password)) {
            return@intent
        }

        reduce {
            state.copy(isLoggingIn = true, loginError = false)
        }

        runCatching {
            signInRepository.locationSignIn(state.id, state.password)
        }.onSuccess { success ->
            reduce {
                state.copy(isLoggingIn = false)
            }
            if (success) {
                postSideEffect(LocationLoginSideEffect.LoginSuccess)
            } else {
                reduce {
                    state.copy(loginError = true)
                }
                postSideEffect(LocationLoginSideEffect.ShowToast("로그인에 실패했습니다"))
            }
        }.onFailure { exception ->
            reduce {
                state.copy(isLoggingIn = false, loginError = true)
            }
            postSideEffect(LocationLoginSideEffect.ShowToast("로그인 중 오류가 발생했습니다"))
        }
    }

    fun skipSignIn(updateIsSignedIn: (Boolean) -> Unit) = intent {
        runCatching {
            signInRepository.locationSignInSkip()
        }.onSuccess {
            updateIsSignedIn(false)
            postSideEffect(LocationLoginSideEffect.NavigateToUserSignIn)
        }.onFailure {
            postSideEffect(LocationLoginSideEffect.ShowToast("오류가 발생했습니다"))
        }
    }

    fun navigateToSettings() = intent {
        postSideEffect(LocationLoginSideEffect.NavigateToSettings)
    }

    private fun validateLocationSignIn(id: String, password: String): Boolean {
        return !(id.isBlank() || password.isBlank())
    }
}
