package com.harang.data.di

import android.content.Context
import androidx.room.Room
import com.harang.data.db.AppDatabase
import com.harang.data.db.DatabaseCallback
import com.harang.data.db.dao.AdImageDao
import com.harang.data.db.dao.LocationDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context,
        callback: DatabaseCallback
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "nenoon_kiosk_database"
        ).addCallback(callback)
        .build()
    }

    @Provides
    @Singleton
    fun provideLocationDao(database: AppDatabase): LocationDao {
        return database.locationDao()
    }

    @Provides
    @Singleton
    fun provideAdImageDao(database: AppDatabase): AdImageDao {
        return database.adImageDao()
    }
}