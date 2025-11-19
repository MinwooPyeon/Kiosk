package com.pixelro.nenoonkiosk.core.util.qr

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.zxing.BinaryBitmap
import com.google.zxing.MultiFormatReader
import com.google.zxing.NotFoundException
import com.google.zxing.PlanarYUVLuminanceSource
import com.google.zxing.common.HybridBinarizer
import java.nio.ByteBuffer

class QRScannerAnalyzer(
    private val onQrCodeScanned: (String) -> Unit,
) : ImageAnalysis.Analyzer {
    private val reader = MultiFormatReader()

    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val yBuffer: ByteBuffer = imageProxy.planes[0].buffer // Y plane contains luminance data
        val ySize = yBuffer.remaining()
        val yBytes = ByteArray(ySize)
        yBuffer.get(yBytes)

        val width = imageProxy.width
        val height = imageProxy.height

        val source =
            PlanarYUVLuminanceSource(
                yBytes,
                width,
                height,
                0,
                0,
                width,
                height,
                false,
                // Invert colors if necessary, usually false
            )

        val binaryBitmap = BinaryBitmap(HybridBinarizer(source))

        try {
            val result = reader.decode(binaryBitmap)
            onQrCodeScanned(result.text)
        } catch (e: NotFoundException) {
            // No QR code found in this frame, continue scanning
        } finally {
            imageProxy.close() // IMPORTANT: Close the image proxy to release the buffer
        }
    }
}
