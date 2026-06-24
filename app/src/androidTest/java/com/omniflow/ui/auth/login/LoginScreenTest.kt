package com.omniflow.ui.auth.login

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.omniflow.core.designsystem.theme.OmniFlowTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class LoginScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun loginContentShowsRequiredActionsWithoutUnavailableGoogleLogin() {
        var loginClicked = false
        composeRule.setContent {
            OmniFlowTheme {
                LoginContent(
                    state = LoginUiState(
                        email = "user@example.com",
                        password = "Secret123!",
                    ),
                    onEmailChanged = {},
                    onPasswordChanged = {},
                    onPasswordVisibilityToggle = {},
                    onLoginClick = { loginClicked = true },
                    onForgotPasswordClick = {},
                    onRegisterClick = {},
                    onVerifyEmailClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Welcome back").assertIsDisplayed()
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
        composeRule.onNodeWithText("Continue with Google")
            .assertIsDisplayed()
            .assertIsNotEnabled()
        composeRule.onNodeWithText("Login").performClick()

        assertTrue(loginClicked)
    }
}
