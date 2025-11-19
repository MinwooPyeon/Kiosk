package com.pixelro.nenoonkiosk.core.di

import com.harang.data.api.AuthApi
import com.harang.data.api.NenoonKioskApi
import com.harang.data.api.SurveyApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ApiModule {
    @Provides
    @Singleton
    fun provideNenoonApi(retrofit: Retrofit): NenoonKioskApi {
        return retrofit.create(NenoonKioskApi::class.java)
    }

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): AuthApi {
        return retrofit.create(AuthApi::class.java)
    }
    @Provides
    @Singleton
    fun provideSurveyApi(retrofit: Retrofit): SurveyApi {
        return retrofit.create(SurveyApi::class.java)
    }
}