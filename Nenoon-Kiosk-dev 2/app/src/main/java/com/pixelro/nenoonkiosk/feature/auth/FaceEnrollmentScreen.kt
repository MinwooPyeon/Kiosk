package com.pixelro.nenoonkiosk.feature.auth

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.CameraPreview
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FaceEnrollmentScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val faceDetectionStatus by loginViewModel.faceDetectionStatus.collectAsState()
    val isProcessingFace by loginViewModel.isProcessingFace.collectAsState()
    val lastDetectedFaceBitmap by loginViewModel.lastDetectedFaceBitmap.collectAsState()
    val isFaceEnrollmentDataReady by loginViewModel.isFaceEnrollmentDataReady.collectAsState()
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        loginViewModel.resetFaceEnrollmentData()
        loginViewModel.clearEnrollmentMessage()
    }

    val isLandscape = isLandscape()

    if (isLandscape) {
        LandscapeFaceEnrollmentScreen(
            faceDetectionStatus = faceDetectionStatus,
            isProcessingFace = isProcessingFace,
            lastDetectedFaceBitmap = lastDetectedFaceBitmap,
            isFaceEnrollmentDataReady = isFaceEnrollmentDataReady,
            onFaceDetected = { faceBitmap ->
                if (!isProcessingFace) {
                    loginViewModel.processFaceForEmbeddingAndStoreTemporarily(faceBitmap)
                } else {
                    faceBitmap.recycle()
                }
            },
            onDetectionStatus = { status ->
                loginViewModel.updateFaceDetectionStatus(status)
            },
            onEnrollClick = {
                coroutineScope.launch(Dispatchers.Main) {
                    loginViewModel.updateFace().also { success ->
                        if (success) {
                            navController.popBackStack(SignInScreenState.UserSignIn.name, false)
                        }
                    }
                }
            },
            onBackClick = {
                navController.popBackStack(SignInScreenState.UserSignIn.name, false)
            }
        )
    } else {
        PortraitFaceEnrollmentScreen(
            faceDetectionStatus = faceDetectionStatus,
            isProcessingFace = isProcessingFace,
            lastDetectedFaceBitmap = lastDetectedFaceBitmap,
            isFaceEnrollmentDataReady = isFaceEnrollmentDataReady,
            onFaceDetected = { faceBitmap ->
                if (!isProcessingFace) {
                    loginViewModel.processFaceForEmbeddingAndStoreTemporarily(faceBitmap)
                } else {
                    faceBitmap.recycle()
                }
            },
            onDetectionStatus = { status ->
                loginViewModel.updateFaceDetectionStatus(status)
            },
            onEnrollClick = {
                coroutineScope.launch(Dispatchers.Main) {
                    loginViewModel.updateFace().also { success ->
                        if (success) {
                            navController.popBackStack(SignInScreenState.UserSignIn.name, false)
                        }
                    }
                }
            },
            onBackClick = {
                navController.popBackStack(SignInScreenState.UserSignIn.name, false)
            }
        )
    }
}

@Composable
private fun PortraitFaceEnrollmentScreen(
    faceDetectionStatus: String,
    isProcessingFace: Boolean,
    lastDetectedFaceBitmap: android.graphics.Bitmap?,
    isFaceEnrollmentDataReady: Boolean,
    onFaceDetected: (android.graphics.Bitmap) -> Unit,
    onDetectionStatus: (String) -> Unit,
    onEnrollClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val isPreview = LocalInspectionMode.current

    Column(
        modifier = Modifier
            .padding(40.dp)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        StyledText(
            text = stringResource(id = R.string.user_signup_title),
            style = TextStyle.Title,
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.medium)
                .align(Alignment.CenterHorizontally),
        ) {
            if (isPreview) {
                // Preview: Mock 카메라
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    StyledText(text = "카메라 프리뷰")
                }
            } else {
                // 실제 앱: 카메라
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onFaceDetected = onFaceDetected,
                    onDetectionStatus = onDetectionStatus,
                )
            }

            lastDetectedFaceBitmap?.let { bitmap ->
                if (!bitmap.isRecycled) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(id = R.string.captured_face_image_description),
                        modifier = Modifier
                            .size(150.dp)
                            .align(Alignment.BottomEnd)
                            .padding(16.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        StyledText(
            text = if (isFaceEnrollmentDataReady) {
                stringResource(id = R.string.face_enrollment_success)
            } else {
                faceDetectionStatus
            },
            style = if (isFaceEnrollmentDataReady) {
                TextStyle.Success
            } else {
                TextStyle.Message
            },
        )

        Spacer(modifier = Modifier.weight(1f))

        Column {
            PrimaryButton(
                text = stringResource(id = R.string.user_signup_enroll_face_button),
                enabled = isFaceEnrollmentDataReady,
                onClick = onEnrollClick,
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = stringResource(id = R.string.back),
                onClick = onBackClick,
            )
        }
    }
}

