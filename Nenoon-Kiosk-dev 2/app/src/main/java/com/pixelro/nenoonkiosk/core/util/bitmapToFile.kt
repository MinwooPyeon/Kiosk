package com.pixelro.nenoonkiosk.core.util

import android.content.Context
import android.graphics.Bitmap
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

fun bitmapToFile(
    context: Context,
    bitmap: Bitmap,
    fileName: String,
): File {
    val filesDir = context.cacheDir
    val file = File(filesDir, fileName)

    val bos = ByteArrayOutputStream()
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
    val bitmapdata = bos.toByteArray()

    var fos: FileOutputStream? = null
    try {
        fos = FileOutputStream(file)
        fos.write(bitmapdata)
        fos.flush()
    } catch (e: Exception) {
        e.printStackTrace()
    } finally {
        try {
            fos?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    return file
}
