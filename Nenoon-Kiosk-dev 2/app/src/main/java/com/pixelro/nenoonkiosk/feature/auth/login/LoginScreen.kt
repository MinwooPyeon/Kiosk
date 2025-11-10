package com.pixelro.nenoonkiosk.feature.auth.login

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.Logo
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.SecondaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.auth.SignInScreenState

@Composable
fun LoginScreen(
    loginViewModel: LoginViewModel,
    signInNavController: NavController,
    navController: NavController,
) {
    LoginContent(
        onBluetoothSettingsClick = {
            navController.navigate(NavConstants.ROUTE_BT_DEVICE_MANAGEMENT)
        },
        onAdminPageClick = {
            navController.navigate(NavConstants.ROUTE_ADMIN_PAGE)
        },
        onSettingsClick = {
            navController.navigate(NavConstants.ROUTE_SETTINGS)
        },
        onIdPasswordSignInClick = {
            signInNavController.navigate(SignInScreenState.IdPassword.name)
        },
        onQrSignInClick = {
            signInNavController.navigate(SignInScreenState.QR.name)
        },
        onFaceRecognitionClick = {
            signInNavController.navigate(SignInScreenState.FaceId.name)
        },
        onStartWithoutSignInClick = {
            loginViewModel.userSignInSkip()
            navController.navigate(NavConstants.ROUTE_TERMS_OF_SERVICE)
        },
        onSignUpClick = {
            signInNavController.navigate(SignInScreenState.SignUpTermsOfService.name)
        },
        showAdminPage = DebugConstants.ENABLE_ADMIN_PAGE
    )
}

@Composable
private fun LoginContent(
    onBluetoothSettingsClick: () -> Unit,
    onAdminPageClick: () -> Unit,
    onSettingsClick: () -> Unit,
    onIdPasswordSignInClick: () -> Unit,
    onQrSignInClick: () -> Unit,
    onFaceRecognitionClick: () -> Unit,
    onStartWithoutSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    showAdminPage: Boolean
) {
    val isLandscape = isLandscape()

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Top,
    ) {
        TopIconBar(
            onBluetoothSettingsClick = onBluetoothSettingsClick,
            onAdminPageClick = onAdminPageClick,
            onSettingsClick = onSettingsClick,
            showAdminPage = showAdminPage
        )

        if (isLandscape) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(0.8f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Logo()
                    Spacer(modifier = Modifier.height(30.dp))
                    StyledText(
                        text = stringResource(R.string.user_sign_in),
                        style = TextStyle.Message,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(modifier = Modifier.width(40.dp))

                LoginButtonsArea(
                    onIdPasswordSignInClick = onIdPasswordSignInClick,
                    onQrSignInClick = onQrSignInClick,
                    onFaceRecognitionClick = onFaceRecognitionClick,
                    onStartWithoutSignInClick = onStartWithoutSignInClick,
                    onSignUpClick = onSignUpClick,
                    isLandscapeMode = true,
                    showTitle = false,
                    modifier = Modifier.weight(1.2f)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Spacer(modifier = Modifier.weight(1f))

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Logo()
                    Spacer(modifier = Modifier.height(30.dp))
                    StyledText(
                        text = stringResource(R.string.user_sign_in),
                        style = TextStyle.Message,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                LoginButtonsArea(
                    onIdPasswordSignInClick = onIdPasswordSignInClick,
                    onQrSignInClick = onQrSignInClick,
                    onFaceRecognitionClick = onFaceRecognitionClick,
                    onStartWithoutSignInClick = onStartWithoutSignInClick,
                    onSignUpClick = onSignUpClick,
                    isLandscapeMode = false,
                    showTitle = false,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.weight(0.3f))
            }
        }
    }
}

@Composable
private fun TopIconBar(
    onBluetoothSettingsClick: () -> Unit,
    onAdminPageClick: () -> Unit,
    onSettingsClick: () -> Unit,
    showAdminPage: Boolean
) {
    Row(
        horizontalArrangement = Arrangement.End,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 40.dp, vertical = 40.dp),
    ) {
        Image(
            modifier = Modifier
                .size(40.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    onBluetoothSettingsClick()
                },
            painter = painterResource(id = R.drawable.bluetooth_settings_icon),
            contentDescription = ""
        )

        Spacer(modifier = Modifier.width(40.dp))

        Image(
            modifier = Modifier
                .size(40.dp)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() },
                ) {
                    onSettingsClick()
                },
            painter = painterResource(id = R.drawable.icon_settings),
            contentDescription = ""
        )
    }
}

@Composable
private fun LoginButtonsArea(
    onIdPasswordSignInClick: () -> Unit,
    onQrSignInClick: () -> Unit,
    onFaceRecognitionClick: () -> Unit,
    onStartWithoutSignInClick: () -> Unit,
    onSignUpClick: () -> Unit,
    isLandscapeMode: Boolean,
    showTitle: Boolean,
    modifier: Modifier = Modifier
) {
    val userSignIn = stringResource(R.string.user_sign_in)
    val idPwSignIn = stringResource(R.string.id_pw_sign_in)
    val qrLogin = stringResource(R.string.default_sign_in_qr_login)
    val faceRecognition = stringResource(R.string.default_sign_in_face_recognition)
    val startWithoutSignIn = stringResource(R.string.start_without_signin)
    val signUp = stringResource(R.string.default_sign_in_sign_up)

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (showTitle) {
            StyledText(
                text = userSignIn,
                style = TextStyle.Message,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(if (isLandscapeMode) 20.dp else 30.dp))
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(if (isLandscapeMode) 16.dp else 20.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PrimaryButton(
                text = idPwSignIn,
                onClick = onIdPasswordSignInClick,
                iconDrawable = R.drawable.password_logo,
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
            )

            PrimaryButton(
                text = qrLogin,
                onClick = onQrSignInClick,
                iconDrawable = R.drawable.qr_logo,
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
            )

            PrimaryButton(
                text = faceRecognition,
                onClick = onFaceRecognitionClick,
                iconDrawable = R.drawable.face_logo,
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 8.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.height(if (isLandscapeMode) 20.dp else 30.dp))

        SecondaryButton(
            text = signUp,
            onClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(10.dp)
                )
        )

        Spacer(modifier = Modifier.height(if (isLandscapeMode) 16.dp else 20.dp))

        StyledText(
            text = startWithoutSignIn,
            style = TextStyle.Message,
            fontWeight = FontWeight.Bold,
            textDecoration = TextDecoration.Underline,
            modifier = Modifier.clickable(
                indication = null,
                interactionSource = remember { MutableInteractionSource() }
            ) {
                onStartWithoutSignInClick()
            }
        )
    }
}

@Preview(
    name = "Tablet Portrait",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 800,
    heightDp = 1280
)
@Composable
fun PreviewLoginPortrait() {
    LoginContent(
        onBluetoothSettingsClick = {},
        onAdminPageClick = {},
        onSettingsClick = {},
        onIdPasswordSignInClick = {},
        onQrSignInClick = {},
        onFaceRecognitionClick = {},
        onStartWithoutSignInClick = {},
        onSignUpClick = {},
        showAdminPage = true
    )
}

@Preview(
    name = "Tablet Landscape",
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 1280,
    heightDp = 800
)
@Composable
fun PreviewLoginLandscape() {
    LoginContent(
        onBluetoothSettingsClick = {},
        onAdminPageClick = {},
        onSettingsClick = {},
        onIdPasswordSignInClick = {},
        onQrSignInClick = {},
        onFaceRecognitionClick = {},
        onStartWithoutSignInClick = {},
        onSignUpClick = {},
        showAdminPage = true
    )
}
