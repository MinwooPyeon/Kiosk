package com.pixelro.nenoonkiosk.feature.auth

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.CameraPreview
import com.pixelro.nenoonkiosk.core.ui.NenoonTopBar
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
import com.pixelro.nenoonkiosk.ui.theme.NenoonKioskTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FaceUpdateScreen(
    loginViewModel: LoginViewModel,
    navController: NavController,
) {
    val context = LocalContext.current
    val faceDetectionStatus by loginViewModel.faceDetectionStatus.collectAsState()
    val isProcessingFace by loginViewModel.isProcessingFace.collectAsState()
    val lastDetectedFaceBitmap by loginViewModel.lastDetectedFaceBitmap.collectAsState()
    val isFaceEnrollmentDataReady by loginViewModel.isFaceEnrollmentDataReady.collectAsState()
    val enrollmentSuccess by loginViewModel.enrollmentSuccess.collectAsState()
    val enrollmentMessage by loginViewModel.enrollmentMessage.collectAsState()
    val loggedInUserId by loginViewModel.userId.collectAsState()
    var faceEnrollAttempted by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    var liveCameraBitmap: Bitmap? by remember { mutableStateOf(null) }

    val initialStatus = stringResource(R.string.user_face_update_initial_status)
    val readyStatus = stringResource(R.string.user_face_update_ready_status)
    val scanPrompt = stringResource(R.string.user_face_update_scan_prompt)
    val retryStatus = stringResource(R.string.user_face_update_retry_scan)

    var currentScreenStatus: String by remember {
        mutableStateOf(initialStatus)
    }

    LaunchedEffect(Unit) {
        loginViewModel.resetFaceEnrollmentData()
        loginViewModel.clearEnrollmentMessage()
    }

    LaunchedEffect(isFaceEnrollmentDataReady, faceDetectionStatus, faceEnrollAttempted) {
        currentScreenStatus = when {
            isFaceEnrollmentDataReady -> readyStatus
            !faceEnrollAttempted && faceDetectionStatus.isEmpty() -> scanPrompt
            faceDetectionStatus.isNotEmpty() -> faceDetectionStatus
            else -> retryStatus
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            liveCameraBitmap?.recycle()
            liveCameraBitmap = null
        }
    }

    val isLandscape = isLandscape()

    if (isLandscape) {
        LandscapeFaceUpdateScreen(
            faceDetectionStatus = faceDetectionStatus,
            isProcessingFace = isProcessingFace,
            lastDetectedFaceBitmap = lastDetectedFaceBitmap,
            isFaceEnrollmentDataReady = isFaceEnrollmentDataReady,
            currentScreenStatus = currentScreenStatus,
            faceEnrollAttempted = faceEnrollAttempted,
            liveCameraBitmap = liveCameraBitmap,
            onFaceDetected = { faceBitmap ->
                liveCameraBitmap?.recycle()
                liveCameraBitmap = faceBitmap.config?.let { faceBitmap.copy(it, true) }
                faceBitmap.recycle()
            },
            onDetectionStatus = { status ->
                loginViewModel.updateFaceDetectionStatus(status)
            },
            onCaptureClick = {
                if (liveCameraBitmap != null && !isProcessingFace) {
                    faceEnrollAttempted = true
                    loginViewModel.processFaceForEmbeddingAndStoreTemporarily(liveCameraBitmap!!)
                }
            },
            onSaveClick = {
                if (isFaceEnrollmentDataReady && lastDetectedFaceBitmap != null && loggedInUserId != null) {
                    coroutineScope.launch(Dispatchers.Main) {
                        loginViewModel.updateFace(loggedInUserId!!).also { success ->
                            if (success) {
                                navController.popBackStack(
                                    NavConstants.ROUTE_ACCOUNT_MANAGEMENT,
                                    false
                                )
                                loginViewModel.clearEnrollmentMessage()
                            } else {
                                loginViewModel.clearEnrollmentMessage()
                            }
                        }
                    }
                }
            },
            onCancelClick = {
                loginViewModel.resetFaceEnrollmentData()
                navController.popBackStack(NavConstants.ROUTE_ACCOUNT_MANAGEMENT, false)
            }
        )
    } else {
        PortraitFaceUpdateScreen(
            faceDetectionStatus = faceDetectionStatus,
            isProcessingFace = isProcessingFace,
            lastDetectedFaceBitmap = lastDetectedFaceBitmap,
            isFaceEnrollmentDataReady = isFaceEnrollmentDataReady,
            currentScreenStatus = currentScreenStatus,
            faceEnrollAttempted = faceEnrollAttempted,
            liveCameraBitmap = liveCameraBitmap,
            onFaceDetected = { faceBitmap ->
                liveCameraBitmap?.recycle()
                liveCameraBitmap = faceBitmap.config?.let { faceBitmap.copy(it, true) }
                faceBitmap.recycle()
            },
            onDetectionStatus = { status ->
                loginViewModel.updateFaceDetectionStatus(status)
            },
            onCaptureClick = {
                if (liveCameraBitmap != null && !isProcessingFace) {
                    faceEnrollAttempted = true
                    loginViewModel.processFaceForEmbeddingAndStoreTemporarily(liveCameraBitmap!!)
                }
            },
            onSaveClick = {
                if (isFaceEnrollmentDataReady && lastDetectedFaceBitmap != null && loggedInUserId != null) {
                    coroutineScope.launch(Dispatchers.Main) {
                        loginViewModel.updateFace(loggedInUserId!!).also { success ->
                            if (success) {
                                navController.popBackStack(
                                    NavConstants.ROUTE_ACCOUNT_MANAGEMENT,
                                    false
                                )
                                loginViewModel.clearEnrollmentMessage()
                            } else {
                                loginViewModel.clearEnrollmentMessage()
                            }
                        }
                    }
                }
            },
            onCancelClick = {
                loginViewModel.resetFaceEnrollmentData()
                navController.popBackStack(NavConstants.ROUTE_ACCOUNT_MANAGEMENT, false)
            }
        )
    }
}

