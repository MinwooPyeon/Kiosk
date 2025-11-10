package com.pixelro.nenoonkiosk.feature.auth

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
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
import androidx.navigation.compose.rememberNavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.BackButtonHorizontal
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle as CoreTextStyle
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import kotlinx.coroutines.launch

@Composable
fun IdPasswordSignInScreen(
    updateIsSignedIn: (Boolean) -> Unit,
    loginViewModel: LoginViewModel,
    navController: NavController,
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current

    var passwordVisible by remember { mutableStateOf(false) }
    var signInError by remember { mutableStateOf(false) }
    var signingIn by remember { mutableStateOf(false) }

    val isLandscape = isLandscape()

    if (isLandscape) {
        LandscapeIdPasswordSignInScreen(
            id = id,
            onIdChange = { id = it },
            password = password,
            onPasswordChange = { password = it },
            passwordVisible = passwordVisible,
            onPasswordVisibleChange = { passwordVisible = it },
            signInError = signInError,
            signingIn = signingIn,
            onSignInClick = {
                coroutineScope.launch {
                    signingIn = true
                    loginViewModel.userSignIn(id, password, updateIsSignedIn).also { success ->
                        signInError = !success
                        signingIn = false
                    }
                }
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    } else {
        PortraitIdPasswordSignInScreen(
            id = id,
            onIdChange = { id = it },
            password = password,
            onPasswordChange = { password = it },
            passwordVisible = passwordVisible,
            onPasswordVisibleChange = { passwordVisible = it },
            signInError = signInError,
            signingIn = signingIn,
            onSignInClick = {
                coroutineScope.launch {
                    signingIn = true
                    loginViewModel.userSignIn(id, password, updateIsSignedIn).also { success ->
                        signInError = !success
                        signingIn = false
                    }
                }
            },
            onBackClick = {
                navController.popBackStack()
            }
        )
    }
}

@Composable
private fun PortraitIdPasswordSignInScreen(
    id: String,
    onIdChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    signInError: Boolean,
    signingIn: Boolean,
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
            stringResource(id = R.string.id_pw_sign_in_title),
            CoreTextStyle.Title
        )

        Spacer(modifier = Modifier.height(40.dp))

        InputTextField(
            value = id,
            onValueChange = onIdChange,
            label = stringResource(id = R.string.id_pw_sign_in_id_hint),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
        Spacer(modifier = Modifier.height(20.dp))

        InputTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = stringResource(id = R.string.id_pw_sign_in_pw_hint),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibleChange(!passwordVisible) }) {
                    Icon(
                        painter = painterResource(if (passwordVisible) R.drawable.icon_visibility_on else R.drawable.icon_visibility_off),
                        contentDescription = if (passwordVisible) {
                            stringResource(R.string.id_pw_sign_in_pw_hide)
                        } else {
                            stringResource(R.string.id_pw_sign_in_pw_show)
                        },
                    )
                }
            },
        )

        Spacer(modifier = Modifier.weight(1f))

        if (signingIn) {
            ProgressIndicator()
            Spacer(modifier = Modifier.weight(1f))
        }

        if (signInError) {
            StyledText(
                text = stringResource(id = R.string.toast_input_id_pw),
                style = CoreTextStyle.Error,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        PrimaryButton(
            text = stringResource(id = R.string.id_pw_sign_in_button),
            onClick = onSignInClick,
            enabled = id.isNotBlank() && password.isNotBlank(),
            modifier = Modifier.shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(10.dp)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        BackButtonHorizontal(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(10.dp)
                )
        )
    }
}

@Composable
private fun LandscapeIdPasswordSignInScreen(
    id: String,
    onIdChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    signInError: Boolean,
    signingIn: Boolean,
    onSignInClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        StyledText(
            stringResource(id = R.string.id_pw_sign_in_title),
            CoreTextStyle.Title
        )

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            modifier = Modifier.fillMaxWidth(0.6f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            InputTextField(
                value = id,
                onValueChange = onIdChange,
                label = stringResource(id = R.string.id_pw_sign_in_id_hint),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                modifier = Modifier.fillMaxWidth()
            )

            InputTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = stringResource(id = R.string.id_pw_sign_in_pw_hint),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done, keyboardType = KeyboardType.Password),
                trailingIcon = {
                    IconButton(onClick = { onPasswordVisibleChange(!passwordVisible) }) {
                        Icon(
                            painter = painterResource(if (passwordVisible) R.drawable.icon_visibility_on else R.drawable.icon_visibility_off),
                            contentDescription = if (passwordVisible) {
                                stringResource(R.string.id_pw_sign_in_pw_hide)
                            } else {
                                stringResource(R.string.id_pw_sign_in_pw_show)
                            },
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth()
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        if (signingIn) {
            ProgressIndicator()
        }

        if (signInError) {
            StyledText(
                text = stringResource(id = R.string.toast_input_id_pw),
                style = CoreTextStyle.Error,
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier.fillMaxWidth(0.6f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(15.dp)
        ) {
            PrimaryButton(
                text = stringResource(id = R.string.id_pw_sign_in_button),
                onClick = onSignInClick,
                enabled = id.isNotBlank() && password.isNotBlank(),
                modifier = Modifier.shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(10.dp)
                )
            )

            BackButtonHorizontal(
                onClick = onBackClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
            )
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
                            StyledText(label, CoreTextStyle.Hint)
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
                style = CoreTextStyle.InputError,
                modifier = Modifier.padding(start = 20.dp, top = 4.dp),
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    backgroundColor = 0xFFFFFFFF,
    name = "IdPasswordSignIn - Portrait"
)
@Composable
private fun IdPasswordSignInScreen_Preview_Portrait() {
    NenoonKioskTheme {
        PortraitIdPasswordSignInScreen(
            id = "",
            onIdChange = {},
            password = "",
            onPasswordChange = {},
            passwordVisible = false,
            onPasswordVisibleChange = {},
            signInError = false,
            signingIn = false,
            onSignInClick = {},
            onBackClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    backgroundColor = 0xFFFFFFFF,
    name = "IdPasswordSignIn - Landscape"
)
@Composable
private fun IdPasswordSignInScreen_Preview_Landscape() {
    NenoonKioskTheme {
        LandscapeIdPasswordSignInScreen(
            id = "",
            onIdChange = {},
            password = "",
            onPasswordChange = {},
            passwordVisible = false,
            onPasswordVisibleChange = {},
            signInError = false,
            signingIn = false,
            onSignInClick = {},
            onBackClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    backgroundColor = 0xFFFFFFFF,
    name = "IdPasswordSignIn - Landscape (Error)"
)
@Composable
private fun IdPasswordSignInScreen_Preview_Landscape_Error() {
    NenoonKioskTheme {
        LandscapeIdPasswordSignInScreen(
            id = "testuser",
            onIdChange = {},
            password = "wrong",
            onPasswordChange = {},
            passwordVisible = false,
            onPasswordVisibleChange = {},
            signInError = true,
            signingIn = false,
            onSignInClick = {},
            onBackClick = {}
        )
    }
}
