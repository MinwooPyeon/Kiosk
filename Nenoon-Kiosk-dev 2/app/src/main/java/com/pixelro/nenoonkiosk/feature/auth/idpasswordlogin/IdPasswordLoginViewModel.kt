package com.pixelro.nenoonkiosk.feature.auth.idpasswordlogin

import android.util.Log
import androidx.lifecycle.ViewModel
import com.harang.data.repository.SignInRepository
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.manager.SharedPreferencesManager
import com.pixelro.nenoonkiosk.core.util.StringProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.orbitmvi.orbit.Container
import org.orbitmvi.orbit.ContainerHost
import org.orbitmvi.orbit.viewmodel.container
import javax.inject.Inject

@HiltViewModel
class IdPasswordLoginViewModel @Inject constructor(
    private val signInRepository: SignInRepository
) : ViewModel(), ContainerHost<IdPasswordLoginState, IdPasswordLoginSideEffect> {

    override val container: Container<IdPasswordLoginState, IdPasswordLoginSideEffect> =
        container(IdPasswordLoginState())

    fun updateUserId(userId: String) = intent {
        reduce {
            state.copy(
                userId = userId,
                isLoginEnabled = userId.isNotBlank() && state.userPassword.isNotBlank()
            )
        }
    }

    fun updateUserPassword(password: String) = intent {
        reduce {
            state.copy(
                userPassword = password,
                isLoginEnabled = state.userId.isNotBlank() && password.isNotBlank()
            )
        }
    }

    fun signIn() = intent {
        if (state.userId.isBlank() || state.userPassword.isBlank()) {
            reduce {
                state.copy(
                    errorMessage = StringProvider.getString(R.string.signin_vm_empty_fields_error)
                )
            }
            postSideEffect(
                IdPasswordLoginSideEffect.LoginFailed(
                    StringProvider.getString(R.string.signin_vm_empty_fields_error)
                )
            )
            return@intent
        }

        reduce {
            state.copy(
                isLoading = true,
                errorMessage = null
            )
        }

        try {
            val success = if (AppConstants.MANAGE_USERS_INTERNALLY) {
                signInLocally(state.userId, state.userPassword)
            } else {
                signInWithServer(state.userId, state.userPassword)
            }

            if (success) {
                reduce { state.copy(isLoading = false) }
                postSideEffect(IdPasswordLoginSideEffect.LoginSuccess)
            } else {
                val errorMsg =
                    StringProvider.getString(R.string.signin_vm_invalid_credentials_error)
                reduce {
                    state.copy(
                        isLoading = false,
                        errorMessage = errorMsg
                    )
                }
                postSideEffect(IdPasswordLoginSideEffect.LoginFailed(errorMsg))
            }
        } catch (e: Exception) {
            Log.e("IdPasswordLoginVM", "Sign in error: ${e.message}", e)
            val errorMsg = StringProvider.getString(R.string.signin_vm_login_failed_error)
            reduce {
                state.copy(
                    isLoading = false,
                    errorMessage = errorMsg
                )
            }
            postSideEffect(IdPasswordLoginSideEffect.LoginFailed(errorMsg))
        }
    }

    private fun signInLocally(userId: String, password: String): Boolean {
        val storedUsers = SharedPreferencesManager.getStoredUsers()
        val user = storedUsers.find { it.id == userId && it.password == password }

        if (user != null) {
            SharedPreferencesManager.putCurrentUser(user)
            return true
        }
        return false
    }

    private suspend fun signInWithServer(userId: String, password: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val signedInUserData = signInRepository.userSignIn(userId, password)

                if (signedInUserData?.accessToken != null) {
                    val newUserData =
                        signInRepository.getUserProfile(signedInUserData.accessToken!!)
                    if (newUserData != null) {
                        SharedPreferencesManager.putCurrentUser(newUserData)
                        true
                    } else {
                        false
                    }
                } else {
                    false
                }
            } catch (e: Exception) {
                Log.e("IdPasswordLoginVM", "Server sign in failed: ${e.message}", e)
                false
            }
        }
    }

    fun navigateBack() = intent {
        postSideEffect(IdPasswordLoginSideEffect.NavigateBack)
    }

    fun clearError() = intent {
        reduce { state.copy(errorMessage = null) }
    }
}
