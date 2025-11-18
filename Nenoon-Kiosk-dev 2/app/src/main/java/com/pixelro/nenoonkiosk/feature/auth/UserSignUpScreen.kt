package com.pixelro.nenoonkiosk.feature.auth

import android.graphics.Bitmap
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.BackButtonHorizontal
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun UserSignUpScreen(
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
    val isFaceEnrollmentDataReady by loginViewModel.isFaceEnrollmentDataReady.collectAsState()
    val generatedQrBitmap by loginViewModel.accountQrCode.collectAsState()
    var isSigningUp by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var signupSuccess by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }

    val coroutineScope = rememberCoroutineScope()

    val signupFailedError = stringResource(R.string.user_signup_error_signup_failed)

    LaunchedEffect(Unit) {
        signupSuccess = false
    }

    UserSignUpContent(
        id = id,
        password = password,
        name = name,
        email = email,
        confirmPassword = confirmPassword,
        passwordVisible = passwordVisible,
        confirmPasswordVisible = confirmPasswordVisible,
        isSigningUp = isSigningUp,
        signupSuccess = signupSuccess,
        generatedQrBitmap = generatedQrBitmap,
        isFaceEnrollmentDataReady = isFaceEnrollmentDataReady,
        errorMessage = errorMessage,
        passwordError = passwordError,
        confirmPasswordError = confirmPasswordError,
        emailError = emailError,
        onIdChange = { id = it },
        onPasswordChange = {
            password = it
            passwordError = loginViewModel.validatePassword(it)
            if (confirmPassword.isNotBlank()) {
                confirmPasswordError = loginViewModel.validateConfirmPassword(it, confirmPassword)
            }
        },
        onNameChange = { name = it },
        onEmailChange = {
            email = it
            emailError = if (it.isNotBlank()) loginViewModel.validateEmail(it) else null
        },
        onConfirmPasswordChange = {
            confirmPassword = it
            confirmPasswordError = loginViewModel.validateConfirmPassword(password, it)
        },
        onPasswordVisibilityToggle = { passwordVisible = !passwordVisible },
        onConfirmPasswordVisibilityToggle = { confirmPasswordVisible = !confirmPasswordVisible },
        onSignUpClick = {
            errorMessage = null
            isSigningUp = true
            coroutineScope.launch {
                delay(1000L)
                val result = loginViewModel.userSignUp(
                    id = id,
                    password = password,
                    name = name,
                    email = email,
                )
                if (result != null) {
                    loginViewModel.generateAndPrintQrCode(id, password)
                    signupSuccess = true
                } else {
                    errorMessage = signupFailedError
                    signupSuccess = false
                }
                isSigningUp = false
            }
        },
        onFaceEnrollmentClick = toFaceEnrollmentScreen,
        onBackClick = {
            navController.popBackStack(SignInScreenState.UserSignIn.name, false)
        }
    )
}

