package com.pixelro.nenoonkiosk.feature.facedetection.components

import android.Manifest
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceDetectionWithPreview(isPreviewShowing: Boolean) {
    val permissionState =
        rememberMultiplePermissionsState(
            permissions =
                listOf(
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO,
                ),
        )

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }
    FaceDetectionScreenContentWithPreview(
        isPreviewShowing = isPreviewShowing,
    )
}
