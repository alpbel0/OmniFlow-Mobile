package com.omniflow.ui.auth.verifyemail

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.omniflow.R
import com.omniflow.core.auth.PendingAuthCredentialsStore
import com.omniflow.core.common.UiText
import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.auth.AuthUser
import com.omniflow.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class VerifyEmailViewModelTest {

    private val dispatcher = StandardTestDispatcher()
    private lateinit var repository: AuthRepository
    private lateinit var credentialsStore: PendingAuthCredentialsStore

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        repository = mockk()
        credentialsStore = PendingAuthCredentialsStore()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `register source starts backend aligned cooldown`() = runTest(dispatcher) {
        val viewModel = createViewModel(source = VerifyEmailSource.REGISTER)

        assertEquals(60, viewModel.uiState.value.cooldownSeconds)

        advanceTimeBy(1_001)

        assertEquals(59, viewModel.uiState.value.cooldownSeconds)
    }

    @Test
    fun `login source allows immediate resend`() = runTest(dispatcher) {
        val viewModel = createViewModel(source = VerifyEmailSource.LOGIN)

        assertEquals(0, viewModel.uiState.value.cooldownSeconds)
        assertTrue(viewModel.uiState.value.canResend)
    }

    @Test
    fun `navigation state arriving after construction updates email and source`() = runTest(dispatcher) {
        val savedStateHandle = SavedStateHandle()
        val viewModel = VerifyEmailViewModel(repository, credentialsStore, savedStateHandle)

        savedStateHandle[VERIFY_EMAIL_KEY] = TEST_EMAIL
        savedStateHandle[VERIFY_EMAIL_SOURCE_KEY] = VerifyEmailSource.REGISTER.name
        runCurrent()

        assertEquals(TEST_EMAIL, viewModel.uiState.value.email)
        assertEquals(VerifyEmailSource.REGISTER, viewModel.uiState.value.source)
        assertEquals(60, viewModel.uiState.value.cooldownSeconds)
    }

    @Test
    fun `successful resend restarts cooldown and emits success message`() = runTest(dispatcher) {
        coEvery { repository.resendVerification(TEST_EMAIL) } returns ApiResult.Success(Unit)
        val viewModel = createViewModel(source = VerifyEmailSource.LOGIN)

        viewModel.effects.test {
            viewModel.onResendClicked()
            runCurrent()

            assertEquals(
                VerifyEmailEffect.ShowSnackbar(
                    UiText.StringResource(R.string.verify_email_resend_success),
                    isError = false,
                ),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(60, viewModel.uiState.value.cooldownSeconds)
        coVerify(exactly = 1) { repository.resendVerification(TEST_EMAIL) }
    }

    @Test
    fun `rate limited resend shows error and blocks immediate retry`() = runTest(dispatcher) {
        coEvery { repository.resendVerification(TEST_EMAIL) } returns ApiResult.Error(
            code = 429,
            message = UiText.DynamicString("rate limited"),
        )
        val viewModel = createViewModel(source = VerifyEmailSource.LOGIN)

        viewModel.effects.test {
            viewModel.onResendClicked()
            runCurrent()

            assertEquals(
                VerifyEmailEffect.ShowSnackbar(
                    UiText.StringResource(R.string.verify_email_resend_error),
                    isError = true,
                ),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }

        assertEquals(60, viewModel.uiState.value.cooldownSeconds)
        assertFalse(viewModel.uiState.value.canResend)
    }

    @Test
    fun `verified credentials login navigates home and clears password`() = runTest(dispatcher) {
        credentialsStore.save(TEST_EMAIL, TEST_PASSWORD)
        coEvery { repository.login(TEST_EMAIL, TEST_PASSWORD) } returns ApiResult.Success(
            AuthUser("id", "traveler", TEST_EMAIL, "user"),
        )
        val viewModel = createViewModel(source = VerifyEmailSource.REGISTER)

        viewModel.effects.test {
            viewModel.onVerifiedLoginClicked()
            advanceUntilIdle()

            assertEquals(VerifyEmailEffect.NavigateHome, awaitItem())
        }

        assertNull(credentialsStore.get())
    }

    @Test
    fun `unverified login keeps screen stable with inline error`() = runTest(dispatcher) {
        credentialsStore.save(TEST_EMAIL, TEST_PASSWORD)
        coEvery { repository.login(TEST_EMAIL, TEST_PASSWORD) } returns ApiResult.Error(
            code = 403,
            message = UiText.DynamicString("forbidden"),
        )
        val viewModel = createViewModel(source = VerifyEmailSource.REGISTER)

        viewModel.onVerifiedLoginClicked()
        advanceUntilIdle()

        assertEquals(
            UiText.StringResource(R.string.verify_email_not_verified),
            viewModel.uiState.value.verificationError,
        )
        assertFalse(viewModel.uiState.value.isLoading)
    }

    @Test
    fun `missing process credentials falls back to prefilled login`() = runTest(dispatcher) {
        val viewModel = createViewModel(source = VerifyEmailSource.REGISTER)

        viewModel.effects.test {
            viewModel.onVerifiedLoginClicked()

            assertEquals(VerifyEmailEffect.NavigateLogin(TEST_EMAIL), awaitItem())
        }
    }

    @Test
    fun `change email click opens inline form without clearing credentials`() = runTest(dispatcher) {
        credentialsStore.save(TEST_EMAIL, TEST_PASSWORD)
        val viewModel = createViewModel(source = VerifyEmailSource.REGISTER)

        viewModel.onChangeEmailClicked()

        assertTrue(viewModel.uiState.value.isChangingEmail)
        assertEquals(TEST_EMAIL, credentialsStore.get()?.email)
    }

    @Test
    fun `successful email change updates screen email closes form and starts cooldown`() = runTest(dispatcher) {
        credentialsStore.save(TEST_EMAIL, TEST_PASSWORD)
        coEvery {
            repository.changeVerificationEmail(TEST_EMAIL, NEW_EMAIL, TEST_PASSWORD)
        } returns ApiResult.Success(Unit)
        val viewModel = createViewModel(source = VerifyEmailSource.REGISTER)

        viewModel.onChangeEmailClicked()
        viewModel.onNewEmailChanged(NEW_EMAIL)

        viewModel.effects.test {
            viewModel.onSubmitEmailChangeClicked()
            runCurrent()

            assertEquals(
                VerifyEmailEffect.ShowSnackbar(
                    UiText.StringResource(R.string.verify_email_change_success),
                    isError = false,
                ),
                awaitItem(),
            )
            cancelAndIgnoreRemainingEvents()
        }

        val state = viewModel.uiState.value
        assertEquals(NEW_EMAIL, state.email)
        assertFalse(state.isChangingEmail)
        assertEquals("", state.newEmailInput)
        assertEquals(60, state.cooldownSeconds)
        assertEquals(NEW_EMAIL, credentialsStore.get()?.email)
    }

    @Test
    fun `failed email change preserves typed value`() = runTest(dispatcher) {
        credentialsStore.save(TEST_EMAIL, TEST_PASSWORD)
        coEvery {
            repository.changeVerificationEmail(TEST_EMAIL, NEW_EMAIL, TEST_PASSWORD)
        } returns ApiResult.Error(
            code = 400,
            message = UiText.DynamicString("Email address is already registered."),
        )
        val viewModel = createViewModel(source = VerifyEmailSource.REGISTER)

        viewModel.onChangeEmailClicked()
        viewModel.onNewEmailChanged(NEW_EMAIL)
        viewModel.onSubmitEmailChangeClicked()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.isChangingEmail)
        assertEquals(NEW_EMAIL, state.newEmailInput)
        assertEquals(UiText.DynamicString("Email address is already registered."), state.newEmailError)
    }

    @Test
    fun `email change without pending credentials falls back to login`() = runTest(dispatcher) {
        val viewModel = createViewModel(source = VerifyEmailSource.REGISTER)

        viewModel.onChangeEmailClicked()
        viewModel.onNewEmailChanged(NEW_EMAIL)

        viewModel.effects.test {
            viewModel.onSubmitEmailChangeClicked()
            assertEquals(VerifyEmailEffect.NavigateLogin(TEST_EMAIL), awaitItem())
        }
    }

    private fun createViewModel(source: VerifyEmailSource): VerifyEmailViewModel =
        VerifyEmailViewModel(
            authRepository = repository,
            credentialsStore = credentialsStore,
            savedStateHandle = SavedStateHandle(
                mapOf(
                    VERIFY_EMAIL_KEY to TEST_EMAIL,
                    VERIFY_EMAIL_SOURCE_KEY to source.name,
                ),
            ),
        )

    private companion object {
        const val TEST_EMAIL = "user@example.com"
        const val NEW_EMAIL = "new@example.com"
        const val TEST_PASSWORD = "Secret123!"
    }
}
