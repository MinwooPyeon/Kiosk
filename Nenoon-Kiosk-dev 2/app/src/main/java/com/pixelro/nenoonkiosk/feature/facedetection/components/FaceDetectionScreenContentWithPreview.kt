package com.pixelro.nenoonkiosk.feature.facedetection.components

import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.pixelro.nenoonkiosk.core.manager.detection.MyFaceAnalyzer
import com.pixelro.nenoonkiosk.feature.facedetection.FaceDetectionViewModel
import java.util.concurrent.Executors

@Composable
fun FaceDetectionScreenContentWithPreview(
    isPreviewShowing: Boolean,
    viewModel: FaceDetectionViewModel? = null,
) {
    val isInspectionMode = LocalInspectionMode.current

    // Preview 모드에서는 항상 회색 박스만 표시
    if (isInspectionMode) {
        Surface {
            Box(
                modifier =
                    Modifier
                        .width(600.dp)
                        .height(600.dp)
                        .background(Color.Gray)
            )
        }
        return
    }

    // 실제 실행 환경에서만 ViewModel 사용
    val actualViewModel = viewModel ?: hiltViewModel()
    val lifecycleOwner = LocalLifecycleOwner.current
    val context = LocalContext.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }

    Surface {
        if (isPreviewShowing) {
            AndroidView(
                modifier =
                    Modifier
                        .width(600.dp)
                        .height(600.dp),
                factory = { context ->
                    val previewView = PreviewView(context)
                    previewView.scaleType = PreviewView.ScaleType.FILL_CENTER
                    val executor = ContextCompat.getMainExecutor(context)
                    val executor1 = Executors.newSingleThreadExecutor()
                    cameraProviderFuture.addListener({
                        val cameraProvider = cameraProviderFuture.get()
                        val preview =
                            Preview.Builder().build().also {
                                it.surfaceProvider = previewView.surfaceProvider
                            }
                        val resolutionSelector = ResolutionSelector.Builder()
                            .setResolutionStrategy(
                                ResolutionStrategy(
                                    Size(1000, 1000),
                                    ResolutionStrategy.FALLBACK_RULE_CLOSEST_HIGHER_THEN_LOWER
                                )
                            )
                            .build()
                        val imageAnalysis =
                            ImageAnalysis.Builder()
                                .setResolutionSelector(resolutionSelector)
                                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                                .setImageQueueDepth(5).build().apply {
                                    setAnalyzer(
                                        executor1,
                                        MyFaceAnalyzer(
                                            actualViewModel::updateFaceDetectionData,
                                            actualViewModel::updateTextRecognitionData,
                                            actualViewModel::updateIsFaceDetected,
                                            actualViewModel::updateIsNenoonTextDetected,
                                            actualViewModel::onGazeResultDetected,
                                            executor1,
                                            actualViewModel::updateInputImageSize,
                                        ),
                                    )
                                }
                        val cameraSelector =
                            CameraSelector.Builder()
                                .requireLensFacing(CameraSelector.LENS_FACING_FRONT).build()
                        cameraProvider.unbindAll()
                        cameraProvider.bindToLifecycle(
                            lifecycleOwner,
                            cameraSelector,
                            preview,
                            imageAnalysis,
                        )
                    }, executor)
                    previewView
                },
            )
        } else {//프리뷰 모드
            Box(
                modifier =
                    Modifier
                        .width(600.dp)
                        .height(600.dp)
                        .background(Color.Gray)
            )
        }
    }
}