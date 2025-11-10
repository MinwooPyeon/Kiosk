package com.harang.data.repository

import com.harang.data.datasource.SharedPreferencesDataSource
import com.harang.data.db.dao.AdImageDao
import com.harang.data.db.entity.AdImageEntity
import com.harang.data.vo.Constants
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ScreenSaverRepository(
    private val sharedPreferencesDataSource: SharedPreferencesDataSource,
    private val adImageDao: AdImageDao,
) {
    suspend fun getScreenSaverVideoURI(): String {
        val videoURI: String
        withContext(Dispatchers.IO) {
            videoURI = sharedPreferencesDataSource.getString(Constants.PREF_VIDEO_URI)
        }
        return videoURI
    }

    suspend fun getScreenSaverAds(): List<AdImageEntity> {
        return withContext(Dispatchers.IO) {
            adImageDao.getAdImagesByLocationOnce(locationId = 2)
        }
    }
}
