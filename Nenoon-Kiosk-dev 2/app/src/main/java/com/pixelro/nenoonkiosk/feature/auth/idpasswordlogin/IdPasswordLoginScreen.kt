package com.pixelro.nenoonkiosk.feature.auth.idpasswordlogin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.auth.SignInScreenState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun IdPasswordLoginRoute(
    navController: NavController,
    updateIsSignedIn: (Boolean) -> Unit,
    viewModel: IdPasswordLoginViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is IdPasswordLoginSideEffect.ShowToast -> {
                // 토스트 표시 처리
            }

            is IdPasswordLoginSideEffect.LoginSuccess -> {
                updateIsSignedIn(true)
            }

            is IdPasswordLoginSideEffect.LoginFailed -> {
                // 실패 메시지는 state.errorMessage에서 처리
            }

            is IdPasswordLoginSideEffect.NavigateBack -> {
                navController.popBackStack(SignInScreenState.UserSignIn.name, false)
            }
        }
    }

    IdPasswordLoginScreen(
        state = state,
        onUserIdChange = { userId ->
            viewModel.updateUserId(userId)
        },
        onPasswordChange = { password ->
            viewModel.updateUserPassword(password)
        },
        onSignInClick = {
            viewModel.signIn()
        },
        onBackClick = {
            viewModel.navigateBack()
        },
        onErrorDismiss = {
            viewModel.clearError()
        }
    )
}

@Composable
fun IdPasswordLoginScreen(
    state: IdPasswordLoginState,
    onUserIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onBackClick: () -> Unit,
    onErrorDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(40.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        StyledText(
            text = StringProvider.getString(R.string.id_password_signin_title),
            style = TextStyle.Title,
        )

        Spacer(modifier = Modifier.weight(1f))

        StyledTextField(
            value = state.userId,
            onValueChange = onUserIdChange,
            label = StringProvider.getString(R.string.signin_id_label),
            enabled = !state.isLoading,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        )

        Spacer(modifier = Modifier.height(20.dp))

        StyledTextField(
            value = state.userPassword,
            onValueChange = onPasswordChange,
            label = StringProvider.getString(R.string.signin_password_label),
            enabled = !state.isLoading,
            visualTransformation = PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (state.errorMessage != null) {
            StyledText(
                text = state.errorMessage,
                style = TextStyle.Error,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (state.isLoading) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(20.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        Column {
            PrimaryButton(
                text = StringProvider.getString(R.string.signin_button_text),
                onClick = onSignInClick,
                enabled = state.isLoginEnabled && !state.isLoading,
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.back),
                onClick = onBackClick,
                enabled = !state.isLoading,
            )
        }
    }
}