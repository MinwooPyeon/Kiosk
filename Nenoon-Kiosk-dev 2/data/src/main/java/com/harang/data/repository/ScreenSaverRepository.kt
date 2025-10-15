package com.harang.data.repository

import com.harang.data.datasource.SharedPreferencesDataSource
import com.harang.data.vo.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScreenSaverRepository(
    private val sharedPreferencesDataSource: SharedPreferencesDataSource
) {
    suspend fun getScreenSaverVideoURI(): String {
        val videoURI: String
        withContext(Dispatchers.IO) {
            videoURI = sharedPreferencesDataSource.getString(Constants.PREF_VIDEO_URI)
        }
        return videoURI
    }
}