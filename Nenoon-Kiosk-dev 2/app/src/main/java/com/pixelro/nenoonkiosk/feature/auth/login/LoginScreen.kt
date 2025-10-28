package com.pixelro.nenoonkiosk.feature.auth.login

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.DebugConstants
import com.pixelro.nenoonkiosk.core.ui.Logo
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import org.orbitmvi.orbit.compose.collectSideEffect


@Composable
fun LoginRoute(
    updateIsSignedIn: (Boolean) -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LoginSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LoginScreen(
        onIdPasswordClick = { viewModel.navigateToIdPassword() },
        onQRClick = { viewModel.navigateToQR() },
        onFaceIdClick = { viewModel.navigateToFaceId() },
        onSignUpClick = { viewModel.navigateToSignUpTerms() },
        onSkipSignInClick = { viewModel.userSignInSkip(updateIsSignedIn) },
        onBluetoothClick = { viewModel.navigateToBluetoothManagement() },
        onAdminClick = { viewModel.navigateToAdminPage() },
        onSettingsClick = { viewModel.navigateToSettings() }
    )
}

@Composable
fun LoginScreen(
    onIdPasswordClick: () -> Unit,
    onQRClick: () -> Unit,
    onFaceIdClick: () -> Unit,
    onSignUpClick: () -> Unit,
    onSkipSignInClick: () -> Unit,
    onBluetoothClick: () -> Unit,
    onAdminClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    Column(
        verticalArrangement = Arrangement.Top,
    ) {
        Row(
            horizontalArrangement = Arrangement.End,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 40.dp, end = 40.dp, top = 40.dp),
        ) {
            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onBluetoothClick
                    ),
                painter = painterResource(id = R.drawable.bluetooth_settings_icon),
                contentDescription = "",
            )

            if (DebugConstants.ENABLE_ADMIN_PAGE) {
                Spacer(modifier = Modifier.width(40.dp))

                Image(
                    modifier = Modifier
                        .size(40.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() },
                            onClick = onAdminClick
                        ),
                    painter = painterResource(id = R.drawable.data_icon),
                    contentDescription = "",
                )
            }

            Spacer(modifier = Modifier.width(40.dp))

            Image(
                modifier = Modifier
                    .size(40.dp)
                    .clickable(
                        indication = null,
                        interactionSource = remember { MutableInteractionSource() },
                        onClick = onSettingsClick
                    ),
                painter = painterResource(id = R.drawable.icon_settings),
                contentDescription = "",
            )
        }

        Column(
            modifier = Modifier
                .padding(start = 40.dp, end = 40.dp, bottom = 40.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
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
                onClick = onIdPasswordClick,
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.default_sign_in_qr_login),
                onClick = onQRClick,
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.default_sign_in_face_recognition),
                onClick = onFaceIdClick,
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.start_without_signin),
                onClick = onSkipSignInClick,
            )

            Spacer(modifier = Modifier.weight(0.5f))

            PrimaryButton(
                text = StringProvider.getString(R.string.default_sign_in_sign_up),
                onClick = onSignUpClick,
            )
        }
    }
}