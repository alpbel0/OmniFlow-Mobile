package com.omniflow.core.di

import com.omniflow.BuildConfig
import com.omniflow.core.common.Constants
import com.omniflow.core.auth.TokenManager
import com.omniflow.core.auth.TokenStore
import com.omniflow.data.local.datastore.PreferencesManager
import com.omniflow.core.preferences.OnboardingStore
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

    @Binds
    abstract fun bindOnboardingStore(preferencesManager: PreferencesManager): OnboardingStore

    companion object {

        @Provides
        @Named("baseUrl")
        fun provideBaseUrl(): String = Constants.BaseUrl

        @Provides
        @Named("alwaysShowOnboarding")
        fun provideAlwaysShowOnboarding(): Boolean = BuildConfig.DEBUG
    }
}
