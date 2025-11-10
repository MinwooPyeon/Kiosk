package com.harang.data.repository

import android.graphics.Bitmap
import com.harang.data.db.dao.AdImageDao
import com.harang.data.db.entity.AdImageEntity
import com.harang.data.util.FileManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AdImageRepository @Inject constructor(
    private val adImageDao: AdImageDao,
    private val fileManager: FileManager
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

    /**
     * 선택된 location과 언어에 해당하는 이미지를 order 오름차순으로 가져옵니다.
     * 해당 언어의 광고가 없으면 영어("en") 광고를 fallback으로 반환합니다.
     * @param locationId 위치 ID (1: TEST_LIST_SCREEN, 2: SCREENSAVER)
     * @param language 언어 코드 ("ko", "en" 등)
     */
    fun getAdImagesByLocationAndLanguage(locationId: Int, language: String): Flow<List<AdImageEntity>> {
        return adImageDao.getAdImagesByLocationAndLanguage(locationId, language)
            .flatMapLatest { images ->
                // 해당 언어의 광고가 없고, 영어가 아닌 경우 영어 광고를 fallback으로 사용
                if (images.isEmpty() && language != "en") {
                    adImageDao.getAdImagesByLocationAndLanguage(locationId, "en")
                } else {
                    flowOf(images)
                }
            }
    }

    suspend fun getAdImageById(id: Int): AdImageEntity? {
        return adImageDao.getAdImageById(id)
    }

    /**
     * 새로운 광고 이미지를 추가합니다.
     * 이미지를 내부 저장소에 저장하고, DB에는 파일 경로를 저장합니다.
     * @param bitmap 저장할 이미지 Bitmap
     * @param locationId 위치 ID
     * @param order 순서
     * @param fileName 파일 이름
     */
    suspend fun insertAdImage(bitmap: Bitmap, locationId: Int, order: Int, fileName: String) {
        // 1. 이미지를 내부 저장소에 저장하고 경로를 가져옵니다.
        val imagePath = fileManager.saveBitmapToInternalStorage(bitmap, "ad_images", fileName)

        // 2. 경로가 성공적으로 생성되었을 경우에만 DB에 저장합니다.
        imagePath?.let { path ->
            val adImage = AdImageEntity(
                url = path, // DB에는 파일 경로(URL)를 저장
                name = fileName,
                locationId = locationId,
                order = order
            )
            adImageDao.insertAdImage(adImage)
        }
    }


    suspend fun insertAdImages(adImages: List<AdImageEntity>) {
        adImageDao.insertAdImages(adImages)
    }

    suspend fun updateAdImage(adImage: AdImageEntity) {
        adImageDao.updateAdImage(adImage)
    }

    suspend fun deleteAdImage(adImage: AdImageEntity) {
        // 1. 실제 이미지 파일을 먼저 삭제합니다.
        fileManager.deleteFile(adImage.url)
        // 2. DB에서 해당 이미지 정보를 삭제합니다.
        adImageDao.deleteAdImage(adImage)
    }

    suspend fun deleteAdImagesByLocation(locationId: Int) {
        // 1. 삭제할 이미지 정보 목록을 DB에서 가져옵니다.
        val imagesToDelete = adImageDao.getAdImagesByLocationOnce(locationId)

        // 2. 각 이미지에 대해 실제 파일을 삭제합니다.
        imagesToDelete.forEach { entity ->
            fileManager.deleteFile(entity.url)
        }

        // 3. DB에서 locationId에 해당하는 모든 이미지 정보를 삭제합니다.
        adImageDao.deleteAdImagesByLocation(locationId)
    }
}