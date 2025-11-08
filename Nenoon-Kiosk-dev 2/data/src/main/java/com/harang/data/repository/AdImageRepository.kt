package com.harang.data.repository

import com.harang.data.db.dao.AdImageDao
import com.harang.data.db.entity.AdImageEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdImageRepository @Inject constructor(
    private val adImageDao: AdImageDao
) {
    /**
     * 모든 광고 이미지를 order 오름차순으로 가져옵니다.
     */
    fun getAllAdImages(): Flow<List<AdImageEntity>> {
        return adImageDao.getAllAdImages()
    }

    /**
     * 선택된 location에 해당하는 이미지를 order 오름차순으로 가져옵니다.
     * @param locationId 위치 ID (1: TEST_LIST_SCREEN, 2: SCREENSAVER)
     */
    fun getAdImagesByLocation(locationId: Int): Flow<List<AdImageEntity>> {
        return adImageDao.getAdImagesByLocation(locationId)
    }

    suspend fun getAdImageById(id: Int): AdImageEntity? {
        return adImageDao.getAdImageById(id)
    }

    suspend fun insertAdImage(adImage: AdImageEntity) {
        adImageDao.insertAdImage(adImage)
    }

    suspend fun insertAdImages(adImages: List<AdImageEntity>) {
        adImageDao.insertAdImages(adImages)
    }

    suspend fun updateAdImage(adImage: AdImageEntity) {
        adImageDao.updateAdImage(adImage)
    }

    suspend fun deleteAdImage(adImage: AdImageEntity) {
        adImageDao.deleteAdImage(adImage)
    }

    suspend fun deleteAdImagesByLocation(locationId: Int) {
        adImageDao.deleteAdImagesByLocation(locationId)
    }
}