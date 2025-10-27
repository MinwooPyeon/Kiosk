package com.pixelro.nenoonkiosk.feature.auth.qrlogin

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.auth.SignInScreenState
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun QrLoginRoute(
    navController: NavController,
    updateIsSignedIn: (Boolean) -> Unit,
    viewModel: QrLoginViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is QrLoginSideEffect.ShowToast -> {
                // 토스트 표시 처리
            }

            is QrLoginSideEffect.LoginSuccess -> {
                updateIsSignedIn(true)
            }

            is QrLoginSideEffect.LoginFailed -> {
                // 실패 처리
            }

            is QrLoginSideEffect.RequestCameraPermission -> {
                // 카메라 권한 요청 처리
            }

            is QrLoginSideEffect.NavigateBack -> {
                navController.popBackStack(SignInScreenState.UserSignIn.name, false)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.checkCameraPermission()
    }

    QrLoginScreen(
        state = state,
        onQrCodeScanned = { qrData ->
            viewModel.signInWithQrCode(qrData)
        },
        onBackClick = {
            viewModel.navigateBack()
        }
    )
}

@Composable
fun QrLoginScreen(
    state: QrLoginState,
    onQrCodeScanned: (String) -> Unit,
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
            text = StringProvider.getString(R.string.qr_signin_title),
            style = TextStyle.Title,
        )

        Spacer(modifier = Modifier.weight(1f))

        if (state.isCameraPermissionGranted) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterHorizontally),
            ) {
                QrCodeScanner(
                    modifier = Modifier.fillMaxSize(),
                    onQrCodeScanned = { qrData ->
                        if (!state.isProcessingQr) {
                            onQrCodeScanned(qrData)
                        }
                    }
                )
            }
        } else {
            StyledText(
                text = StringProvider.getString(R.string.qr_signin_camera_permission_required),
                style = TextStyle.Error,
                textAlign = TextAlign.Center,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (state.isProcessingQr) {
            CircularProgressIndicator()
            Spacer(modifier = Modifier.height(20.dp))
        }

        StyledText(
            text = state.qrScanStatus,
            style = if (state.qrScanStatus.contains("success", ignoreCase = true)) {
                TextStyle.Success
            } else if (state.qrScanStatus.contains("error", ignoreCase = true) ||
                state.qrScanStatus.contains("failed", ignoreCase = true) ||
                state.qrScanStatus.contains("invalid", ignoreCase = true)
            ) {
                TextStyle.Error
            } else {
                TextStyle.Message
            },
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = StringProvider.getString(R.string.back),
            onClick = onBackClick,
            enabled = !state.isProcessingQr,
        )
    }
}