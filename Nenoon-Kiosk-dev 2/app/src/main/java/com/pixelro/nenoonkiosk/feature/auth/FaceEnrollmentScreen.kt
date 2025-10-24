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
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.CameraPreview
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.feature.auth.login.LoginViewModel
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

    Column(
        modifier =
            Modifier
                .padding(40.dp)
                .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
    ) {
        StyledText(
            text = StringProvider.getString(R.string.user_signup_title),
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
                    if (!isProcessingFace) {
                        loginViewModel.processFaceForEmbeddingAndStoreTemporarily(faceBitmap)
                    } else {
                        faceBitmap.recycle()
                    }
                },
                onDetectionStatus = { status ->
                    loginViewModel.updateFaceDetectionStatus(status)
                },
            )

            lastDetectedFaceBitmap?.let { bitmap ->
                if (!bitmap.isRecycled) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = StringProvider.getString(R.string.captured_face_image_description),
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
            text =
                if (isFaceEnrollmentDataReady) {
                    StringProvider.getString(R.string.face_enrollment_success)
                } else {
                    faceDetectionStatus
                },
            style =
                if (isFaceEnrollmentDataReady) {
                    TextStyle.Success
                } else {
                    TextStyle.Message
                },
        )

        Spacer(modifier = Modifier.weight(1f))

        Column {
            PrimaryButton(
                text = StringProvider.getString(R.string.user_signup_enroll_face_button),
                enabled = isFaceEnrollmentDataReady,
                onClick = {
                    coroutineScope.launch(Dispatchers.Main) {
                        loginViewModel.updateFace().also { success ->
                            if (success) {
                                navController.popBackStack(SignInScreenState.UserSignIn.name, false)
                            }
                        }
                    }
                },
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.back),
                onClick = {
                    navController.popBackStack(SignInScreenState.UserSignIn.name, false)
                },
            )
        }
    }
}
