package com.pixelro.nenoonkiosk.feature.auth.idpasswordlogin

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
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
                // 실패 처리
            }

            is IdPasswordLoginSideEffect.NavigateBack -> {
                navController.popBackStack(SignInScreenState.UserSignIn.name, false)
            }
        }
    }

    IdPasswordLoginScreen(
        state = state,
        onUserIdChange = { viewModel.updateUserId(it) },
        onPasswordChange = { viewModel.updateUserPassword(it) },
        onPasswordVisibilityToggle = { viewModel.togglePasswordVisibility() },
        onSignInClick = { viewModel.signIn() },
        onBackClick = { viewModel.navigateBack() }
    )
}

@Composable
fun IdPasswordLoginScreen(
    state: IdPasswordLoginState,
    onUserIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onSignInClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        StyledText(
            StringProvider.getString(R.string.id_pw_sign_in_title),
            com.pixelro.nenoonkiosk.core.ui.TextStyle.Title
        )

        Spacer(modifier = Modifier.height(40.dp))

        InputTextField(
            value = state.userId,
            onValueChange = onUserIdChange,
            label = StringProvider.getString(R.string.id_pw_sign_in_id_hint),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.height(20.dp))

        InputTextField(
            value = state.userPassword,
            onValueChange = onPasswordChange,
            label = StringProvider.getString(R.string.id_pw_sign_in_pw_hint),
            visualTransformation = if (state.passwordVisible) {
                VisualTransformation.None
            } else {
                PasswordVisualTransformation()
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
                keyboardType = KeyboardType.Password
            ),
            trailingIcon = {
                IconButton(onClick = onPasswordVisibilityToggle) {
                    Icon(
                        painter = painterResource(
                            if (state.passwordVisible) {
                                R.drawable.icon_visibility_on
                            } else {
                                R.drawable.icon_visibility_off
                            }
                        ),
                        contentDescription = if (state.passwordVisible) {
                            StringProvider.getString(R.string.id_pw_sign_in_pw_hide)
                        } else {
                            StringProvider.getString(R.string.id_pw_sign_in_pw_show)
                        }
                    )
                }
            },
            enabled = !state.isLoading
        )

        Spacer(modifier = Modifier.weight(0.5f))

        if (state.isLoading) {
            ProgressIndicator()
            Spacer(modifier = Modifier.weight(0.5f))
        }

        if (state.errorMessage != null) {
            StyledText(
                text = state.errorMessage,
                style = com.pixelro.nenoonkiosk.core.ui.TextStyle.Error,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        PrimaryButton(
            text = StringProvider.getString(R.string.id_pw_sign_in_button),
            onClick = onSignInClick,
            enabled = state.isLoginEnabled && !state.isLoading,
        )

        Spacer(modifier = Modifier.height(20.dp))

        PrimaryButton(
            text = StringProvider.getString(R.string.button_back),
            onClick = onBackClick,
        )
    }
}

@Composable
private fun InputTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
    trailingIcon: @Composable (() -> Unit)? = null,
    enabled: Boolean = true
) {
    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = TextStyle(fontSize = 30.sp),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        enabled = enabled,
        decorationBox = { innerTextField ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(80.dp)
                    .border(
                        border = BorderStroke(
                            width = 1.dp,
                            color = Color(0xffc3c3c3),
                        ),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .padding(start = 20.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier.weight(1f),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (value.isEmpty()) {
                        StyledText(label, com.pixelro.nenoonkiosk.core.ui.TextStyle.Hint)
                    }
                    innerTextField()
                }
                trailingIcon?.invoke()
            }
        },
    )
}
