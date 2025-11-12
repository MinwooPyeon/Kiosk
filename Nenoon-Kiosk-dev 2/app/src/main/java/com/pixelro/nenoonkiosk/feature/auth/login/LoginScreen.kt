package com.pixelro.nenoonkiosk.feature.auth.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import com.pixelro.nenoonkiosk.ui.theme.Gray

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
                    .padding(horizontal = 60.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(0.7f),
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
                    modifier = Modifier.weight(1.3f)
                )
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 60.dp, vertical = 40.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Logo()
                    Spacer(modifier = Modifier.height(50.dp))
                    StyledText(
                        text = stringResource(R.string.user_sign_in),
                        style = TextStyle.Message,
                        fontWeight = FontWeight.SemiBold,
                    )
                }

                Spacer(modifier = Modifier.weight(1.2f))

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

                Spacer(modifier = Modifier.weight(0.8f))
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
            .padding(horizontal = 20.dp, vertical = 20.dp),
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

        Spacer(modifier = Modifier.width(60.dp))

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
    val idPwSignIn = stringResource(R.string.password_login_button)
    val qrLogin = stringResource(R.string.qr_login_button)
    val faceRecognition = stringResource(R.string.face_login_button)
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

            Spacer(modifier = Modifier.height(if (isLandscapeMode) 40.dp else 50.dp))
        }

        Row(
            horizontalArrangement = Arrangement.spacedBy(if (isLandscapeMode) 30.dp else 40.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            PrimaryButton(
                text = idPwSignIn,
                onClick = onIdPasswordSignInClick,
                iconDrawable = R.drawable.password_logo,
                modifier = Modifier
                    .weight(1f)
                    .shadow(
                        elevation = 2.dp,
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
                        elevation = 2.dp,
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
                        elevation = 2.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
            )
        }

        Spacer(modifier = Modifier.height(if (isLandscapeMode) 30.dp else 50.dp))

        SecondaryButton(
            text = signUp,
            onClick = onSignUpClick,
            modifier = Modifier
                .fillMaxWidth()
                .shadow(
                    elevation = 2.dp,
                    shape = RoundedCornerShape(10.dp)
                )
        )

        Spacer(modifier = Modifier.height(if (isLandscapeMode) 30.dp else 60.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .width(IntrinsicSize.Max)
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onStartWithoutSignInClick()
                }
        ) {
            StyledText(
                text = startWithoutSignIn,
                style = TextStyle.Message,
                fontWeight = FontWeight.Bold,
                color = Gray
            )
            Spacer(modifier = Modifier.height(2.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(Gray)
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 800,
    heightDp = 1280,
    name = "Portrait - Standard Tablet"
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
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 1280,
    heightDp = 800,
    name = "Landscape - Standard Tablet"
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

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 1920,
    heightDp = 1080,
    name = "Landscape - 32 inch Full HD"
)
@Composable
fun PreviewLogin32InchFullHD() {
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
