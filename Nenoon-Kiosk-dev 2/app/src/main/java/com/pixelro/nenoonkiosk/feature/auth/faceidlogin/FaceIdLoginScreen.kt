package com.pixelro.nenoonkiosk.feature.auth.faceidlogin

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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.ui.CameraPreview
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.auth.SignInScreenState
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FaceIdLoginRoute(
    navController: NavController,
    updateIsSignedIn: (Boolean) -> Unit,
    viewModel: FaceIdLoginViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FaceIdLoginSideEffect.ShowToast -> {
                // 토스트 표시 처리
            }
            is FaceIdLoginSideEffect.LoginSuccess -> {
                updateIsSignedIn(true)
            }
            is FaceIdLoginSideEffect.LoginFailed -> {
                // 실패 처리
            }
            is FaceIdLoginSideEffect.MaxAttemptsReached -> {
                updateIsSignedIn(false)
            }
            is FaceIdLoginSideEffect.NavigateBack -> {
                navController.popBackStack(SignInScreenState.UserSignIn.name, false)
            }
        }
    }

    LaunchedEffect(Unit) {
        delay(30000)
        updateIsSignedIn(false)
    }

    FaceIdLoginScreen(
        state = state,
        onFaceDetected = { faceBitmap ->
            if (!state.isProcessingFace && state.attemptsLeft > 0 && viewModel.canAttemptSignIn()) {
                viewModel.signInWithFace(faceBitmap)
            } else {
                faceBitmap.recycle()
            }
        },
        onDetectionStatus = { status ->
            viewModel.updateFaceDetectionStatus(status)
        },
        onBackClick = {
            viewModel.navigateBack()
        }
    )
}

@Composable
fun FaceIdLoginScreen(
    state: FaceIdLoginState,
    onFaceDetected: (android.graphics.Bitmap) -> Unit,
    onDetectionStatus: (String) -> Unit,
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
            StringProvider.getString(R.string.faceid_signin_title),
            style = TextStyle.Title,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 40.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
                .align(Alignment.CenterHorizontally),
        ) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onFaceDetected = onFaceDetected,
                onDetectionStatus = onDetectionStatus,
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (state.attemptsLeft > 0) {
            StyledText(state.liveFaceDetectionStatus)
            Spacer(modifier = Modifier.height(20.dp))
            if (state.attemptsLeft < AppConstants.FACE_ID_MAX_ATTEMPTS) {
                StyledText(
                    "${state.faceDetectionStatus} (${AppConstants.FACE_ID_MAX_ATTEMPTS - state.attemptsLeft + 1}/${AppConstants.FACE_ID_MAX_ATTEMPTS})"
                )
            }
        } else {
            StyledText(
                StringProvider.getString(R.string.signin_vm_face_no_match),
                TextStyle.Error
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = StringProvider.getString(R.string.back),
            onClick = onBackClick,
        )
    }
}
