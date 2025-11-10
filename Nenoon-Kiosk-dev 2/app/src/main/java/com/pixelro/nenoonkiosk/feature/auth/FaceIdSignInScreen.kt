// FaceIdSignInScreen.kt
package com.pixelro.nenoonkiosk.feature.auth

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
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.ui.CameraPreview
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
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
    var attemptsLeft by remember { mutableIntStateOf(AppConstants.FACE_ID_MAX_ATTEMPTS) }
    val coroutineScope = rememberCoroutineScope()
    var previousAttemptTime by remember { mutableLongStateOf(System.currentTimeMillis()) }

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

    FaceIdSignInContent(
        faceRecognitionStatus = faceRecognitionStatus,
        isProcessingFace = isProcessingFace,
        isSignedIn = isSignedIn,
        liveFaceDetectionStatus = liveFaceDetectionStatus,
        attemptsLeft = attemptsLeft,
        onFaceDetected = { faceBitmap ->
            if (!isProcessingFace && !isSignedIn && attemptsLeft > 0 &&
                System.currentTimeMillis() - previousAttemptTime > AppConstants.FACE_ID_INTERVAL
            ) {
                previousAttemptTime = System.currentTimeMillis()
                coroutineScope.launch(Dispatchers.Main) {
                    loginViewModel.userSignInWithFace(faceBitmap, updateIsSignedIn)
                        .also { success ->
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
        onBackClick = {
            navController.popBackStack(SignInScreenState.UserSignIn.name, false)
        }
    )
}

@Composable
private fun FaceIdSignInContent(
    faceRecognitionStatus: String,
    isProcessingFace: Boolean,
    isSignedIn: Boolean,
    liveFaceDetectionStatus: String,
    attemptsLeft: Int,
    onFaceDetected: (android.graphics.Bitmap) -> Unit,
    onDetectionStatus: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val isLandscape = isLandscape()

    if (isLandscape) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            FaceIdSignInLayout(
                faceRecognitionStatus = faceRecognitionStatus,
                isProcessingFace = isProcessingFace,
                isSignedIn = isSignedIn,
                liveFaceDetectionStatus = liveFaceDetectionStatus,
                attemptsLeft = attemptsLeft,
                onFaceDetected = onFaceDetected,
                onDetectionStatus = onDetectionStatus,
                onBackClick = onBackClick,
                isLandscapeMode = true,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 40.dp, vertical = 16.dp)
            )
        }
    } else {
        FaceIdSignInLayout(
            faceRecognitionStatus = faceRecognitionStatus,
            isProcessingFace = isProcessingFace,
            isSignedIn = isSignedIn,
            liveFaceDetectionStatus = liveFaceDetectionStatus,
            attemptsLeft = attemptsLeft,
            onFaceDetected = onFaceDetected,
            onDetectionStatus = onDetectionStatus,
            onBackClick = onBackClick,
            isLandscapeMode = false,
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp)
        )
    }
}

@Composable
private fun FaceIdSignInLayout(
    faceRecognitionStatus: String,
    isProcessingFace: Boolean,
    isSignedIn: Boolean,
    liveFaceDetectionStatus: String,
    attemptsLeft: Int,
    onFaceDetected: (android.graphics.Bitmap) -> Unit,
    onDetectionStatus: (String) -> Unit,
    onBackClick: () -> Unit,
    isLandscapeMode: Boolean,
    modifier: Modifier = Modifier
) {
    val isPreview = LocalInspectionMode.current

    val title = stringResource(id = R.string.default_sign_in_face_recognition)
    val backButtonText = stringResource(id = R.string.back)
    val noMatchText = stringResource(id = R.string.signin_vm_face_no_match)

    val cameraWidthFraction = if (isLandscapeMode) 0.3f else 0.7f

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        Spacer(modifier = Modifier.weight(if (isLandscapeMode) 0.6f else 1f))

        StyledText(
            title,
            style = TextStyle.Title,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.weight(if (isLandscapeMode) 0.5f else 1f))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(cameraWidthFraction)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp)),
        ) {
            if (isPreview) {
                StyledText(text = "카메라 프리뷰")
            } else {
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onFaceDetected = onFaceDetected,
                    onDetectionStatus = onDetectionStatus,
                )
            }
        }

        Spacer(modifier = Modifier.height(if (isLandscapeMode) 20.dp else 20.dp))

        if (attemptsLeft > 0) {
            StyledText(liveFaceDetectionStatus, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(if (isLandscapeMode) 12.dp else 20.dp))

        if (attemptsLeft > 0) {
            StyledText(
                faceRecognitionStatus + " (${AppConstants.FACE_ID_MAX_ATTEMPTS - attemptsLeft + 1}/${AppConstants.FACE_ID_MAX_ATTEMPTS})",
                textAlign = TextAlign.Center
            )
        } else {
            StyledText(noMatchText, TextStyle.Error, textAlign = TextAlign.Center)
        }

        Spacer(modifier = Modifier.height(if (isLandscapeMode) 20.dp else 40.dp))

        PrimaryButton(
            text = backButtonText,
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        )

        Spacer(modifier = Modifier.weight(if (isLandscapeMode) 0.6f else 1f))
    }
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    backgroundColor = 0xFFFFFFFF,
    name = "FaceIdSignIn - Portrait"
)
@Composable
fun FaceIdSignInScreen_Preview_Portrait() {
    NenoonKioskTheme {
        FaceIdSignInContent(
            faceRecognitionStatus = "얼굴 인식 중...",
            isProcessingFace = false,
            isSignedIn = false,
            liveFaceDetectionStatus = "얼굴을 화면 중앙에 위치시켜주세요",
            attemptsLeft = 3,
            onFaceDetected = {},
            onDetectionStatus = {},
            onBackClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
    backgroundColor = 0xFFFFFFFF,
    name = "FaceIdSignIn - Landscape"
)
@Composable
fun FaceIdSignInScreen_Preview_Landscape() {
    NenoonKioskTheme {
        FaceIdSignInContent(
            faceRecognitionStatus = "얼굴 인식 중...",
            isProcessingFace = false,
            isSignedIn = false,
            liveFaceDetectionStatus = "얼굴을 화면 중앙에 위치시켜주세요",
            attemptsLeft = 3,
            onFaceDetected = {},
            onDetectionStatus = {},
            onBackClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1280,
    heightDp = 800,
    backgroundColor = 0xFFFFFFFF,
    name = "FaceIdSignIn - Landscape (Failed)"
)
@Composable
fun FaceIdSignInScreen_Preview_Landscape_Failed() {
    NenoonKioskTheme {
        FaceIdSignInContent(
            faceRecognitionStatus = "얼굴 인식 실패",
            isProcessingFace = false,
            isSignedIn = false,
            liveFaceDetectionStatus = "",
            attemptsLeft = 0,
            onFaceDetected = {},
            onDetectionStatus = {},
            onBackClick = {}
        )
    }
}
