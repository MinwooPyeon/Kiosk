package com.pixelro.nenoonkiosk.di

import android.content.Context
import com.harang.data.datasource.SharedPreferencesDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object SharedPreferencesDataSourceModule {

    @Provides
    fun provideSharedPreferencesDataSource(@ApplicationContext context: Context): SharedPreferencesDataSource {
        return SharedPreferencesDataSource(context)
    }
}