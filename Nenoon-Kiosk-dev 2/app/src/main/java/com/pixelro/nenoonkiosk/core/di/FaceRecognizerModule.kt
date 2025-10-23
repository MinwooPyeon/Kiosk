package com.pixelro.nenoonkiosk.core.di

import android.app.Application
import com.pixelro.nenoonkiosk.feature.user.FaceRecognizer
import com.pixelro.nenoonkiosk.feature.user.TFLiteFaceRecognizer // Import the correct class
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FaceRecognizerModule {
    @Provides
    @Singleton
    fun provideFaceRecognizer(application: Application): FaceRecognizer {
        return TFLiteFaceRecognizer(application)
    }
}
