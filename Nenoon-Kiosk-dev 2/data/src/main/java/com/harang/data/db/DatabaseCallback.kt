package com.harang.data.db

import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.harang.data.db.entity.AdImageEntity
import com.harang.data.db.entity.LocationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class
DatabaseCallback : RoomDatabase.Callback() {
    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        // 초기 데이터 삽입은 DatabaseModule에서 처리
    }

    companion object {
        fun prepopulateDatabase(database: AppDatabase) {
            val locationDao = database.locationDao()
            val adImageDao = database.adImageDao()

            CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
                // Location 초기 데이터 삽입
                val locations = listOf(
                    LocationEntity(id = 1, name = "TEST_LIST_SCREEN"),
                    LocationEntity(id = 2, name = "SCREENSAVER")
                )
                locationDao.insertLocations(locations)

                // AdImage 더미 데이터 삽입
                val adImages = listOf(
                    // TEST_LIST_SCREEN용 이미지
                    AdImageEntity(
                        locationId = 1,
                        name = "테스트 목록 이미지 1",
                        url = "https://example.com/test1.jpg",
                        order = 1
                    ),
                    AdImageEntity(
                        locationId = 1,
                        name = "테스트 목록 이미지 2",
                        url = "https://example.com/test2.jpg",
                        order = 2
                    ),
                    AdImageEntity(
                        locationId = 1,
                        name = "테스트 목록 이미지 3",
                        url = "https://example.com/test3.jpg",
                        order = 3
                    ), AdImageEntity(
                        locationId = 1,
                        name = "테스트 목록 이미지 4",
                        url = "https://example.com/test4.jpg",
                        order = 4
                    ),
                    AdImageEntity(
                        locationId = 1,
                        name = "테스트 목록 이미지 5",
                        url = "https://example.com/test5.jpg",
                        order = 5
                    ),
                    // SCREENSAVER용 이미지
                    AdImageEntity(
                        locationId = 2,
                        name = "스크린세이버 이미지 1",
                        url = "https://example.com/screensaver1.jpg",
                        order = 1
                    ),
                    AdImageEntity(
                        locationId = 2,
                        name = "스크린세이버 이미지 2",
                        url = "https://example.com/screensaver2.jpg",
                        order = 2
                    ),
                    AdImageEntity(
                        locationId = 2,
                        name = "스크린세이버 이미지 3",
                        url = "https://example.com/screensaver3.jpg",
                        order = 3
                    )
                )
                adImageDao.insertAdImages(adImages)
            }
        }
    }
}