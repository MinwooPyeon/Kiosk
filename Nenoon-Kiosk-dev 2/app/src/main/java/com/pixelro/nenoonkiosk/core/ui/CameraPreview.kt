package com.pixelro.nenoonkiosk.core.ui

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Log
import android.util.Size
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.LinearLayout
import androidx.annotation.OptIn
import androidx.camera.core.AspectRatio
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import kotlin.math.max
import kotlin.math.min
import androidx.compose.ui.geometry.Rect as ComposeRect

@Composable
@OptIn(ExperimentalGetImage::class)
fun CameraPreview(
    modifier: Modifier = Modifier,
    onFaceDetected: (Bitmap) -> Unit,
    onDetectionStatus: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(context) }
    val cameraExecutor: ExecutorService = remember { Executors.newSingleThreadExecutor() }

    val faceBounds = remember { mutableStateListOf<ComposeRect>() }
    val primaryFaceBox = remember { mutableStateListOf<ComposeRect>() }

    DisposableEffect(Unit) {
        onDispose {
            cameraExecutor.shutdown()
            cameraProviderFuture.get().unbindAll()
        }
    }

    AndroidView(
        modifier =
            modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        factory = { ctx ->
            PreviewView(ctx).apply {
                layoutParams = LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT)
                implementationMode = PreviewView.ImplementationMode.COMPATIBLE
                scaleType = PreviewView.ScaleType.FILL_CENTER
            }.also { previewView ->
                val cameraProvider = cameraProviderFuture.get()

                val preview =
                    Preview.Builder()
                        .setTargetAspectRatio(AspectRatio.RATIO_4_3)
                        .build()
                        .also {
                            it.setSurfaceProvider(previewView.surfaceProvider)
                        }

                val faceDetectorOptions =
                    FaceDetectorOptions.Builder()
                        .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
                        .setMinFaceSize(0.15f)
                        .build()

                val faceDetector = FaceDetection.getClient(faceDetectorOptions)

                val imageAnalysis =
                    ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .setTargetResolution(Size(640, 480))
                        .build()
                        .also {
                            it.setAnalyzer(cameraExecutor) { imageProxy ->
                                val rotationDegrees = imageProxy.imageInfo.rotationDegrees
                                val mediaImage = imageProxy.image

                                if (mediaImage != null) {
                                    val originalBitmap = imageToBitmap(mediaImage, rotationDegrees)

                                    if (originalBitmap != null) {
                                        val inputImage = InputImage.fromBitmap(originalBitmap, 0)

                                        faceDetector.process(inputImage)
                                            .addOnSuccessListener { faces ->
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    if (faces.isNotEmpty()) {
                                                        onDetectionStatus(
                                                            StringProvider.getString(
                                                                R.string.face_detection_status_detected,
                                                                "${faces.size}",
                                                            ),
                                                        )
                                                        val largestFace = faces.maxByOrNull { it.boundingBox.width() * it.boundingBox.height() }

                                                        faceBounds.clear()
                                                        primaryFaceBox.clear()

                                                        largestFace?.let { face ->
                                                            val bounds = face.boundingBox

                                                            val scaleX = previewView.width.toFloat() / originalBitmap.width.toFloat()
                                                            val scaleY = previewView.height.toFloat() / originalBitmap.height.toFloat()

                                                            for (detectedFace in faces) {
                                                                val detectedBounds = detectedFace.boundingBox
                                                                faceBounds.add(
                                                                    ComposeRect(
                                                                        detectedBounds.left * scaleX,
                                                                        detectedBounds.top * scaleY,
                                                                        detectedBounds.right * scaleX,
                                                                        detectedBounds.bottom * scaleY,
                                                                    ),
                                                                )
                                                            }

                                                            primaryFaceBox.add(
                                                                ComposeRect(
                                                                    bounds.left * scaleX,
                                                                    bounds.top * scaleY,
                                                                    bounds.right * scaleX,
                                                                    bounds.bottom * scaleY,
                                                                ),
                                                            )

                                                            val cropX = max(0, bounds.left)
                                                            val cropY = max(0, bounds.top)
                                                            val cropWidth = min(originalBitmap.width - cropX, bounds.width())
                                                            val cropHeight = min(originalBitmap.height - cropY, bounds.height())

                                                            if (cropWidth > 0 && cropHeight > 0) {
                                                                val croppedBitmap =
                                                                    Bitmap.createBitmap(
                                                                        originalBitmap,
                                                                        cropX,
                                                                        cropY,
                                                                        cropWidth,
                                                                        cropHeight,
                                                                    )
                                                                onFaceDetected(croppedBitmap)
                                                            } else {
                                                                onDetectionStatus(StringProvider.getString(R.string.face_detection_status_invalid_area))
                                                            }
                                                        } ?: onDetectionStatus(StringProvider.getString(R.string.face_detection_status_size_issue))
                                                    } else {
                                                        onDetectionStatus(StringProvider.getString(R.string.face_detection_status_no_face))
                                                        faceBounds.clear()
                                                        primaryFaceBox.clear()
                                                    }
                                                    originalBitmap.recycle()
                                                    imageProxy.close()
                                                }
                                            }
                                            .addOnFailureListener { e ->
                                                CoroutineScope(Dispatchers.Main).launch {
                                                    onDetectionStatus(
                                                        StringProvider.getString(
                                                            R.string.face_detection_error,
                                                            e.message ?: "Unknown error",
                                                        ),
                                                    )
                                                }
                                                Log.e("CameraPreview", "Face detection failed", e)
                                                originalBitmap.recycle()
                                                imageProxy.close()
                                            }
                                    } else {
                                        onDetectionStatus(StringProvider.getString(R.string.image_conversion_failed))
                                        imageProxy.close()
                                    }
                                } else {
                                    imageProxy.close()
                                }
                            }
                        }

                val cameraSelector =
                    CameraSelector.Builder()
                        .requireLensFacing(CameraSelector.LENS_FACING_FRONT)
                        .build()

                try {
                    cameraProvider.unbindAll()

                    cameraProvider.bindToLifecycle(
                        lifecycleOwner,
                        cameraSelector,
                        preview,
                        imageAnalysis,
                    )
                } catch (exc: Exception) {
                    Log.e("CameraPreview", "Camera binding failed", exc)
                    onDetectionStatus(StringProvider.getString(R.string.camera_init_failed))
                }
            }
        },
    )

    Canvas(modifier = Modifier.fillMaxSize()) {
        val mlKitPaint =
            Paint().apply {
                color = Color.GREEN
                style = Paint.Style.STROKE
                strokeWidth = 5f
            }

        faceBounds.forEach { rect ->
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawRect(
                    rect.left,
                    rect.top,
                    rect.right,
                    rect.bottom,
                    mlKitPaint,
                )
            }
        }

        val primaryFacePaint =
            Paint().apply {
                color = Color.BLUE
                style = Paint.Style.STROKE
                strokeWidth = 10f
            }

        primaryFaceBox.forEach { rect ->
            drawIntoCanvas { canvas ->
                canvas.nativeCanvas.drawRect(
                    rect.left,
                    rect.top,
                    rect.right,
                    rect.bottom,
                    primaryFacePaint,
                )
            }
        }
    }
}

private fun imageToBitmap(
    image: Image,
    rotationDegrees: Int,
): Bitmap? {
    if (image.format != ImageFormat.YUV_420_888) {
        Log.e("CameraPreview", "Unsupported image format")
        return null
    }

    val yBuffer = image.planes[0].buffer
    val uBuffer = image.planes[1].buffer
    val vBuffer = image.planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
    val out = ByteArrayOutputStream()
    yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 75, out)
    val imageBytes = out.toByteArray()
    val originalBitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size) ?: return null

    val matrix = Matrix()
    matrix.postRotate(rotationDegrees.toFloat())

    matrix.postScale(-1f, 1f, originalBitmap.width / 2f, originalBitmap.height / 2f)

    val rotatedBitmap =
        Bitmap.createBitmap(
            originalBitmap,
            0,
            0,
            originalBitmap.width,
            originalBitmap.height,
            matrix,
            true,
        )
    originalBitmap.recycle()
    return rotatedBitmap
}
