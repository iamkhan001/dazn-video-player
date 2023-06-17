package com.dazn.player.di

import android.content.Context
import com.dazn.player.data.VideoRepository
import com.dazn.player.utils.AnalyticsEventLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object GoogleAnalyticsModule {

    @Provides
    @Singleton
    fun provideAnalyticsEventLogger(@ApplicationContext appContext: Context): AnalyticsEventLogger = AnalyticsEventLogger()

}