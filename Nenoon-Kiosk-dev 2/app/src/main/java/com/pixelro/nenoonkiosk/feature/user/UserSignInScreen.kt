package com.pixelro.nenoonkiosk.feature.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.core.ui.Logo
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle

@Composable
fun UserSignInScreen(
    signInViewModel: SignInViewModel,
    signInNavController: NavController,
    navController: NavController,
) {

    Column(
        verticalArrangement = Arrangement.Top
    ) {

        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 40.dp, top = 40.dp)
        ) {
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        navController.navigate(NavConstants.ROUTE_BT_DEVICE_MANAGEMENT)
                    },
                painter = painterResource(id = R.drawable.bluetooth_settings_icon),
                contentDescription = ""
            )

            if (DebugConstants.ENABLE_ADMIN_PAGE) {

                Spacer(
                    modifier = Modifier
                        .width(40.dp)
                )

                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            navController.navigate(NavConstants.ROUTE_ADMIN_PAGE)
                        },
                    painter = painterResource(id = R.drawable.data_icon),
                    contentDescription = ""
                )
            }

            Spacer(
                modifier = Modifier
                    .width(40.dp)
            )

            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        navController.navigate(NavConstants.ROUTE_SETTINGS)
                    },
                painter = painterResource(id = R.drawable.icon_settings),
                contentDescription = ""
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp, bottom = 40.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(modifier = Modifier.weight(1f))
            Logo()
            Spacer(modifier = Modifier.weight(1f))

            StyledText(
                text = StringProvider.getString(R.string.user_sign_in),
                style = TextStyle.Message,
                fontWeight = FontWeight.SemiBold,
            )

            Spacer(modifier = Modifier.height(40.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.id_pw_sign_in),
                onClick = {
                    signInNavController.navigate(SignInScreenState.IdPassword.name)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.default_sign_in_qr_login),
                onClick = {
                    signInNavController.navigate(SignInScreenState.QR.name)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.default_sign_in_face_recognition),
                onClick = {
                    signInNavController.navigate(SignInScreenState.FaceId.name)
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.start_without_signin),
                onClick = {
                    signInViewModel.userSignInSkip()
                    navController.navigate(NavConstants.ROUTE_TERMS_OF_SERVICE)
                }
            )

            Spacer(modifier = Modifier.weight(0.5f))

            PrimaryButton(
                text = StringProvider.getString(R.string.default_sign_in_sign_up),
                onClick = {
                    signInNavController.navigate(SignInScreenState.SignUpTermsOfService.name)
                }
            )
        }
    }
}