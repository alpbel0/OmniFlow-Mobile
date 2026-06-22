package com.omniflow.core.network

import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import com.omniflow.BuildConfig
import com.omniflow.core.auth.TokenStore
import com.omniflow.core.network.interceptors.AuthInterceptor
import com.omniflow.core.network.interceptors.TokenAuthenticator
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import javax.inject.Singleton
import javax.inject.Named
import javax.inject.Qualifier

@Qualifier
@Retention(AnnotationRetention.BINARY)
private annotation class RefreshClient

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideJson(): Json {
        return Json {
            ignoreUnknownKeys = true
            explicitNulls = false
        }
    }

    @Provides
    @Singleton
    fun provideErrorParser(json: Json): ErrorParser = ErrorParser(json)

    @Provides
    @Singleton
    @RefreshClient
    fun provideRefreshOkHttpClient(): OkHttpClient = OkHttpClient.Builder().build()

    @Provides
    @Singleton
    @RefreshClient
    fun provideRefreshRetrofit(
        @Named("baseUrl") baseUrl: String,
        @RefreshClient okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .client(okHttpClient)
        .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
        .build()

    @Provides
    @Singleton
    fun provideRefreshTokenApi(@RefreshClient retrofit: Retrofit): RefreshTokenApi =
        retrofit.create(RefreshTokenApi::class.java)

    @Provides
    @Singleton
    fun provideOkHttpClient(
        tokenStore: TokenStore,
        refreshTokenApi: RefreshTokenApi,
    ): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BASIC
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
            redactHeader("Authorization")
        }

        return OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenStore))
            .authenticator(TokenAuthenticator(tokenStore, refreshTokenApi))
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("baseUrl") baseUrl: String,
        okHttpClient: OkHttpClient,
        json: Json,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory("application/json".toMediaType()))
            .build()
    }
}
