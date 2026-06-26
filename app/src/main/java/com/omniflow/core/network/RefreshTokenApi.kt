package com.omniflow.core.network

import com.omniflow.core.common.Constants
import com.omniflow.data.models.auth.AuthenticationResponseDto
import com.omniflow.data.models.auth.RefreshTokenRequestDto
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface RefreshTokenApi {
    @Headers("${Constants.PlatformHeader}: ${Constants.MobilePlatform}")
    @POST(Constants.RefreshTokenPath)
    fun refreshToken(@Body request: RefreshTokenRequestDto): Call<AuthenticationResponseDto>
}
