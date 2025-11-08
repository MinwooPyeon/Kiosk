package com.harang.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.harang.data.db.dao.AdImageDao
import com.harang.data.db.dao.LocationDao
import com.harang.data.db.entity.AdImageEntity
import com.harang.data.db.entity.LocationEntity

@Database(
    entities = [
        LocationEntity::class,
        AdImageEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun locationDao(): LocationDao
    abstract fun adImageDao(): AdImageDao
}