@Composable
private fun UserSignUpContent(
    id: String,
    password: String,
    name: String,
    email: String,
    confirmPassword: String,
    passwordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    isSigningUp: Boolean,
    signupSuccess: Boolean,
    generatedQrBitmap: Bitmap?,
    isFaceEnrollmentDataReady: Boolean,
    errorMessage: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    emailError: String?,
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
    val isLandscape = isLandscape()

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        NenoonTopBar(
            title = stringResource(R.string.user_signup_title),
            showBackButton = false
        )

        if (isLandscape) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                if (signupSuccess && generatedQrBitmap != null) {
                    Row(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(40.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Image(
                                bitmap = generatedQrBitmap.asImageBitmap(),
                                contentDescription = stringResource(R.string.user_signup_qr_image_cd),
                                modifier = Modifier.size(280.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            StyledText(text = stringResource(R.string.user_signup_qr_description))
                        }

                        Spacer(modifier = Modifier.width(20.dp))

                        Column(
                            modifier = Modifier.weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            PrimaryButton(
                                text = stringResource(R.string.user_signup_face_enrollment_button),
                                onClick = onFaceEnrollmentClick,
                                enabled = !isFaceEnrollmentDataReady,
                                modifier = Modifier.shadow(
                                    elevation = 2.dp,
                                    shape = RoundedCornerShape(10.dp)
                                )
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            BackButtonHorizontal(
                                onClick = onBackClick,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .shadow(
                                        elevation = 2.dp,
                                        shape = RoundedCornerShape(10.dp)
                                    )
                            )
                        }
                    }
                } else {
                    UserSignUpForm(
                        id = id,
                        password = password,
                        name = name,
                        email = email,
                        confirmPassword = confirmPassword,
                        passwordVisible = passwordVisible,
                        confirmPasswordVisible = confirmPasswordVisible,
                        isSigningUp = isSigningUp,
                        signupSuccess = signupSuccess,
                        isFaceEnrollmentDataReady = isFaceEnrollmentDataReady,
                        errorMessage = errorMessage,
                        passwordError = passwordError,
                        confirmPasswordError = confirmPasswordError,
                        emailError = emailError,
                        onIdChange = onIdChange,
                        onPasswordChange = onPasswordChange,
                        onNameChange = onNameChange,
                        onEmailChange = onEmailChange,
                        onConfirmPasswordChange = onConfirmPasswordChange,
                        onPasswordVisibilityToggle = onPasswordVisibilityToggle,
                        onConfirmPasswordVisibilityToggle = onConfirmPasswordVisibilityToggle,
                        onSignUpClick = onSignUpClick,
                        onFaceEnrollmentClick = onFaceEnrollmentClick,
                        onBackClick = onBackClick,
                        isLandscapeMode = true,
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 40.dp, vertical = 20.dp)
                    )
                }
            }
        } else {
            UserSignUpForm(
                id = id,
                password = password,
                name = name,
                email = email,
                confirmPassword = confirmPassword,
                passwordVisible = passwordVisible,
                confirmPasswordVisible = confirmPasswordVisible,
                isSigningUp = isSigningUp,
                signupSuccess = signupSuccess,
                isFaceEnrollmentDataReady = isFaceEnrollmentDataReady,
                errorMessage = errorMessage,
                passwordError = passwordError,
                confirmPasswordError = confirmPasswordError,
                emailError = emailError,
                onIdChange = onIdChange,
                onPasswordChange = onPasswordChange,
                onNameChange = onNameChange,
                onEmailChange = onEmailChange,
                onConfirmPasswordChange = onConfirmPasswordChange,
                onPasswordVisibilityToggle = onPasswordVisibilityToggle,
                onConfirmPasswordVisibilityToggle = onConfirmPasswordVisibilityToggle,
                onSignUpClick = onSignUpClick,
                onFaceEnrollmentClick = onFaceEnrollmentClick,
                onBackClick = onBackClick,
                isLandscapeMode = false,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(40.dp)
            )
        }
    }
}

