package com.pixelro.nenoonkiosk.feature.auth.faceenrollment

import android.graphics.Bitmap
import android.widget.Toast
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.CameraPreview
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FaceEnrollmentRoute(
    userId: String,
    accessToken: String? = null,
    viewModel: FaceEnrollmentViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value
    val context = LocalContext.current

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FaceEnrollmentSideEffect.ShowToast -> {
                Toast.makeText(context, sideEffect.message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.resetFaceEnrollmentData()
    }

    FaceEnrollmentScreen(
        state = state,
        onFaceDetected = { faceBitmap ->
            viewModel.processFaceForEmbedding(faceBitmap)
        },
        onDetectionStatus = { status ->
            viewModel.updateFaceDetectionStatus(status)
        },
        onEnrollFaceClick = {
            viewModel.enrollFace(userId, accessToken)
        },
        onBackClick = {
            viewModel.navigateBack()
        }
    )
}

@Composable
fun FaceEnrollmentScreen(
    state: FaceEnrollmentState,
    onFaceDetected: (Bitmap) -> Unit,
    onDetectionStatus: (String) -> Unit,
    onEnrollFaceClick: () -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
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
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.medium)
                .align(Alignment.CenterHorizontally),
        ) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onFaceDetected = { faceBitmap ->
                    if (!state.isProcessingFace) {
                        onFaceDetected(faceBitmap)
                    } else {
                        faceBitmap.recycle()
                    }
                },
                onDetectionStatus = { status ->
                    onDetectionStatus(status)
                },
            )

            state.lastDetectedFaceBitmap?.let { bitmap ->
                if (!bitmap.isRecycled) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = StringProvider.getString(R.string.captured_face_image_description),
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
            text = if (state.isFaceEnrollmentDataReady) {
                StringProvider.getString(R.string.face_enrollment_success)
            } else {
                state.faceDetectionStatus
            },
            style = if (state.isFaceEnrollmentDataReady) {
                TextStyle.Success
            } else {
                TextStyle.Message
            },
        )

        Spacer(modifier = Modifier.weight(1f))

        Column {
            PrimaryButton(
                text = StringProvider.getString(R.string.user_signup_enroll_face_button),
                enabled = state.isFaceEnrollmentDataReady && !state.isProcessingFace,
                onClick = onEnrollFaceClick,
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.back),
                onClick = onBackClick,
            )
        }
    }
}
