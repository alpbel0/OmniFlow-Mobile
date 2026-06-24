package com.omniflow.ui.auth.login

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.omniflow.core.designsystem.theme.OmniFlowTheme
import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.auth.AuthUser
import com.omniflow.data.models.auth.RegistrationResult
import com.omniflow.data.repository.AuthRepository
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loginScreenShowsActionsAndCompletesValidLogin() {
        var loginClicked = false
        val viewModel = LoginViewModel(TestAuthRepository())
        viewModel.onEmailChanged("user@example.com")
        viewModel.onPasswordChanged("Secret123!")

        composeRule.setContent {
            OmniFlowTheme {
                LoginScreen(
                    paddingValues = PaddingValues(),
                    onLoginSuccess = { loginClicked = true },
                    onRegisterClick = {},
                    onForgotPasswordClick = {},
                    onVerifyEmailClick = {},
                    viewModel = viewModel,
                )
            }
        }

        composeRule.onNodeWithText("Welcome back").assertIsDisplayed()
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
        composeRule.onNodeWithText("Continue with Google").assertIsDisplayed()
        composeRule.onNodeWithText("Login").performClick()
        composeRule.waitUntil(3_000) { loginClicked }

        assertTrue(loginClicked)
    }
}

private class TestAuthRepository : AuthRepository {
    override suspend fun login(email: String, password: String): ApiResult<AuthUser> =
        ApiResult.Success(AuthUser("id", "traveler", email, "user"))

    override suspend fun register(
        username: String,
        email: String,
        password: String,
        confirmPassword: String,
    ): ApiResult<RegistrationResult> = unexpectedCall()

    override suspend fun verifyEmail(email: String, token: String): ApiResult<Unit> = unexpectedCall()
    override suspend fun resendVerification(email: String): ApiResult<Unit> = unexpectedCall()
    override suspend fun forgotPassword(email: String): ApiResult<Unit> = unexpectedCall()
    override suspend fun resetPassword(
        email: String,
        token: String,
        newPassword: String,
    ): ApiResult<Unit> = unexpectedCall()

    private fun <T> unexpectedCall(): T = throw AssertionError("Unexpected repository call")
}
