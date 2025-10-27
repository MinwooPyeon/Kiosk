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

@Composable
fun FaceIdSignInScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
    updateIsSignedIn: (Boolean) -> Unit,
) {
    val faceRecognitionStatus by loginViewModel.faceDetectionStatus.collectAsState()
    val isProcessingFace by loginViewModel.isProcessingFace.collectAsState()
    val isSignedIn by loginViewModel.isUserSignedIn.collectAsState()

    var liveFaceDetectionStatus by remember { mutableStateOf("") }
    var attemptsLeft by remember { mutableStateOf(AppConstants.FACE_ID_MAX_ATTEMPTS) }
    val coroutineScope = rememberCoroutineScope()
    var previousAttemptTime by remember { mutableStateOf(System.currentTimeMillis()) }

    LaunchedEffect(Unit) {
        coroutineScope.launch(Dispatchers.Main) {
            delay(30000)
            updateIsSignedIn(false)
        }
    }

    LaunchedEffect(attemptsLeft) {
        if (attemptsLeft <= 0) {
            coroutineScope.launch(Dispatchers.Main) {
                delay(3000)
                updateIsSignedIn(false)
            }
        }
    }

    Column(
        modifier =
            Modifier
                .padding(40.dp)
                .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        StyledText(
            StringProvider.getString(R.string.face_id_sign_in_title),
            style = TextStyle.Title,
            textAlign = TextAlign.Center,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 40.dp),
        )

        Spacer(modifier = Modifier.weight(1f))

        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(16.dp))
                    .align(Alignment.CenterHorizontally),
        ) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onFaceDetected = { faceBitmap ->
                    if (!isProcessingFace && !isSignedIn && attemptsLeft > 0 &&
                        System.currentTimeMillis() - previousAttemptTime > AppConstants.FACE_ID_INTERVAL
                    ) {
                        previousAttemptTime = System.currentTimeMillis()
                        coroutineScope.launch(Dispatchers.Main) {
                            loginViewModel.userSignInWithFace(faceBitmap, updateIsSignedIn).also { success ->
                                if (success) {
                                    delay(3000)
                                    updateIsSignedIn(true)
                                }
                            }
                        }
                        attemptsLeft--
                    } else {
                        faceBitmap.recycle()
                    }
                },
                onDetectionStatus = { status ->
                    liveFaceDetectionStatus = status
                },
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        if (attemptsLeft > 0) StyledText(liveFaceDetectionStatus)

        Spacer(modifier = Modifier.height(20.dp))

        if (attemptsLeft > 0) {
            StyledText(
                faceRecognitionStatus + " (${AppConstants.FACE_ID_MAX_ATTEMPTS - attemptsLeft + 1}/${AppConstants.FACE_ID_MAX_ATTEMPTS})",
            )
        } else {
            StyledText(StringProvider.getString(R.string.signin_vm_face_no_match), TextStyle.Error)
        }

        Spacer(modifier = Modifier.weight(1f))

        PrimaryButton(
            text = StringProvider.getString(R.string.back),
            onClick = {
                navController.popBackStack(SignInScreenState.UserSignIn.name, false)
            },
        )
    }
}
