package com.pixelro.nenoonkiosk.feature.facedetection

import android.Manifest
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.pixelro.nenoonkiosk.core.manager.detection.MyFaceAnalyzer
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceDetection() {
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
    FaceDetectionScreenContent()
}


@Composable
fun FaceDetectionScreenContent(viewModel: FaceDetectionViewModel = hiltViewModel()) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    Box {
        LaunchedEffect(true) {
            val executor = ContextCompat.getMainExecutor(context)
            val executor1 = Executors.newSingleThreadExecutor()
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector =
                    CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
                val imageAnalysis =
                    ImageAnalysis.Builder()
                        .setTargetResolution(Size(1000, 1000))
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setImageQueueDepth(5).build().apply {
                            setAnalyzer(
                                executor1,
                                MyFaceAnalyzer(
                                    viewModel::updateFaceDetectionData,
                                    viewModel::updateTextRecognitionData,
                                    viewModel::updateIsFaceDetected,
                                    viewModel::updateIsNenoonTextDetected,
                                    viewModel::onGazeResultDetected,
                                    executor1,
                                ),
                            )
                        }
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    imageAnalysis,
                )
            }, executor)
        }
    }
}


