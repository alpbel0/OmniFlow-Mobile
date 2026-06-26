package com.omniflow.ui.auth.register

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.omniflow.core.designsystem.theme.OmniFlowTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

class RegisterScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun registerContentShowsFieldsAndFinalActionOrder() {
        var registerClicked = false
        composeRule.setContent {
            OmniFlowTheme {
                RegisterContent(
                    state = RegisterUiState(
                        username = "traveler",
                        email = "user@example.com",
                        password = "ValidPass1!",
                        confirmPassword = "ValidPass1!",
                    ),
                    onUsernameChanged = {},
                    onEmailChanged = {},
                    onPasswordChanged = {},
                    onConfirmPasswordChanged = {},
                    onPasswordVisibilityToggle = {},
                    onConfirmPasswordVisibilityToggle = {},
                    onRegisterClick = { registerClicked = true },
                    onLoginClick = {},
                )
            }
        }

        composeRule.onNodeWithText("Username").assertIsDisplayed()
        composeRule.onNodeWithText("Email").assertIsDisplayed()
        composeRule.onNodeWithText("Password").assertIsDisplayed()
        composeRule.onNodeWithText("Confirm password").assertIsDisplayed()
        composeRule.onNodeWithText("Create account").performScrollTo().performClick()
        composeRule.onNodeWithText("or continue with").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Continue with Google")
            .performScrollTo()
            .assertIsDisplayed()
            .assertIsNotEnabled()
        composeRule.onNodeWithText("Log in").performScrollTo().assertIsDisplayed()

        assertTrue(registerClicked)
    }
}
