package com.omniflow.data.remote

import com.omniflow.core.common.Constants
import com.omniflow.data.models.auth.RefreshTokenRequestDto
import com.omniflow.data.models.auth.ForgotPasswordRequestDto
import com.omniflow.data.models.auth.LoginRequestDto
import com.omniflow.data.models.auth.RegisterRequestDto
import com.omniflow.data.models.auth.ResendVerificationRequestDto
import com.omniflow.data.models.auth.ResetPasswordRequestDto
import com.omniflow.data.models.auth.VerifyEmailRequestDto
import com.omniflow.data.models.auth.AuthResponseDto
import com.omniflow.data.models.auth.MessageResponseDto
import com.omniflow.data.models.auth.RegistrationVerificationResponseDto
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
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
