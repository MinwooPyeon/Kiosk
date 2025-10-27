package com.pixelro.nenoonkiosk.feature.auth.accountmanagement

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.constants.GlobalValue
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.ProgressIndicator
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun AccountManagementRoute(
    navController: NavController,
    userId: String?,
    userData: com.harang.data.model.dto.User?,
    isUserSignedIn: Boolean,
    onSignOut: () -> Unit,
    viewModel: AccountManagementViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is AccountManagementSideEffect.ShowToast -> {
                // 토스트 표시 처리
            }

            is AccountManagementSideEffect.NavigateToFaceEnrollment -> {
                navController.navigate(NavConstants.ROUTE_FACE_UPDATE_TERMS_OF_SERVICE)
            }

            is AccountManagementSideEffect.SignOut -> {
                onSignOut()
                navController.navigate(NavConstants.ROUTE_SIGN_IN) {
                    popUpTo(navController.graph.startDestinationId) { inclusive = false }
                    launchSingleTop = true
                }
            }

            is AccountManagementSideEffect.NavigateBack -> {
                navController.popBackStack(NavConstants.ROUTE_CATEGORY_LIST, false)
            }
        }
    }

    LaunchedEffect(userId, userData, isUserSignedIn) {
        viewModel.loadUserData(userId, userData, isUserSignedIn)

        if (!AppConstants.MANAGE_USERS_INTERNALLY) {
            userData?.accessToken?.let { token ->
                viewModel.loadQrCodeFromServer(token)
            }
        } else if (isUserSignedIn &&
            !userData?.id.isNullOrBlank() &&
            !userData?.password.isNullOrBlank()
        ) {
            viewModel.generateQrCodeBitmap(userData?.id!!, userData?.password!!)
        }
    }

    AccountManagementScreen(
        state = state,
        onPrintQrCodeClick = {
            viewModel.printQrCode()
        },
        onFaceEnrollClick = {
            viewModel.navigateToFaceEnrollment()
        },
        onSignOutClick = {
            viewModel.signOut()
        },
        onCloseClick = {
            viewModel.navigateBack()
        }
    )
}

@Composable
fun AccountManagementScreen(
    state: AccountManagementState,
    onPrintQrCodeClick: () -> Unit,
    onFaceEnrollClick: () -> Unit,
    onSignOutClick: () -> Unit,
    onCloseClick: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize(),
        ) {
            Box(
                modifier = Modifier
                    .padding(
                        start = 40.dp,
                        top = (GlobalValue.statusBarPadding + 20).dp,
                        end = 40.dp,
                        bottom = 20.dp,
                    )
                    .fillMaxWidth()
                    .height(40.dp),
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    Image(
                        modifier = Modifier
                            .width(32.dp)
                            .clickable { onCloseClick() },
                        painter = painterResource(id = R.drawable.close_button_black),
                        contentDescription = StringProvider.getString(
                            R.string.close_button_description
                        ),
                    )
                }
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = StringProvider.getString(R.string.account_management_title),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }

            Spacer(
                modifier = Modifier
                    .padding(start = 5.dp, end = 5.dp)
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(color = Color(0xff000000)),
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(40.dp),
            ) {
                Spacer(modifier = Modifier.weight(1f))

                if (state.showProgressIndicator) {
                    ProgressIndicator()
                } else if (!state.isUserSignInSkipped && state.isUserSignedIn) {
                    StyledText(
                        text = StringProvider.getString(
                            R.string.qr_code_user_name,
                            state.userData?.name
                                ?: StringProvider.getString(R.string.default_user_name),
                        ),
                    )

                    if (state.qrCodeBitmap != null) {
                        Image(
                            bitmap = state.qrCodeBitmap.asImageBitmap(),
                            contentDescription = StringProvider.getString(
                                R.string.qr_code_image_description
                            ),
                            modifier = Modifier
                                .size(400.dp)
                                .padding(top = 40.dp),
                        )
                    } else {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center,
                            modifier = Modifier.size(400.dp),
                        ) {
                            ProgressIndicator()
                        }
                    }
                } else {
                    StyledText(
                        text = StringProvider.getString(R.string.not_signed_in_message),
                        style = TextStyle.Error,
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                PrimaryButton(
                    onClick = onPrintQrCodeClick,
                    text = StringProvider.getString(R.string.qr_code_print_button),
                    enabled = !state.isUserSignInSkipped && state.isQrPrintButtonEnabled,
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    onClick = onFaceEnrollClick,
                    text = StringProvider.getString(R.string.face_enroll_button_text),
                    enabled = !state.isUserSignInSkipped && state.isUserSignedIn && state.userData?.id != null,
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    onClick = onSignOutClick,
                    text = if (!state.isUserSignInSkipped) {
                        StringProvider.getString(R.string.settings_signout)
                    } else {
                        StringProvider.getString(R.string.signin)
                    },
                )
            }
        }
    }
}
