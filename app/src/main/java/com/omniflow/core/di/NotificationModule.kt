package com.omniflow.core.di

import com.omniflow.data.remote.NotificationService
import com.omniflow.data.repository.NotificationRepository
import com.omniflow.data.repository.NotificationRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton
import retrofit2.Retrofit

@Module
@InstallIn(SingletonComponent::class)
abstract class NotificationModule {
    @Binds
    @Singleton
    abstract fun bindNotificationRepository(impl: NotificationRepositoryImpl): NotificationRepository

    companion object {
        @Provides
        @Singleton
        fun provideNotificationService(retrofit: Retrofit): NotificationService =
            retrofit.create(NotificationService::class.java)
    }
}