@Composable
private fun PortraitFaceUpdateScreen(
    faceDetectionStatus: String,
    isProcessingFace: Boolean,
    lastDetectedFaceBitmap: Bitmap?,
    isFaceEnrollmentDataReady: Boolean,
    currentScreenStatus: String,
    faceEnrollAttempted: Boolean,
    liveCameraBitmap: Bitmap?,
    onFaceDetected: (Bitmap) -> Unit,
    onDetectionStatus: (String) -> Unit,
    onCaptureClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val isPreview = LocalInspectionMode.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top,
    ) {
        NenoonTopBar(
            title = stringResource(id = R.string.user_face_update_title),
            showBackButton = false
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(40.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .align(Alignment.CenterHorizontally),
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

                lastDetectedFaceBitmap?.let { bitmap ->
                    if (!bitmap.isRecycled && isFaceEnrollmentDataReady) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = stringResource(id = R.string.user_face_update_captured_face_description),
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
                text = currentScreenStatus,
                style = if (isFaceEnrollmentDataReady) {
                    TextStyle.Success
                } else if (faceDetectionStatus.isEmpty()) {
                    TextStyle.Error
                } else {
                    TextStyle.Message
                },
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.weight(1f))

            Column {
                PrimaryButton(
                    text = if (!faceEnrollAttempted) {
                        stringResource(id = R.string.user_face_update_capture_button)
                    } else {
                        stringResource(id = R.string.user_face_update_recapture_button)
                    },
                    onClick = onCaptureClick,
                    modifier = Modifier.shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    text = stringResource(id = R.string.user_face_update_save_button),
                    onClick = onSaveClick,
                    enabled = isFaceEnrollmentDataReady && !isProcessingFace,
                    modifier = Modifier.shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
                )

                Spacer(modifier = Modifier.height(20.dp))

                PrimaryButton(
                    text = stringResource(id = R.string.cancel),
                    onClick = onCancelClick,
                    modifier = Modifier.shadow(
                        elevation = 2.dp,
                        shape = RoundedCornerShape(10.dp)
                    )
                )
            }
        }
    }
}