@Composable
private fun UserSignUpForm(
    id: String,
    password: String,
    name: String,
    email: String,
    confirmPassword: String,
    passwordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    isSigningUp: Boolean,
    signupSuccess: Boolean,
    isFaceEnrollmentDataReady: Boolean,
    errorMessage: String?,
    passwordError: String?,
    confirmPasswordError: String?,
    emailError: String?,
    onIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibilityToggle: () -> Unit,
    onConfirmPasswordVisibilityToggle: () -> Unit,
    onSignUpClick: () -> Unit,
    onFaceEnrollmentClick: () -> Unit,
    onBackClick: () -> Unit,
    isLandscapeMode: Boolean,
    modifier: Modifier = Modifier
) {
    val idHint = stringResource(R.string.user_signup_input_id_hint)
    val nameHint = stringResource(R.string.user_signup_input_name_hint)
    val emailHint = stringResource(R.string.user_signup_input_email_hint)
    val passwordHint = stringResource(R.string.user_signup_input_password_hint)
    val confirmPasswordHint = stringResource(R.string.user_signup_input_confirm_password_hint)
    val signUpButton = stringResource(R.string.user_signup_signup_and_qr_button)
    val faceEnrollmentButton = stringResource(R.string.user_signup_face_enrollment_button)
    val passwordShowCd = stringResource(R.string.user_signup_password_show_cd)
    val passwordHideCd = stringResource(R.string.user_signup_password_hide_cd)

    Column(
        modifier = modifier
            .imePadding()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        if (!signupSuccess) {
            if (isSigningUp) {
                Spacer(modifier = Modifier.height(100.dp))
                ProgressIndicator()
                Spacer(modifier = Modifier.height(100.dp))
            } else {
                Column(
                    verticalArrangement = Arrangement.spacedBy(if (isLandscapeMode) 20.dp else 25.dp)
                ) {
                    InputTextField(
                        value = id,
                        onValueChange = onIdChange,
                        label = idHint,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        inputHeight = if (isLandscapeMode) 55.dp else 65.dp
                    )

                    InputTextField(
                        value = name,
                        onValueChange = onNameChange,
                        label = nameHint,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                        inputHeight = if (isLandscapeMode) 55.dp else 65.dp
                    )

                    InputTextField(
                        value = email,
                        onValueChange = onEmailChange,
                        label = emailHint,
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Email,
                        ),
                        isError = emailError != null,
                        errorMessage = emailError,
                        inputHeight = if (isLandscapeMode) 55.dp else 65.dp
                    )

                    InputTextField(
                        value = password,
                        onValueChange = onPasswordChange,
                        label = passwordHint,
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Next,
                            keyboardType = KeyboardType.Password,
                        ),
                        trailingIcon = {
                            IconButton(onClick = onPasswordVisibilityToggle) {
                                Icon(
                                    painter = painterResource(
                                        if (passwordVisible) R.drawable.icon_visibility_on else R.drawable.icon_visibility_off,
                                    ),
                                    contentDescription = if (passwordVisible) passwordHideCd else passwordShowCd,
                                )
                            }
                        },
                        isError = passwordError != null,
                        errorMessage = passwordError,
                        inputHeight = if (isLandscapeMode) 55.dp else 65.dp
                    )

                    InputTextField(
                        value = confirmPassword,
                        onValueChange = onConfirmPasswordChange,
                        label = confirmPasswordHint,
                        visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        keyboardOptions = KeyboardOptions(
                            imeAction = ImeAction.Done,
                            keyboardType = KeyboardType.Password,
                        ),
                        trailingIcon = {
                            IconButton(onClick = onConfirmPasswordVisibilityToggle) {
                                Icon(
                                    painter = painterResource(
                                        if (confirmPasswordVisible) R.drawable.icon_visibility_on else R.drawable.icon_visibility_off,
                                    ),
                                    contentDescription = if (confirmPasswordVisible) passwordHideCd else passwordShowCd,
                                )
                            }
                        },
                        isError = confirmPasswordError != null,
                        errorMessage = confirmPasswordError,
                        inputHeight = if (isLandscapeMode) 55.dp else 65.dp
                    )
                }
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                StyledText(
                    text = errorMessage,
                    style = com.pixelro.nenoonkiosk.core.ui.TextStyle.Error
                )
            }

            Spacer(modifier = Modifier.height(if (isLandscapeMode) 20.dp else 30.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (isLandscapeMode) 10.dp else 15.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                PrimaryButton(
                    text = signUpButton,
                    onClick = onSignUpClick,
                    enabled = id.isNotBlank() &&
                            password.isNotBlank() &&
                            name.isNotBlank() &&
                            passwordError == null &&
                            confirmPasswordError == null &&
                            (email.isBlank() || emailError == null),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isLandscapeMode) 70.dp else 80.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                )

                if (signupSuccess && !isLandscapeMode) {
                    PrimaryButton(
                        text = faceEnrollmentButton,
                        onClick = onFaceEnrollmentClick,
                        enabled = !isFaceEnrollmentDataReady,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isLandscapeMode) 70.dp else 80.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(10.dp)
                            )
                    )
                }

                BackButtonHorizontal(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isLandscapeMode) 70.dp else 80.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        } else if (signupSuccess) {
            Spacer(modifier = Modifier.height(100.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(if (isLandscapeMode) 10.dp else 15.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (!isLandscapeMode) {
                    PrimaryButton(
                        text = faceEnrollmentButton,
                        onClick = onFaceEnrollmentClick,
                        enabled = !isFaceEnrollmentDataReady,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (isLandscapeMode) 70.dp else 80.dp)
                            .shadow(
                                elevation = 2.dp,
                                shape = RoundedCornerShape(10.dp)
                            )
                    )
                }

                BackButtonHorizontal(
                    onClick = onBackClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(if (isLandscapeMode) 70.dp else 80.dp)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                )
            }

            Spacer(modifier = Modifier.height(40.dp))
        }
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
    inputHeight: androidx.compose.ui.unit.Dp = 65.dp
) {
    Column(modifier = modifier) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            textStyle = TextStyle(fontSize = 24.sp),
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            decorationBox = { innerTextField ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(inputHeight)
                        .border(
                            border = BorderStroke(
                                width = 1.dp,
                                color = colorResource(if (isError) R.color.error else R.color.gray2),
                            ),
                            shape = RoundedCornerShape(8.dp),
                        )
                        .padding(start = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier = Modifier.weight(1f),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (value.isEmpty()) {
                            StyledText(
                                text = label,
                                style = com.pixelro.nenoonkiosk.core.ui.TextStyle.Hint
                            )
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
                modifier = Modifier.padding(start = 14.dp, top = 2.dp),
            )
        }
    }
}

