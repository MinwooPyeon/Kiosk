package com.harang.data.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FileManager @Inject constructor(
    @ApplicationContext private val context: Context
) {

    /**
     * Bitmap 이미지를 앱 내부 저장소에 저장하고 파일 경로를 반환합니다.
     * @param bitmap 저장할 이미지의 Bitmap 객체
     * @param childPath 저장할 하위 폴더 이름 (예: "ad_images")
     * @param fileName 저장할 파일 이름 (확장자 제외)
     * @return 저장된 파일의 절대 경로. 실패 시 null을 반환합니다.
     */
    fun saveBitmapToInternalStorage(bitmap: Bitmap, childPath: String, fileName: String): String? {
        val directory = context.getDir(childPath, Context.MODE_PRIVATE)
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, "$fileName.jpg")

        return try {
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }
            file.absolutePath // 저장된 파일의 전체 경로 반환
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * drawable 리소스를 앱 내부 저장소로 복사하고 파일 경로를 반환합니다.
     * @param resourceId drawable 리소스 ID
     * @param childPath 저장할 하위 폴더 이름 (예: "ad_images")
     * @param fileName 저장할 파일 이름 (확장자 제외)
     * @return 저장된 파일의 절대 경로. 실패 시 null을 반환합니다.
     */
    fun copyDrawableToInternalStorage(resourceId: Int, childPath: String, fileName: String): String? {
        return try {
            val bitmap = BitmapFactory.decodeResource(context.resources, resourceId)
            val directory = context.getDir(childPath, Context.MODE_PRIVATE)
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, "$fileName.jpg")
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * assets 폴더의 이미지를 앱 내부 저장소로 복사하고 파일 경로를 반환합니다.
     * @param assetFileName assets 폴더의 파일 이름 (예: "ad_lens.png")
     * @param childPath 저장할 하위 폴더 이름 (예: "ad_images")
     * @param outputFileName 저장할 파일 이름 (확장자 제외)
     * @return 저장된 파일의 절대 경로. 실패 시 null을 반환합니다.
     */
    fun copyAssetToInternalStorage(assetFileName: String, childPath: String, outputFileName: String): String? {
        return try {
            val inputStream = context.assets.open(assetFileName)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            val directory = context.getDir(childPath, Context.MODE_PRIVATE)
            if (!directory.exists()) {
                directory.mkdirs()
            }

            val file = File(directory, "$outputFileName.jpg")
            FileOutputStream(file).use { fos ->
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            }
            file.absolutePath
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 주어진 경로의 파일을 삭제합니다.
     * @param filePath 삭제할 파일의 절대 경로
     * @return 파일 삭제 성공 여부
     */
    fun deleteFile(filePath: String?): Boolean {
        if (filePath.isNullOrEmpty()) return false

        return try {
            val file = File(filePath)
            if (file.exists()) {
                file.delete()
            } else {
                // 파일이 존재하지 않아도, 목적(파일 없음)은 달성되었으므로 true 반환 가능
                true
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}