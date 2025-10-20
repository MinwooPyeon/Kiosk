package com.pixelro.nenoonkiosk.feature.user

import android.app.Application
import android.graphics.Bitmap

interface FaceRecognizer {
    fun initialize(application: Application)
    suspend fun getFaceEmbedding(bitmap: Bitmap): FloatArray?
    fun compareEmbeddings(embedding1: FloatArray, embedding2: FloatArray): Float
    fun shutdown()
}
