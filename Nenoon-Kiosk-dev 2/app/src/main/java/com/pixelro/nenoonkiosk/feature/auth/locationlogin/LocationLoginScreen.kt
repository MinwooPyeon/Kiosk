package com.pixelro.nenoonkiosk.feature.auth.locationlogin

import android.content.res.Configuration
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.Logo
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.auth.SignInScreenState
import com.pixelro.nenoonkiosk.ui.theme.NEURAL200
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LocationLoginRoute(
    updateIsSignedIn: (Boolean) -> Unit,
    signInNavController: NavController,
    navController: NavController,
    viewModel: LocationLoginViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is LocationLoginSideEffect.ShowToast -> {
                // 토스트 표시 처리
            }
            is LocationLoginSideEffect.LoginSuccess -> {
                updateIsSignedIn(true)
                signInNavController.navigate(SignInScreenState.UserSignIn.name)
            }
            is LocationLoginSideEffect.NavigateToUserSignIn -> {
                signInNavController.navigate(SignInScreenState.UserSignIn.name)
            }
            is LocationLoginSideEffect.NavigateToSettings -> {
                navController.navigate(NavConstants.ROUTE_SETTINGS)
            }
        }
    }

    LocationLoginScreen(
        state = state,
        onIdChange = { viewModel.updateId(it) },
        onPasswordChange = { viewModel.updatePassword(it) },
        onSignInClick = { viewModel.signIn() },
        onSkipClick = { viewModel.skipSignIn(updateIsSignedIn) },
        onSettingsClick = { viewModel.navigateToSettings() }
    )
}

@Composable
fun LocationLoginScreen(
    state: LocationLoginState,
    onIdChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    onSkipClick: () -> Unit,
    onSettingsClick: () -> Unit
) {
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT

    if (isPortrait) {
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
                                onSettingsClick()
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
                )

                Spacer(modifier = Modifier.height(40.dp))

                BasicTextField(
                    value = state.id,
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
                            if (state.id.isEmpty()) {
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

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                )

                BasicTextField(
                    value = state.password,
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
                            if (state.password.isEmpty()) {
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

                Spacer(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = StringProvider.getString(R.string.start_without_signin),
                    onClick = onSkipClick,
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    text = StringProvider.getString(R.string.signin),
                    onClick = onSignInClick,
                    enabled = !state.isLoggingIn
                )
            }
        }
    } else {
        // Landscape 처리
    }
}
