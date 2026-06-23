package com.omniflow.features.auth.data.remote

import com.omniflow.core.common.Constants
import com.omniflow.core.data.remote.dto.RefreshTokenRequestDto
import com.omniflow.features.auth.data.remote.dto.AuthResponseDto
import com.omniflow.features.auth.data.remote.dto.ForgotPasswordRequestDto
import com.omniflow.features.auth.data.remote.dto.LoginRequestDto
import com.omniflow.features.auth.data.remote.dto.MessageResponseDto
import com.omniflow.features.auth.data.remote.dto.RegisterRequestDto
import com.omniflow.features.auth.data.remote.dto.RegistrationVerificationResponseDto
import com.omniflow.features.auth.data.remote.dto.ResendVerificationRequestDto
import com.omniflow.features.auth.data.remote.dto.ResetPasswordRequestDto
import com.omniflow.features.auth.data.remote.dto.VerifyEmailRequestDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {
    @POST("api/account/register")
    suspend fun register(
        @Body request: RegisterRequestDto,
    ): RegistrationVerificationResponseDto

    @POST("api/account/login")
    suspend fun login(@Body request: LoginRequestDto): AuthResponseDto

    @POST(Constants.RefreshTokenPath)
    suspend fun refreshToken(@Body request: RefreshTokenRequestDto): AuthResponseDto

    @POST("api/account/verify-email")
    suspend fun verifyEmail(@Body request: VerifyEmailRequestDto): MessageResponseDto

    @POST("api/account/resend-verification")
    suspend fun resendVerification(
        @Body request: ResendVerificationRequestDto,
    ): MessageResponseDto

    @POST("api/account/forgot-password")
    suspend fun forgotPassword(@Body request: ForgotPasswordRequestDto): MessageResponseDto

    @POST("api/account/reset-password")
    suspend fun resetPassword(@Body request: ResetPasswordRequestDto): MessageResponseDto
}