@Composable
private fun LandscapeFaceUpdateScreen(
    faceDetectionStatus: String,
    isProcessingFace: Boolean,
    lastDetectedFaceBitmap: Bitmap?,
    isFaceEnrollmentDataReady: Boolean,
    currentScreenStatus: String,
    faceEnrollAttempted: Boolean,
    liveCameraBitmap: Bitmap?,
    onFaceDetected: (Bitmap) -> Unit,
    onDetectionStatus: (String) -> Unit,
    onCaptureClick: () -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit
) {
    val isPreview = LocalInspectionMode.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        NenoonTopBar(
            title = stringResource(id = R.string.user_face_update_title),
            showBackButton = false
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 15.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(400.dp)
                    .clip(MaterialTheme.shapes.medium),
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

                lastDetectedFaceBitmap?.let { bitmap ->
                    if (!bitmap.isRecycled && isFaceEnrollmentDataReady) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = stringResource(id = R.string.user_face_update_captured_face_description),
                            modifier = Modifier
                                .size(120.dp)
                                .align(Alignment.BottomEnd)
                                .padding(12.dp),
                        )
                    }
                }
            }

            StyledText(
                text = currentScreenStatus,
                style = if (isFaceEnrollmentDataReady) {
                    TextStyle.Success
                } else if (faceDetectionStatus.isEmpty()) {
                    TextStyle.Error
                } else {
                    TextStyle.Message
                },
                textAlign = TextAlign.Center,
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth(0.8f)
            ) {
                PrimaryButton(
                    text = if (!faceEnrollAttempted) {
                        stringResource(id = R.string.user_face_update_capture_button)
                    } else {
                        stringResource(id = R.string.user_face_update_recapture_button)
                    },
                    onClick = onCaptureClick,
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                )

                PrimaryButton(
                    text = stringResource(id = R.string.user_face_update_save_button),
                    onClick = onSaveClick,
                    enabled = isFaceEnrollmentDataReady && !isProcessingFace,
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                )

                PrimaryButton(
                    text = stringResource(id = R.string.cancel),
                    onClick = onCancelClick,
                    modifier = Modifier
                        .weight(1f)
                        .shadow(
                            elevation = 2.dp,
                            shape = RoundedCornerShape(10.dp)
                        )
                )
            }
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 800,
    heightDp = 1280,
    backgroundColor = 0xFFFFFFFF,
    name = "FaceUpdate - Portrait"
)
@Composable
private fun FaceUpdateScreen_Preview_Portrait() {
    NenoonKioskTheme {
        PortraitFaceUpdateScreen(
            faceDetectionStatus = "얼굴을 화면 중앙에 위치시켜주세요",
            isProcessingFace = false,
            lastDetectedFaceBitmap = null,
            isFaceEnrollmentDataReady = false,
            currentScreenStatus = "얼굴을 스캔해주세요",
            faceEnrollAttempted = false,
            liveCameraBitmap = null,
            onFaceDetected = {},
            onDetectionStatus = {},
            onCaptureClick = {},
            onSaveClick = {},
            onCancelClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    backgroundColor = 0xFFFFFFFF,
    name = "FaceUpdate - Landscape"
)
@Composable
private fun FaceUpdateScreen_Preview_Landscape() {
    NenoonKioskTheme {
        LandscapeFaceUpdateScreen(
            faceDetectionStatus = "얼굴을 화면 중앙에 위치시켜주세요",
            isProcessingFace = false,
            lastDetectedFaceBitmap = null,
            isFaceEnrollmentDataReady = false,
            currentScreenStatus = "얼굴을 스캔해주세요",
            faceEnrollAttempted = false,
            liveCameraBitmap = null,
            onFaceDetected = {},
            onDetectionStatus = {},
            onCaptureClick = {},
            onSaveClick = {},
            onCancelClick = {}
        )
    }
}

@Preview(
    showBackground = true,
    widthDp = 1422,
    heightDp = 888,
    backgroundColor = 0xFFFFFFFF,
    name = "FaceUpdate - Landscape (Ready)"
)
@Composable
private fun FaceUpdateScreen_Preview_Landscape_Ready() {
    NenoonKioskTheme {
        LandscapeFaceUpdateScreen(
            faceDetectionStatus = "얼굴 인식 완료",
            isProcessingFace = false,
            lastDetectedFaceBitmap = null,
            isFaceEnrollmentDataReady = true,
            currentScreenStatus = "얼굴 등록 준비 완료",
            faceEnrollAttempted = true,
            liveCameraBitmap = null,
            onFaceDetected = {},
            onDetectionStatus = {},
            onCaptureClick = {},
            onSaveClick = {},
            onCancelClick = {}
        )
    }
}
