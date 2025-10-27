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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.constants.NavConstants
import com.pixelro.nenoonkiosk.core.ui.CameraPreview
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton
import com.pixelro.nenoonkiosk.core.ui.StyledText
import com.pixelro.nenoonkiosk.core.ui.TextStyle
import com.pixelro.nenoonkiosk.core.util.StringProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.orbitmvi.orbit.compose.collectAsState
import org.orbitmvi.orbit.compose.collectSideEffect

@Composable
fun FaceUpdateRoute(
    navController: NavController,
    loggedInUserId: String?,
    viewModel: FaceUpdateViewModel = hiltViewModel()
) {
    val state = viewModel.collectAsState().value

    viewModel.collectSideEffect { sideEffect ->
        when (sideEffect) {
            is FaceUpdateSideEffect.ShowToast -> {
                // 토스트 표시 처리
            }

            is FaceUpdateSideEffect.UpdateSuccess -> {
                navController.popBackStack(NavConstants.ROUTE_ACCOUNT_MANAGEMENT, false)
            }

            is FaceUpdateSideEffect.UpdateFailed -> {
                // 실패 처리
            }

            is FaceUpdateSideEffect.NavigateBack -> {
                navController.popBackStack(NavConstants.ROUTE_ACCOUNT_MANAGEMENT, false)
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.resetFaceData()
    }

    FaceUpdateScreen(
        state = state,
        onCaptureClick = { liveBitmap ->
            viewModel.captureFace(liveBitmap)
        },
        onDetectionStatus = { status ->
            viewModel.updateFaceDetectionStatus(status)
        },
        onSaveClick = {
            // Screen에서 처리
        },
        onCancelClick = {
            viewModel.navigateBack()
        },
        loggedInUserId = loggedInUserId,
        saveFaceUpdate = { userId ->
            viewModel.saveFaceUpdate(userId)
        }
    )
}

@Composable
fun FaceUpdateScreen(
    state: FaceUpdateState,
    onCaptureClick: (Bitmap) -> Unit,
    onDetectionStatus: (String) -> Unit,
    onSaveClick: () -> Unit,
    onCancelClick: () -> Unit,
    loggedInUserId: String?,
    saveFaceUpdate: suspend (String) -> Boolean
) {
    var liveCameraBitmap: Bitmap? by remember { mutableStateOf(null) }
    var faceEnrollAttempted by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    DisposableEffect(Unit) {
        onDispose {
            liveCameraBitmap?.recycle()
            liveCameraBitmap = null
        }
    }

    Column(
        modifier = Modifier
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
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .aspectRatio(1f)
                .clip(MaterialTheme.shapes.medium)
                .align(Alignment.CenterHorizontally),
        ) {
            CameraPreview(
                modifier = Modifier.fillMaxSize(),
                onFaceDetected = { faceBitmap ->
                    liveCameraBitmap?.recycle()
                    liveCameraBitmap = faceBitmap.config?.let {
                        faceBitmap.copy(it, true)
                    }
                    faceBitmap.recycle()
                },
                onDetectionStatus = { status ->
                    onDetectionStatus(status)
                },
            )

            state.lastDetectedFaceBitmap?.let { bitmap ->
                if (!bitmap.isRecycled && state.isFaceEnrollmentDataReady) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = StringProvider.getString(
                            R.string.user_face_update_captured_face_description
                        ),
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
            text = state.currentScreenStatus,
            style = if (state.isFaceEnrollmentDataReady) {
                TextStyle.Success
            } else if (state.faceDetectionStatus.isEmpty()) {
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
                    StringProvider.getString(R.string.user_face_update_capture_button)
                } else {
                    StringProvider.getString(R.string.user_face_update_recapture_button)
                },
                onClick = {
                    if (liveCameraBitmap != null && !state.isProcessingFace) {
                        faceEnrollAttempted = true
                        onCaptureClick(liveCameraBitmap!!)
                    }
                },
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.user_face_update_save_button),
                onClick = {
                    if (state.isFaceEnrollmentDataReady &&
                        state.lastDetectedFaceBitmap != null &&
                        loggedInUserId != null
                    ) {
                        coroutineScope.launch(Dispatchers.Main) {
                            val success = saveFaceUpdate(loggedInUserId)
                            if (success) {
                                onSaveClick()
                            }
                        }
                    }
                },
                enabled = state.isFaceEnrollmentDataReady && !state.isProcessingFace,
            )

            Spacer(modifier = Modifier.height(20.dp))

            PrimaryButton(
                text = StringProvider.getString(R.string.cancel),
                onClick = onCancelClick,
            )
        }
    }
}