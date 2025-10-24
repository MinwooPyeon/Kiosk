package com.pixelro.nenoonkiosk.core.util.qr

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.google.zxing.common.BitMatrix
import java.io.UnsupportedEncodingException

object QRCodeGenerator {
    /**
     * Generates a QR code Bitmap from the given text.
     *
     * @param text The data to encode in the QR code.
     * @param width The desired width of the QR code in pixels.
     * @param height The desired height of the QR code in pixels.
     * @return A Bitmap representing the QR code, or null if an error occurs.
     */
    fun generateQrCode(
        text: String,
        width: Int,
        height: Int,
    ): Bitmap? {
        try {
            val bitMatrix: BitMatrix =
                MultiFormatWriter().encode(
                    text,
                    BarcodeFormat.QR_CODE,
                    width,
                    height,
                )

            val pixels = IntArray(width * height)
            for (y in 0 until height) {
                for (x in 0 until width) {
                    pixels[y * width + x] = if (bitMatrix[x, y]) Color.BLACK else Color.WHITE
                }
            }

            return Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888).apply {
                setPixels(pixels, 0, width, 0, 0, width, height)
            }
        } catch (e: WriterException) {
            e.printStackTrace()
            // Handle the exception (e.g., log it, show a toast)
            return null
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
            // Handle the exception
            return null
        }
    }
}
