package com.pixelro.nenoonkiosk.core.di

import com.harang.data.api.AuthApi
import com.harang.data.api.NenoonKioskApi
import com.harang.data.api.SurveyApi
import com.harang.data.datasource.SignInRemoteDataSource
import com.harang.data.datasource.SurveyRemoteDataSource
import com.harang.data.datasource.TestResultRemoteDataSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RemoteDataSourceModule {
    @Provides
    @Singleton
    fun provideSignInRemoteDataSource(api: AuthApi): SignInRemoteDataSource {
        return SignInRemoteDataSource(api)
    }

    @Provides
    @Singleton
    fun provideSurveyRemoteDataSource(api: SurveyApi): SurveyRemoteDataSource {
        return SurveyRemoteDataSource(api)
    }

    @Provides
    @Singleton
    fun provideTestResultRemoteDataSource(api: NenoonKioskApi): TestResultRemoteDataSource {
        return TestResultRemoteDataSource(api)
    }
}
