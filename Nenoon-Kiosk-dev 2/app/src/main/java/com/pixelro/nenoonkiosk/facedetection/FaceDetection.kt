package com.pixelro.nenoonkiosk.facedetection

import android.Manifest
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.newSingleThreadContext
import java.util.concurrent.Executors

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceDetection() {

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
        )
    )

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }
    FaceDetectionScreenContent()
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun FaceDetectionWithPreview(
    isPreviewShowing: Boolean
) {

    val permissionState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.CAMERA, Manifest.permission.RECORD_AUDIO
        )
    )

    LaunchedEffect(Unit) {
        permissionState.launchMultiplePermissionRequest()
    }
    FaceDetectionScreenContentWithPreview(
        isPreviewShowing = isPreviewShowing
    )
}

@Composable
fun FaceDetectionScreenContent(
    viewModel: FaceDetectionViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    Box() {
        LaunchedEffect(true) {
            val executor = ContextCompat.getMainExecutor(context)
            val executor1 = Executors.newSingleThreadExecutor()
            cameraProviderFuture.addListener({
                val cameraProvider = cameraProviderFuture.get()
                val cameraSelector = CameraSelector.Builder()
                    .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
                val imageAnalysis = ImageAnalysis.Builder()
                    .setTargetResolution(Size(1000, 1000))
                    .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                    .setImageQueueDepth(5).build().apply {
                        setAnalyzer(
                            executor1, MyFaceAnalyzer(
                                viewModel::updateFaceDetectionData,
                                viewModel::updateTextRecognitionData,
                                viewModel::updateIsFaceDetected,
                                viewModel::updateIsNenoonTextDetected,
                                executor1
                            )
                        )
                    }
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, imageAnalysis
                )
            }, executor)
        }
    }
}

@Composable
fun FaceDetectionScreenContentWithPreview(
    isPreviewShowing: Boolean,
    viewModel: FaceDetectionViewModel = hiltViewModel()
) {
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    Surface() {
        if (isPreviewShowing) {
            AndroidView(
                modifier = Modifier
                    .width(600.dp)
                    .height(800.dp),
                factory = { context ->
                    val previewView = PreviewView(context)
                    previewView.scaleType = PreviewView.ScaleType.FILL_END
                    val executor = ContextCompat.getMainExecutor(context)
                    val executor1 = Executors.newSingleThreadExecutor()
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview = Preview.Builder().build().also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }
                        val imageAnalysis = ImageAnalysis.Builder()
                            .setTargetResolution(Size(1000, 1000))
                            .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                            .setImageQueueDepth(5).build().apply {
                                setAnalyzer(
                                    executor1, MyFaceAnalyzer(
                                        viewModel::updateFaceDetectionData,
                                        viewModel::updateTextRecognitionData,
                                        viewModel::updateIsFaceDetected,
                                        viewModel::updateIsNenoonTextDetected,
                                        executor1
                                    )
                                )
                            }
                        val cameraSelector = CameraSelector.Builder()
                            .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner, cameraSelector, preview, imageAnalysis)
                    }, executor)
                    previewView
                }
            )
        }
    }
}