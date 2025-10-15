package com.pixelro.nenoonkiosk.user

import android.app.Application
import android.graphics.Bitmap
import android.util.Log
import androidx.core.graphics.scale
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.sqrt
import org.tensorflow.lite.Interpreter.Options as InterpreterOptions

@Singleton
class TFLiteFaceRecognizer @Inject constructor(
    private val application: Application
) : FaceRecognizer {

    private val TAG = "TFLiteFaceRecognizer"
    
    private var interpreter: Interpreter? = null
    private var mlKitFaceDetector: FaceDetector? = null

    private val modelInputSize = 112
    private val embeddingSize = 192
    private val inputMean = 127.5f
    private val inputStd = 127.5f
    private val modelFileName = "mobile_face_net.tflite"

    override fun initialize(application: Application) {
        if (interpreter != null && mlKitFaceDetector != null) {
            Log.d(TAG, "FaceRecognizer already initialized.")
            return
        }

        try {
            val modelByteBuffer = loadModelFile(application.assets, modelFileName)
            val options = InterpreterOptions()

            val compatList = CompatibilityList()

            if (compatList.isDelegateSupportedOnThisDevice) {
                val delegateOptions = compatList.bestOptionsForThisDevice
                options.addDelegate(GpuDelegate(delegateOptions))
            } else {
                Log.w(TAG, "GPU delegate not available, falling back to CPU.")
            }

            options.setNumThreads(4)

            interpreter = Interpreter(modelByteBuffer, options)

            val faceDetectorOptions = FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setMinFaceSize(0.15f)
                .build()

            mlKitFaceDetector = FaceDetection.getClient(faceDetectorOptions)

            Log.d(TAG, "FaceRecognizer initialized successfully.")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FaceRecognizer: ${e.message}", e)
            interpreter = null
            mlKitFaceDetector = null
        }
    }

    private fun loadModelFile(assets: android.content.res.AssetManager, modelPath: String): MappedByteBuffer {
        val fileDescriptor = assets.openFd(modelPath)
        val inputStream = FileInputStream(fileDescriptor.fileDescriptor)
        val fileChannel = inputStream.channel
        val startOffset = fileDescriptor.startOffset
        val declaredLength = fileDescriptor.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
    }

    private fun preprocessBitmap(originalBitmap: Bitmap): ByteBuffer {
        val resizedBitmap = originalBitmap.scale(modelInputSize, modelInputSize)
        val byteBuffer = ByteBuffer.allocateDirect(1 * modelInputSize * modelInputSize * 3 * 4)
        byteBuffer.order(ByteOrder.nativeOrder())
        val intValues = IntArray(modelInputSize * modelInputSize)
        resizedBitmap.getPixels(intValues, 0, resizedBitmap.width, 0, 0, resizedBitmap.width, resizedBitmap.height)

        for (i in 0 until modelInputSize) {
            for (j in 0 until modelInputSize) {
                val pixelVal = intValues[i * modelInputSize + j]
                byteBuffer.putFloat(((pixelVal shr 16 and 0xFF) - inputMean) / inputStd)
                byteBuffer.putFloat(((pixelVal shr 8 and 0xFF) - inputMean) / inputStd)
                byteBuffer.putFloat(((pixelVal and 0xFF) - inputMean) / inputStd)
            }
        }
        return byteBuffer
    }

    override suspend fun getFaceEmbedding(bitmap: Bitmap): FloatArray? {
        if (interpreter == null) {
            Log.e(TAG, "Interpreter not initialized or shut down unexpectedly.")
            return null
        }

        val preprocessedBuffer = preprocessBitmap(bitmap)
        val output = Array(1) { FloatArray(embeddingSize) }

        return try {
            interpreter?.run(preprocessedBuffer, output)
            output[0]
        } catch (e: Exception) {
            Log.e(TAG, "Error running TFLite inference: ${e.message}", e)
            null
        }
    }

    override fun compareEmbeddings(embedding1: FloatArray, embedding2: FloatArray): Float {
        if (embedding1.size != embedding2.size || embedding1.isEmpty()) {
            return 0.0f
        }

        var dotProduct = 0.0f
        var norm1 = 0.0f
        var norm2 = 0.0f

        for (i in embedding1.indices) {
            dotProduct += embedding1[i] * embedding2[i]
            norm1 += embedding1[i] * embedding1[i]
            norm2 += embedding2[i] * embedding2[i]
        }

        val denominator = sqrt(norm1) * sqrt(norm2)
        return if (denominator == 0.0f) 0.0f else (dotProduct / denominator)
    }

    override fun shutdown() {
        Log.d(TAG, "Shutting down FaceRecognizer.")
        interpreter?.close()
        interpreter = null
        mlKitFaceDetector?.close()
        mlKitFaceDetector = null
    }
}