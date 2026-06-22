package com.omniflow.core.di

import com.omniflow.core.common.Constants
import com.omniflow.core.auth.TokenManager
import com.omniflow.core.auth.TokenStore
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named

@Module
@InstallIn(SingletonComponent::class)
abstract class AppModule {

    @Binds
    abstract fun bindTokenStore(tokenManager: TokenManager): TokenStore

    companion object {

        @Provides
        @Named("baseUrl")
        fun provideBaseUrl(): String = Constants.BaseUrl
    }
}
