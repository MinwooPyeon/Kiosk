package com.pixelro.nenoonkiosk.feature.auth.locationlogin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.auth.SignInScreenState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun LocationLoginRoute(
    navController: NavController,
    updateIsSignedIn: (Boolean) -> Unit,
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
            }

            is LocationLoginSideEffect.LoginFailed -> {
                // 실패 처리
            }

            is LocationLoginSideEffect.RequestLocationPermission -> {
                // 권한 요청 처리
            }

            is LocationLoginSideEffect.RequestEnableLocation -> {
                // 위치 활성화 요청 처리
            }

            is LocationLoginSideEffect.NavigateBack -> {
                navController.popBackStack(SignInScreenState.UserSignIn.name, false)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkLocationPermission()
        viewModel.checkLocationEnabled()
    }

    LocationLoginScreen(
        state = state,
        onSignInWithLocationClick = {
            viewModel.signInWithLocation()
        },
        onBackClick = {
            viewModel.navigateBack()
        }
    )
}

@Composable
fun LocationLoginScreen(
    state: LocationLoginState,
    onSignInWithLocationClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .padding(40.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        StyledText(
            text = StringProvider.getString(R.string.location_signin_title),
            style = TextStyle.Title,
        )

        Spacer(modifier = Modifier.weight(1f))

        if (state.isCheckingLocation) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (state.locationStatus.isNotEmpty()) {
            StyledText(
                text = state.locationStatus,
                style = if (state.locationStatus.contains("success", ignoreCase = true)) {
                    TextStyle.Success
                } else if (state.locationStatus.contains("error", ignoreCase = true) ||
                    state.locationStatus.contains("failed", ignoreCase = true)
                ) {
                    TextStyle.Error
                } else {
                    TextStyle.Message
                },
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        if (state.currentLatitude != null && state.currentLongitude != null) {
            StyledText(
                text = StringProvider.getString(
                    R.string.location_signin_coordinates,
                    state.currentLatitude,
                    state.currentLongitude
                ),
                style = TextStyle.Message,
                textAlign = TextAlign.Center,
            )
            Spacer(modifier = Modifier.height(20.dp))
        }

        Spacer(modifier = Modifier.weight(1f))

        Column {
            PrimaryButton(
                text = StringProvider.getString(R.string.location_signin_button_text),
                onClick = onSignInWithLocationClick,
                enabled = !state.isCheckingLocation &&
                        state.isLocationPermissionGranted &&
                        state.isLocationEnabled,
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.back),
                onClick = onBackClick,
                enabled = !state.isCheckingLocation,
            )
        }
    }
}