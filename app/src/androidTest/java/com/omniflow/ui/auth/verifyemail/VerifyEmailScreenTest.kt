package com.omniflow.ui.auth.verifyemail

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import com.omniflow.R
import com.omniflow.core.common.UiText
import com.omniflow.core.designsystem.theme.OmniFlowTheme
import org.junit.Rule
import org.junit.Test

class VerifyEmailScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun defaultStateShowsEmailActionsAndRegisterCooldown() {
        setContent(
            VerifyEmailUiState(
                email = "user@example.com",
                source = VerifyEmailSource.REGISTER,
                cooldownSeconds = 42,
            ),
        )

        composeRule.onNodeWithText("Email'ini doğrula").assertIsDisplayed()
        composeRule.onNodeWithText("user@example.com", substring = true).assertIsDisplayed()
        composeRule.onNodeWithText("Doğruladım, giriş yap").assertIsEnabled()
        composeRule.onNodeWithText("Mail uygulamasını aç").assertIsEnabled()
        composeRule.onNodeWithText("42 sn sonra tekrar gönder").assertIsDisplayed()
        composeRule.onNodeWithText("Login'e dön").assertIsDisplayed()
    }

    @Test
    fun loadingStateDisablesEveryAction() {
        setContent(
            VerifyEmailUiState(
                email = "user@example.com",
                isVerifying = true,
            ),
        )

        composeRule.onNodeWithText("Doğruladım, giriş yap").assertIsNotEnabled()
        composeRule.onNodeWithText("Mail uygulamasını aç").assertIsNotEnabled()
        composeRule.onNodeWithText("Tekrar gönder").assertIsNotEnabled()
    }

    @Test
    fun unverifiedStateShowsReservedInlineError() {
        setContent(
            VerifyEmailUiState(
                email = "user@example.com",
                verificationError = UiText.StringResource(R.string.verify_email_not_verified),
            ),
        )

        composeRule.onNodeWithText("Email henüz doğrulanmadı.").assertIsDisplayed()
        composeRule.onNodeWithText("Mail uygulamasını aç").assertIsDisplayed()
    }

    private fun setContent(state: VerifyEmailUiState) {
        composeRule.setContent {
            OmniFlowTheme {
                VerifyEmailContent(
                    state = state,
                    paddingValues = PaddingValues(),
                    onVerifiedLoginClick = {},
                    onOpenMailClick = {},
                    onResendClick = {},
                    onChangeEmailClick = {},
                    onNewEmailChange = {},
                    onSubmitEmailChangeClick = {},
                    onCancelEmailChangeClick = {},
                    onBackToLoginClick = {},
                )
            }
        }
    }
}
