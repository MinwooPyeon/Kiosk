package com.pixelro.nenoonkiosk.feature.auth

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.Logo
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import com.pixelro.nenoonkiosk.ui.theme.NEURAL200
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LocationSignInScreen(
    updateIsSignedIn: (Boolean) -> Unit,
    loginViewModel: LoginViewModel,
    signInNavController: NavController,
    navController: NavController,
) {
    val isLandscape = isLandscape()

    if (isLandscape) {
        LandscapeLocationSignInScreen(
            onSignInSkip = {
                loginViewModel.locationSignInSkip(updateIsSignedIn)
                signInNavController.navigate(SignInScreenState.UserSignIn.name)
            },
            onSignIn = { id, password ->
                loginViewModel.locationSignIn(id, password, updateIsSignedIn)
            },
            onValidate = { id, password ->
                loginViewModel.validateLocationSignIn(id, password)
            },
            onNavigateToSettings = {
                navController.navigate(NavConstants.ROUTE_SETTINGS)
            },
            onSignInSuccess = {
                signInNavController.navigate(SignInScreenState.UserSignIn.name)
            }
        )
    } else {
        PortraitLocationSignInScreen(
            onSignInSkip = {
                loginViewModel.locationSignInSkip(updateIsSignedIn)
                signInNavController.navigate(SignInScreenState.UserSignIn.name)
            },
            onSignIn = { id, password ->
                loginViewModel.locationSignIn(id, password, updateIsSignedIn)
            },
            onValidate = { id, password ->
                loginViewModel.validateLocationSignIn(id, password)
            },
            onNavigateToSettings = {
                navController.navigate(NavConstants.ROUTE_SETTINGS)
            },
            onSignInSuccess = {
                signInNavController.navigate(SignInScreenState.UserSignIn.name)
            }
        )
    }
}

@Composable
private fun PortraitLocationSignInScreen(
    onSignInSkip: () -> Unit = {},
    onSignIn: suspend (String, String) -> Boolean = { _, _ -> true },
    onValidate: (String, String) -> Boolean = { _, _ -> true },
    onNavigateToSettings: () -> Unit = {},
    onSignInSuccess: () -> Unit = {},
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box {
        Column(
            modifier = Modifier
                .padding(40.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top,
        ) {
            Row(
                horizontalArrangement = Arrangement.End,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                        ) {
                            onNavigateToSettings()
                        },
                    painter = painterResource(id = R.drawable.icon_settings),
                    contentDescription = "",
                )
            }

            Spacer(modifier = Modifier.height(107.dp))

            Logo()

            Spacer(modifier = Modifier.height(50.dp))

            Text(
                text = StringProvider.getString(R.string.location_signin),
                color = NEURAL200,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 32.sp
            )

            Spacer(modifier = Modifier.height(40.dp))

            InputFields(
                id = id,
                onIdChange = { id = it },
                password = password,
                onPasswordChange = { password = it }
            )

            Spacer(modifier = Modifier.height(40.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.signin),
                onClick = {
                    if (!onValidate(id, password)) {
                        return@PrimaryButton
                    }
                    coroutineScope.launch(Dispatchers.Main) {
                        onSignIn(id, password).also { success ->
                            if (success) {
                                onSignInSuccess()
                            } else {
                                loginError = true
                            }
                        }
                    }
                },
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = StringProvider.getString(R.string.start_without_signin),
                fontSize = 38.sp,
                color = Color.Black,
                textDecoration = TextDecoration.Underline,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        onSignInSkip()
                    }
                    .padding(vertical = 8.dp)
            )
        }
    }
}

@Composable
private fun LandscapeLocationSignInScreen(
    onSignInSkip: () -> Unit = {},
    onSignIn: suspend (String, String) -> Boolean = { _, _ -> true },
    onValidate: (String, String) -> Boolean = { _, _ -> true },
    onNavigateToSettings: () -> Unit = {},
    onSignInSuccess: () -> Unit = {},
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Box(modifier = Modifier.fillMaxSize()) {
        // 설정 버튼
        Image(
            modifier = Modifier
                .padding(40.dp)
                .size(40.dp)
                .align(Alignment.TopEnd)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    onNavigateToSettings()
                },
            painter = painterResource(id = R.drawable.icon_settings),
            contentDescription = "",
        )

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 80.dp, vertical = 40.dp),
            horizontalArrangement = Arrangement.spacedBy(80.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 왼쪽: 로고 + 기관로그인 텍스트
            Column(
                modifier = Modifier
                    .weight(0.4f)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Logo()

                Spacer(modifier = Modifier.height(40.dp))

                Text(
                    text = StringProvider.getString(R.string.location_signin),
                    color = Color.Black,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 32.sp
                )
            }

            // 오른쪽: 입력 필드 + 버튼
            Column(
                modifier = Modifier
                    .weight(0.6f)
                    .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                InputFields(
                    id = id,
                    onIdChange = { id = it },
                    password = password,
                    onPasswordChange = { password = it },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Spacer(modifier = Modifier.height(40.dp))

                PrimaryButton(
                    text = StringProvider.getString(R.string.signin),
                    onClick = {
                        if (!onValidate(id, password)) {
                            return@PrimaryButton
                        }
                        coroutineScope.launch(Dispatchers.Main) {
                            onSignIn(id, password).also { success ->
                                if (success) {
                                    onSignInSuccess()
                                } else {
                                    loginError = true
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = StringProvider.getString(R.string.start_without_signin),
                    fontSize = 38.sp,
                    color = Color.Black,
                    textDecoration = TextDecoration.Underline,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onSignInSkip()
                        }
                        .padding(vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun InputFields(
    id: String,
    onIdChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        BasicTextField(
            value = id,
            onValueChange = onIdChange,
            textStyle = TextStyle(
                fontSize = 36.sp,
            ),
            decorationBox = { innerTextField ->
                Box(
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
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (id.isEmpty()) {
                        Text(
                            text = StringProvider.getString(R.string.id_input),
                            fontSize = 36.sp,
                            color = Color.LightGray,
                        )
                    }
                    innerTextField()
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
        )

        Spacer(modifier = Modifier.height(20.dp))

        BasicTextField(
            value = password,
            onValueChange = onPasswordChange,
            visualTransformation = PasswordVisualTransformation(),
            textStyle = TextStyle(
                fontSize = 36.sp,
            ),
            decorationBox = { innerTextField ->
                Box(
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
                    contentAlignment = Alignment.CenterStart,
                ) {
                    if (password.isEmpty()) {
                        Text(
                            text = StringProvider.getString(R.string.pw_input),
                            fontSize = 36.sp,
                            color = Color.LightGray,
                        )
                    }
                    innerTextField()
                }
            },
            keyboardOptions = KeyboardOptions(
                imeAction = ImeAction.Done,
            ),
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    name = "LocationSignIn - Portrait"
)
@Composable
private fun LocationSignInScreen_Preview_Portrait() {
    NenoonKioskTheme {
        PortraitLocationSignInScreen()
    }
}

@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    name = "LocationSignIn - Landscape"
)
@Composable
private fun LocationSignInScreen_Preview_Landscape() {
    NenoonKioskTheme {
        LandscapeLocationSignInScreen()
    }
}
