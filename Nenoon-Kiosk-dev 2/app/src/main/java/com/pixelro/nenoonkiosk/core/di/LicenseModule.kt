package com.pixelro.nenoonkiosk.core.di

import android.content.Context
import com.pixelro.nenoonkiosk.core.manager.LicenseManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * 라이선스 관련 Hilt 의존성 주입 모듈
 */
@Module
@InstallIn(SingletonComponent::class)
object LicenseModule {

    /**
     * LicenseManager 싱글톤 제공
     */
    @Provides
    @Singleton
    fun provideLicenseManager(
        @ApplicationContext context: Context
    ): LicenseManager {
        return LicenseManager(context)
    }
}