package com.pixelro.nenoonkiosk.feature.auth.faceupdate

import android.graphics.Bitmap
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.CameraPreview
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
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

    var currentScreenStatus: String by remember {
        mutableStateOf(StringProvider.getString(R.string.user_face_update_initial_status))
    }

    LaunchedEffect(Unit) {
        loginViewModel.resetFaceEnrollmentData()
        loginViewModel.clearEnrollmentMessage()
    }

    LaunchedEffect(isFaceEnrollmentDataReady, faceDetectionStatus, faceEnrollAttempted) {
        currentScreenStatus =
            if (isFaceEnrollmentDataReady) {
                StringProvider.getString(R.string.user_face_update_ready_status)
            } else if (!faceEnrollAttempted && faceDetectionStatus.isEmpty()) {
                StringProvider.getString(R.string.user_face_update_scan_prompt)
            } else if (faceDetectionStatus.isNotEmpty()) {
                faceDetectionStatus
            } else {
                StringProvider.getString(R.string.user_face_update_retry_scan)
            }
    }

    DisposableEffect(Unit) {
        onDispose {
            liveCameraBitmap?.recycle()
            liveCameraBitmap = null
        }
    }

    Column(
        modifier =
            Modifier
                .padding(40.dp)
                .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        StyledText(
            text = StringProvider.getString(R.string.user_face_update_title),
            style = TextStyle.Title,
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier =
                Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f)
                    .clip(MaterialTheme.shapes.medium)
                    .align(Alignment.CenterHorizontally),
        ) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onFaceDetected = { faceBitmap ->
                    liveCameraBitmap?.recycle()
                    liveCameraBitmap = faceBitmap.config?.let { faceBitmap.copy(it, true) }
                    faceBitmap.recycle()
                },
                onDetectionStatus = { status ->
                    loginViewModel.updateFaceDetectionStatus(status)
                },
            )

            lastDetectedFaceBitmap?.let { bitmap ->
                if (!bitmap.isRecycled && isFaceEnrollmentDataReady) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = StringProvider.getString(R.string.user_face_update_captured_face_description),
                        modifier =
                            Modifier
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
            style =
                if (isFaceEnrollmentDataReady) {
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
                text =
                    if (!faceEnrollAttempted) {
                        StringProvider.getString(R.string.user_face_update_capture_button)
                    } else {
                        StringProvider.getString(R.string.user_face_update_recapture_button)
                    },
                onClick = {
                    if (liveCameraBitmap != null && !isProcessingFace) {
                        faceEnrollAttempted = true
                        loginViewModel.processFaceForEmbeddingAndStoreTemporarily(liveCameraBitmap!!)
                    }
                },
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.user_face_update_save_button),
                onClick = {
                    if (isFaceEnrollmentDataReady && lastDetectedFaceBitmap != null && loggedInUserId != null) {
                        coroutineScope.launch(Dispatchers.Main) {
                            loginViewModel.updateFace(loggedInUserId!!).also { success ->
                                if (success) {
                                    navController.popBackStack(NavConstants.ROUTE_ACCOUNT_MANAGEMENT, false)
                                    loginViewModel.clearEnrollmentMessage()
                                } else {
                                    loginViewModel.clearEnrollmentMessage()
                                }
                            }
                        }
                    }
                },
                enabled = isFaceEnrollmentDataReady && !isProcessingFace,
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.cancel),
                onClick = {
                    loginViewModel.resetFaceEnrollmentData()
                    navController.popBackStack(NavConstants.ROUTE_ACCOUNT_MANAGEMENT, false)
                },
            )
        }
    }
}
