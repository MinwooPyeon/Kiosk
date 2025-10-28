package com.pixelro.nenoonkiosk.feature.auth.idpasswordlogin

import android.util.Log
import androidx.lifecycle.ViewModel
import com.harang.data.repository.SignInRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.navigation.Navigator
import com.pixelro.nenoonkiosk.core.navigation.SignInRoute
import com.pixelro.nenoonkiosk.core.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class IdPasswordLoginViewModel @Inject constructor(
    private val signInRepository: SignInRepository,
    private val navigator: Navigator
) : ViewModel(), ContainerHost<IdPasswordLoginState, IdPasswordLoginSideEffect> {

    override val container: Container<IdPasswordLoginState, IdPasswordLoginSideEffect> =
        container(IdPasswordLoginState())

    fun updateUserId(userId: String) = intent {
        reduce {
            state.copy(
                userId = userId,
                isLoginEnabled = userId.isNotBlank() && state.userPassword.isNotBlank(),
                errorMessage = null
            )
        }
    }

    fun updateUserPassword(password: String) = intent {
        reduce {
            state.copy(
                userPassword = password,
                isLoginEnabled = state.userId.isNotBlank() && password.isNotBlank(),
                errorMessage = null
            )
        }
    }

    fun togglePasswordVisibility() = intent {
        reduce {
            state.copy(passwordVisible = !state.passwordVisible)
        }
    }

    fun signIn() = intent {
        if (state.userId.isBlank() || state.userPassword.isBlank()) {
            reduce {
                state.copy(errorMessage = StringProvider.getString(R.string.toast_input_id_pw))
            }
            return@intent
        }

        reduce {
            state.copy(isLoading = true, errorMessage = null)
        }

        runCatching {
            if (AppConstants.MANAGE_USERS_INTERNALLY) {
                signInLocally(state.userId, state.userPassword)
            } else {
                signInWithServer(state.userId, state.userPassword)
            }
        }.onSuccess { success ->
            reduce { state.copy(isLoading = false) }

            if (success) {
                postSideEffect(IdPasswordLoginSideEffect.LoginSuccess)
            } else {
                reduce {
                    state.copy(errorMessage = StringProvider.getString(R.string.toast_input_id_pw))
                }
            }
        }.onFailure { e ->
            Log.e("IdPasswordLoginVM", "Sign in error: ${e.message}", e)
            reduce {
                state.copy(
                    isLoading = false,
                    errorMessage = StringProvider.getString(R.string.toast_input_id_pw)
                )
            }
        }
    }

    private fun signInLocally(userId: String, password: String): Boolean {
        val user = SharedPreferencesManager.checkUserAccount(userId, password)
        return user != null
    }

    private suspend fun signInWithServer(userId: String, password: String): Boolean {
        val signedInUserData = signInRepository.userSignIn(userId, password)

        return if (signedInUserData?.accessToken != null) {
            val newUserData = signInRepository.getUserProfile(signedInUserData.accessToken!!)
            newUserData != null
        } else {
            false
        }
    }

    fun navigateBack() = intent {
        navigator.navigate(SignInRoute.UserSignIn)
    }

    fun clearError() = intent {
        reduce { state.copy(errorMessage = null) }
    }
}
