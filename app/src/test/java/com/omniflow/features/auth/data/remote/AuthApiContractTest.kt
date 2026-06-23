package com.omniflow.features.auth.data.remote

import org.junit.Assert.assertEquals
import org.junit.Test
import retrofit2.http.POST

class AuthApiContractTest {
    @Test
    fun `auth api exposes every account endpoint`() {
        val paths = AuthApi::class.java.declaredMethods
            .mapNotNull { method -> method.getAnnotation(POST::class.java)?.value }
            .toSet()

        assertEquals(
            setOf(
                "api/account/register",
                "api/account/login",
                "api/account/refresh-token",
                "api/account/verify-email",
                "api/account/resend-verification",
                "api/account/forgot-password",
                "api/account/reset-password",
            ),
            paths,
        )
    }
}
