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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.auth.SignInScreenState
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(
    updateIsSignedIn: (Boolean) -> Unit,
    toFaceEnrollmentScreen: () -> Unit,
    loginViewModel: LoginViewModel,
    navController: NavController,
) {
    var id by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var confirmPassword by rememberSaveable { mutableStateOf("") }
    var isFaceEnrollmentTermsOfServiceAccepted by rememberSaveable { mutableStateOf(false) }
    val isFaceEnrollmentDataReady by loginViewModel.isFaceEnrollmentDataReady.collectAsState()
    val generatedQrBitmap by loginViewModel.accountQrCode.collectAsState()
    var isSigningUp by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    var signupSuccess by remember { mutableStateOf(false) }

    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        signupSuccess = false
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = if (signupSuccess) Arrangement.Center else Arrangement.Top,
    ) {
        if (!signupSuccess) {
            StyledText(StringProvider.getString(R.string.user_signup_title), com.pixelro.nenoonkiosk.core.ui.TextStyle.Title)

            if (isSigningUp) {
                Spacer(modifier = Modifier.weight(0.5f))
                ProgressIndicator()
                Spacer(modifier = Modifier.height(20.dp))
            } else {
                InputTextField(
                    value = id,
                    onValueChange = { id = it },
                    label = StringProvider.getString(R.string.user_signup_input_id_hint),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                Spacer(modifier = Modifier.height(20.dp))

                InputTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = StringProvider.getString(R.string.user_signup_input_name_hint),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                )
                Spacer(modifier = Modifier.height(20.dp))

                InputTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError =
                            if (it.isNotBlank()) loginViewModel.validateEmail(it) else null
                    },
                    label = StringProvider.getString(R.string.user_signup_input_email_hint),
                    keyboardOptions =
                        KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email,
                        ),
                    isError = emailError != null,
                    errorMessage = emailError,
                )
                Spacer(modifier = Modifier.height(20.dp))

                InputTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = loginViewModel.validatePassword(it)
                        if (confirmPassword.isNotBlank()) {
                            confirmPasswordError =
                                loginViewModel.validateConfirmPassword(it, confirmPassword)
                        }
                    },
                    label = StringProvider.getString(R.string.user_signup_input_password_hint),
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions =
                        KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Password,
                        ),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                painter =
                                    painterResource(
                                        if (passwordVisible) R.drawable.icon_visibility_on else R.drawable.icon_visibility_off,
                                    ),
                                contentDescription =
                                    StringProvider.getString(
                                        if (passwordVisible) R.string.user_signup_password_hide_cd else R.string.user_signup_password_show_cd,
                                    ),
                            )
                        }
                    },
                    isError = passwordError != null,
                    errorMessage = passwordError,
                )
                Spacer(modifier = Modifier.height(20.dp))

                InputTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        confirmPasswordError = loginViewModel.validateConfirmPassword(password, it)
                    },
                    label = StringProvider.getString(R.string.user_signup_input_confirm_password_hint),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions =
                        KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Password,
                        ),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                painter =
                                    painterResource(
                                        if (confirmPasswordVisible) R.drawable.icon_visibility_on else R.drawable.icon_visibility_off,
                                    ),
                                contentDescription =
                                    StringProvider.getString(
                                        if (confirmPasswordVisible) R.string.user_signup_password_hide_cd else R.string.user_signup_password_show_cd,
                                    ),
                            )
                        }
                    },
                    isError = confirmPasswordError != null,
                    errorMessage = confirmPasswordError,
                )
            }
            Spacer(modifier = Modifier.weight(0.5f))

            if (errorMessage != null) {
                StyledText(errorMessage!!, com.pixelro.nenoonkiosk.core.ui.TextStyle.Error)
                Spacer(modifier = Modifier.height(20.dp))
            }

            PrimaryButton(
                text = StringProvider.getString(R.string.user_signup_signup_and_qr_button),
                onClick = {
                    errorMessage = null
                    isSigningUp = true
                    coroutineScope.launch {
                        delay(1000L)
                        val result =
                            loginViewModel.userSignUp(
                                id = id,
                                password = password,
                                name = name,
                                email = email,
                            )
                        if (result != null) {
                            loginViewModel.generateAndPrintQrCode(id, password)
                            signupSuccess = true
                        } else {
                            errorMessage = StringProvider.getString(R.string.user_signup_error_signup_failed)
                            signupSuccess = false
                        }
                        isSigningUp = false
                    }
                },
                enabled =
                    id.isNotBlank() &&
                        password.isNotBlank() &&
                        name.isNotBlank() &&
                        passwordError == null &&
                        confirmPasswordError == null &&
                        (email.isBlank() || emailError == null),
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (signupSuccess && generatedQrBitmap != null) {
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier =
                    Modifier
                        .weight(4f)
                        .padding(20.dp)
                        .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Image(
                    bitmap = generatedQrBitmap!!.asImageBitmap(),
                    contentDescription = StringProvider.getString(R.string.user_signup_qr_image_cd),
                    modifier =
                        Modifier
                            .size(400.dp)
                            .padding(bottom = 32.dp),
                )
                StyledText(StringProvider.getString(R.string.user_signup_qr_description))
            }

            PrimaryButton(
                text = StringProvider.getString(R.string.user_signup_face_enrollment_button),
                onClick = {
                    toFaceEnrollmentScreen()
                },
                enabled = !isFaceEnrollmentDataReady,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        PrimaryButton(
            text = StringProvider.getString(R.string.user_signup_back_button),
            onClick = {
                navController.popBackStack(SignInScreenState.UserSignIn.name, false)
            },
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
            textStyle =
                TextStyle(
                    fontSize = 30.sp,
                ),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            decorationBox = { innerTextField ->
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(80.dp)
                            .border(
                                border =
                                    BorderStroke(
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
