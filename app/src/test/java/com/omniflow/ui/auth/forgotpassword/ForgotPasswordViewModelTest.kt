package com.omniflow.ui.auth.forgotpassword

import app.cash.turbine.test
import com.omniflow.R
import com.omniflow.core.common.UiText
import com.omniflow.core.network.ApiResult
import com.omniflow.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ForgotPasswordViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: AuthRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `invalid email is rejected before repository call`() = runTest(dispatcher) {
        val viewModel = ForgotPasswordViewModel(repository)

        viewModel.onEmailChanged("ornek@")
        viewModel.onSendResetLinkClicked()

        assertEquals(
            UiText.StringResource(R.string.forgot_password_error_email_invalid),
            viewModel.uiState.value.emailError,
        )
    }

    @Test
    fun `successful send enters success state and starts cooldown`() = runTest(dispatcher) {
        coEvery { repository.forgotPassword(TEST_EMAIL) } returns ApiResult.Success(Unit)
        val viewModel = ForgotPasswordViewModel(repository)

        viewModel.onEmailChanged(TEST_EMAIL)
        viewModel.onSendResetLinkClicked()
        runCurrent()

        val state = viewModel.uiState.value
        assertTrue(state.isSuccess)
        assertEquals(TEST_EMAIL, state.sentEmail)
        assertEquals(60, state.cooldownSeconds)
        assertFalse(state.canResend)
        coVerify(exactly = 1) { repository.forgotPassword(TEST_EMAIL) }
    }

    @Test
    fun `failed send shows snackbar and keeps request screen`() = runTest(dispatcher) {
        coEvery { repository.forgotPassword(TEST_EMAIL) } returns ApiResult.Error(
            code = 500,
            message = UiText.DynamicString("server error"),
        )
        val viewModel = ForgotPasswordViewModel(repository)

        viewModel.effects.test {
            viewModel.onEmailChanged(TEST_EMAIL)
            viewModel.onSendResetLinkClicked()
            runCurrent()

            assertEquals(
                ForgotPasswordEffect.ShowSnackbar(
                    UiText.StringResource(R.string.forgot_password_send_error),
                    isError = true,
                ),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }

        assertFalse(viewModel.uiState.value.isSuccess)
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `successful resend restarts cooldown and shows success snackbar`() = runTest(dispatcher) {
        coEvery { repository.forgotPassword(TEST_EMAIL) } returns ApiResult.Success(Unit)
        val viewModel = ForgotPasswordViewModel(repository)

        viewModel.onEmailChanged(TEST_EMAIL)
        viewModel.onSendResetLinkClicked()
        runCurrent()
        advanceTimeBy(60_001)

        viewModel.effects.test {
            viewModel.onResendClicked()
            runCurrent()

            assertEquals(
                ForgotPasswordEffect.ShowSnackbar(
                    UiText.StringResource(R.string.forgot_password_resend_success),
                    isError = false,
                ),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(60, viewModel.uiState.value.cooldownSeconds)
        coVerify(exactly = 2) { repository.forgotPassword(TEST_EMAIL) }
    }

    private companion object {
        const val TEST_EMAIL = "user@example.com"
    }
}