@Preview(
    name = "Tablet Portrait",
    showBackground = true,
    widthDp = 800,
    backgroundColor = 0xFFFFFFFF,
    heightDp = 1280
)
@Composable
private fun PreviewUserSignUpPortrait() {
    UserSignUpContent(
        id = "",
        password = "",
        name = "",
        email = "",
        confirmPassword = "",
        passwordVisible = false,
        confirmPasswordVisible = false,
        isSigningUp = false,
        signupSuccess = false,
        generatedQrBitmap = null,
        isFaceEnrollmentDataReady = false,
        errorMessage = null,
        passwordError = null,
        confirmPasswordError = null,
        emailError = null,
        onIdChange = {},
        onPasswordChange = {},
        onNameChange = {},
        onEmailChange = {},
        onConfirmPasswordChange = {},
        onPasswordVisibilityToggle = {},
        onConfirmPasswordVisibilityToggle = {},
        onSignUpClick = {},
        onFaceEnrollmentClick = {},
        onBackClick = {}
    )
}

@Preview(
    name = "Tablet Landscape",
    showBackground = true,
    widthDp = 1280,
    backgroundColor = 0xFFFFFFFF,
    heightDp = 800
)
@Composable
private fun PreviewUserSignUpLandscape() {
    UserSignUpContent(
        id = "",
        password = "",
        name = "",
        email = "",
        confirmPassword = "",
        passwordVisible = false,
        confirmPasswordVisible = false,
        isSigningUp = false,
        signupSuccess = false,
        generatedQrBitmap = null,
        isFaceEnrollmentDataReady = false,
        errorMessage = null,
        passwordError = null,
        confirmPasswordError = null,
        emailError = null,
        onIdChange = {},
        onPasswordChange = {},
        onNameChange = {},
        onEmailChange = {},
        onConfirmPasswordChange = {},
        onPasswordVisibilityToggle = {},
        onConfirmPasswordVisibilityToggle = {},
        onSignUpClick = {},
        onFaceEnrollmentClick = {},
        onBackClick = {}
    )
}
