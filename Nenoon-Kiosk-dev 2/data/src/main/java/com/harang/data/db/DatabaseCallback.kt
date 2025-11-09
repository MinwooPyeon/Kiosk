package com.harang.data.db

import android.content.Context
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.harang.data.db.entity.AdImageEntity
import com.harang.data.db.entity.LocationEntity
import com.harang.data.util.FileManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
class DatabaseCallback @Inject constructor(
    @ApplicationContext private val context: Context,
    private val fileManager: FileManager,
    private val databaseProvider: Provider<AppDatabase>
) : RoomDatabase.Callback() {

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // 데이터베이스가 생성된 직후 초기 데이터 삽입
        prepopulateDatabase()
    }

    private fun prepopulateDatabase() {
        applicationScope.launch {
            val database = databaseProvider.get()
            val locationDao = database.locationDao()
            val adImageDao = database.adImageDao()

            // Location 초기 데이터 삽입
            val locations = listOf(
                LocationEntity(id = 1, name = "TEST_LIST_SCREEN"),
                LocationEntity(id = 2, name = "SCREENSAVER")
            )
            locationDao.insertLocations(locations)

            // assets 폴더의 이미지를 내부 저장소로 복사
            val adLensPath = fileManager.copyAssetToInternalStorage("ad_lens.png", "ad_images", "ad_lens")
            val adHadesPath = fileManager.copyAssetToInternalStorage("ad_hades.png", "ad_images", "ad_hades")
            val adHadesEnPath = fileManager.copyAssetToInternalStorage("ad_hades_en.png", "ad_images", "ad_hades_en")

            // AdImage 초기 데이터 삽입
            val adImages = mutableListOf<AdImageEntity>()

            // ad_lens를 TEST_LIST_SCREEN에 추가 (모든 언어)
            adLensPath?.let { path ->
                adImages.add(
                    AdImageEntity(
                        locationId = 1,
                        name = File(path).name,
                        url = path,
                        order = 1,
                        language = "ko" // 모든 언어에 표시
                    )
                )
            }

            // ad_hades를 TEST_LIST_SCREEN에 추가 (한국어 전용)
            adHadesPath?.let { path ->
                adImages.add(
                    AdImageEntity(
                        locationId = 1,
                        name = File(path).name,
                        url = path,
                        order = 2,
                        language = "ko" // 한국어 전용
                    )
                )
            }

            // ad_hades_en을 TEST_LIST_SCREEN에 추가 (영어 전용)
            adHadesEnPath?.let { path ->
                adImages.add(
                    AdImageEntity(
                        locationId = 1,
                        name = File(path).name,
                        url = path,
                        order = 3,
                        language = "en" // 영어 전용
                    )
                )
            }

            if (adImages.isNotEmpty()) {
                adImageDao.insertAdImages(adImages)
            }
        }
    }
}