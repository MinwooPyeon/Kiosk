package com.pixelro.nenoonkiosk.feature.auth.signup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
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
fun SignUpRoute(
    navController: NavController,
    viewModel: SignUpViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is SignUpSideEffect.ShowToast -> {
                // 토스트 표시
            }
            is SignUpSideEffect.SignUpSuccess -> {
                // 회원가입 성공
            }
            is SignUpSideEffect.SignUpFailed -> {
                // 회원가입 실패
            }
            is SignUpSideEffect.NavigateToFaceEnrollment -> {
                // 얼굴 등록 화면으로 이동
            }
            is SignUpSideEffect.NavigateBack -> {
                navController.popBackStack(SignInScreenState.UserSignIn.name, false)
            }
        }
    }

    SignUpScreen(
        state = state,
        onIdChange = { viewModel.updateId(it) },
        onPasswordChange = { viewModel.updatePassword(it) },
        onNameChange = { viewModel.updateName(it) },
        onEmailChange = { viewModel.updateEmail(it) },
        onConfirmPasswordChange = { viewModel.updateConfirmPassword(it) },
        onPasswordVisibilityToggle = { viewModel.togglePasswordVisibility() },
        onConfirmPasswordVisibilityToggle = { viewModel.toggleConfirmPasswordVisibility() },
        onSignUpClick = { viewModel.signUp() },
        onFaceEnrollmentClick = { viewModel.navigateToFaceEnrollment() },
        onBackClick = { viewModel.navigateBack() }
    )
}

@Composable
fun SignUpScreen(
    state: SignUpState,
    onIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onConfirmPasswordVisibilityToggle: () -> Unit,
    onSignUpClick: () -> Unit,
    onFaceEnrollmentClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (state.signupSuccess) Arrangement.Center else Arrangement.Top,
    ) {
        if (!state.signupSuccess) {
            StyledText(
                StringProvider.getString(R.string.user_signup_title),
                com.pixelro.nenoonkiosk.core.ui.TextStyle.Title
            )

            if (state.isSigningUp) {
                Spacer(modifier = Modifier.weight(0.5f))
                ProgressIndicator()
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                InputTextField(
                    value = state.id,
                    onValueChange = onIdChange,
                    label = StringProvider.getString(R.string.user_signup_input_id_hint),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                Spacer(modifier = Modifier.height(20.dp))

                InputTextField(
                    value = state.name,
                    onValueChange = onNameChange,
                    label = StringProvider.getString(R.string.user_signup_input_name_hint),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                Spacer(modifier = Modifier.height(20.dp))

                InputTextField(
                    value = state.email,
                    onValueChange = onEmailChange,
                    label = StringProvider.getString(R.string.user_signup_input_email_hint),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Email,
                    ),
                    isError = state.emailError != null,
                    errorMessage = state.emailError,
                )
                Spacer(modifier = Modifier.height(20.dp))

                InputTextField(
                    value = state.password,
                    onValueChange = onPasswordChange,
                    label = StringProvider.getString(R.string.user_signup_input_password_hint),
                    visualTransformation = if (state.passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Next,
                        keyboardType = KeyboardType.Password,
                    ),
                    trailingIcon = {
                        IconButton(onClick = onPasswordVisibilityToggle) {
                            Icon(
                                painter = painterResource(
                                    if (state.passwordVisible) R.drawable.icon_visibility_on else R.drawable.icon_visibility_off
                                ),
                                contentDescription = StringProvider.getString(
                                    if (state.passwordVisible) R.string.user_signup_password_hide_cd else R.string.user_signup_password_show_cd
                                ),
                            )
                        }
                    },
                    isError = state.passwordError != null,
                    errorMessage = state.passwordError,
                )
                Spacer(modifier = Modifier.height(20.dp))

                InputTextField(
                    value = state.confirmPassword,
                    onValueChange = onConfirmPasswordChange,
                    label = StringProvider.getString(R.string.user_signup_input_confirm_password_hint),
                    visualTransformation = if (state.confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(
                        imeAction = ImeAction.Done,
                        keyboardType = KeyboardType.Password,
                    ),
                    trailingIcon = {
                        IconButton(onClick = onConfirmPasswordVisibilityToggle) {
                            Icon(
                                painter = painterResource(
                                    if (state.confirmPasswordVisible) R.drawable.icon_visibility_on else R.drawable.icon_visibility_off
                                ),
                                contentDescription = StringProvider.getString(
                                    if (state.confirmPasswordVisible) R.string.user_signup_password_hide_cd else R.string.user_signup_password_show_cd
                                ),
                            )
                        }
                    },
                    isError = state.confirmPasswordError != null,
                    errorMessage = state.confirmPasswordError,
                )
            }

            Spacer(modifier = Modifier.weight(0.5f))

            if (state.errorMessage != null) {
                StyledText(state.errorMessage, com.pixelro.nenoonkiosk.core.ui.TextStyle.Error)
                Spacer(modifier = Modifier.height(20.dp))
            }

            PrimaryButton(
                text = StringProvider.getString(R.string.user_signup_signup_and_qr_button),
                onClick = onSignUpClick,
                enabled = state.id.isNotBlank() &&
                        state.password.isNotBlank() &&
                        state.name.isNotBlank() &&
                        state.passwordError == null &&
                        state.confirmPasswordError == null &&
                        (state.email.isBlank() || state.emailError == null),
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (state.signupSuccess && state.generatedQrBitmap != null) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier
                    .weight(4f)
                    .padding(20.dp)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    bitmap = state.generatedQrBitmap.asImageBitmap(),
                    contentDescription = StringProvider.getString(R.string.user_signup_qr_image_cd),
                    modifier = Modifier
                        .size(400.dp)
                        .padding(bottom = 32.dp),
                )
                StyledText(StringProvider.getString(R.string.user_signup_qr_description))
            }

            Spacer(modifier = Modifier.weight(1f))

            PrimaryButton(
                text = StringProvider.getString(R.string.user_signup_face_enrollment_button),
                onClick = onFaceEnrollmentClick,
                enabled = !state.isFaceEnrollmentReady,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        PrimaryButton(
            text = StringProvider.getString(R.string.user_signup_back_button),
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
    isError: Boolean = false,
    errorMessage: String? = null,
) {
    Column(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 30.sp),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(80.dp)
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = colorResource(if (isError) R.color.error else R.color.gray2),
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
        if (isError && errorMessage != null) {
            StyledText(
                text = errorMessage,
                style = com.pixelro.nenoonkiosk.core.ui.TextStyle.InputError,
                modifier = Modifier.padding(start = 20.dp, top = 4.dp),
            )
        }
    }
}