@Composable
private fun LandscapeFaceEnrollmentScreen(
    faceDetectionStatus: String,
    isProcessingFace: Boolean,
    lastDetectedFaceBitmap: android.graphics.Bitmap?,
    isFaceEnrollmentDataReady: Boolean,
    onFaceDetected: (android.graphics.Bitmap) -> Unit,
    onDetectionStatus: (String) -> Unit,
    onEnrollClick: () -> Unit,
    onBackClick: () -> Unit
) {
    val isPreview = LocalInspectionMode.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        StyledText(
            text = stringResource(id = R.string.user_signup_title),
            style = TextStyle.Title,
        )

        Spacer(modifier = Modifier.height(30.dp))

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .size(400.dp)
                .clip(MaterialTheme.shapes.medium),
        ) {
            if (isPreview) {
                // Preview: Mock 카메라
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    StyledText(text = "카메라 프리뷰")
                }
            } else {
                // 실제 앱: 카메라
                CameraPreview(
                    modifier = Modifier.fillMaxSize(),
                    onFaceDetected = onFaceDetected,
                    onDetectionStatus = onDetectionStatus,
                )
            }

            lastDetectedFaceBitmap?.let { bitmap ->
                if (!bitmap.isRecycled) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = stringResource(id = R.string.captured_face_image_description),
                        modifier = Modifier
                            .size(120.dp)
                            .align(Alignment.BottomEnd)
                            .padding(12.dp),
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        StyledText(
            text = if (isFaceEnrollmentDataReady) {
                stringResource(id = R.string.face_enrollment_success)
            } else {
                faceDetectionStatus
            },
            style = if (isFaceEnrollmentDataReady) {
                TextStyle.Success
            } else {
                TextStyle.Message
            },
        )

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(400.dp)
        ) {
            PrimaryButton(
                text = stringResource(id = R.string.user_signup_enroll_face_button),
                enabled = isFaceEnrollmentDataReady,
                onClick = onEnrollClick,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = stringResource(id = R.string.back),
                onClick = onBackClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF,
    widthDp = 800,
    heightDp = 1280,
    name = "FaceEnrollment - Portrait"
)
@Composable
private fun FaceEnrollmentScreen_Preview_Portrait() {
    NenoonKioskTheme {
        PortraitFaceEnrollmentScreen(
            faceDetectionStatus = "얼굴을 화면 중앙에 위치시켜주세요",
            isProcessingFace = false,
            lastDetectedFaceBitmap = null,
            isFaceEnrollmentDataReady = false,
            onFaceDetected = {},
            onDetectionStatus = {},
            onEnrollClick = {},
            onBackClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    backgroundColor = 0xFFFFFFFF,
    name = "FaceEnrollment - Landscape"
)
@Composable
private fun FaceEnrollmentScreen_Preview_Landscape() {
    NenoonKioskTheme {
        LandscapeFaceEnrollmentScreen(
            faceDetectionStatus = "얼굴을 화면 중앙에 위치시켜주세요",
            isProcessingFace = false,
            lastDetectedFaceBitmap = null,
            isFaceEnrollmentDataReady = false,
            onFaceDetected = {},
            onDetectionStatus = {},
            onEnrollClick = {},
            onBackClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    backgroundColor = 0xFFFFFFFF,
    name = "FaceEnrollment - Landscape (Ready)"
)
@Composable
private fun FaceEnrollmentScreen_Preview_Landscape_Ready() {
    NenoonKioskTheme {
        LandscapeFaceEnrollmentScreen(
            faceDetectionStatus = "얼굴 등록 준비 완료",
            isProcessingFace = false,
            lastDetectedFaceBitmap = null,
            isFaceEnrollmentDataReady = true,
            onFaceDetected = {},
            onDetectionStatus = {},
            onEnrollClick = {},
            onBackClick = {}
        )
    }
}
