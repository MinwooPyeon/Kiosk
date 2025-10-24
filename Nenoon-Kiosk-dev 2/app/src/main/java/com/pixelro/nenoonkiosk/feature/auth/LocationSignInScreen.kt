package com.pixelro.nenoonkiosk.feature.auth

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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.Logo
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.auth.login.SignInViewModel
import com.pixelro.nenoonkiosk.ui.theme.NEURAL200
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun LocationSignInScreen(
    updateIsSignedIn: (Boolean) -> Unit,
    signInViewModel: SignInViewModel,
    signInNavController: NavController,
    navController: NavController,
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val isPortrait = configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    if (isPortrait) {
        Box {
            Column(
                modifier =
                    Modifier
                        .padding(40.dp)
                        .fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top,
            ) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                ) {
                    Image(
                        modifier =
                            Modifier
                                .size(40.dp)
                                .clickable(
                                    indication = null,
                                    interactionSource = remember { MutableInteractionSource() },
                                ) {
                                    navController.navigate(NavConstants.ROUTE_SETTINGS)
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
                    value = id,
                    onValueChange = { id = it },
                    textStyle =
                        TextStyle(
                            fontSize = 36.sp,
                        ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .border(
                                        border =
                                            BorderStroke(
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
                    keyboardOptions =
                        KeyboardOptions(
                            imeAction = ImeAction.Next,
                        ),
                )

                Spacer(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(20.dp),
                )

                BasicTextField(
                    value = password,
                    onValueChange = { password = it },
                    visualTransformation = PasswordVisualTransformation(),
                    textStyle =
                        TextStyle(
                            fontSize = 36.sp,
                        ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier =
                                Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .border(
                                        border =
                                            BorderStroke(
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
                    keyboardOptions =
                        KeyboardOptions(
                            imeAction = ImeAction.Done,
                        ),
                )

                Spacer(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(20.dp),
                )

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    text = StringProvider.getString(R.string.start_without_signin),
                    onClick = {
                        signInViewModel.locationSignInSkip(updateIsSignedIn)
                        signInNavController.navigate(SignInScreenState.UserSignIn.name)
                    },
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    text = StringProvider.getString(R.string.signin),
                    onClick = {
                        if (!signInViewModel.validateLocationSignIn(
                                id,
                                password,
                            )
                        ) {
                            return@PrimaryButton
                        }
                        coroutineScope.launch(Dispatchers.Main) {
                            signInViewModel.locationSignIn(id, password, updateIsSignedIn)
                                .also { success ->
                                    if (success) {
                                        signInNavController.navigate(SignInScreenState.UserSignIn.name)
                                    } else {
                                        // Handle login error
                                    }
                                }
                        }
                    },
                )
            }
        }
    } else {
    }
}
