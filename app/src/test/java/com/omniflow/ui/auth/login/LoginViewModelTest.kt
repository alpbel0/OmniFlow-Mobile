package com.omniflow.ui.auth.login

import androidx.lifecycle.SavedStateHandle
import app.cash.turbine.test
import com.omniflow.core.network.ApiResult
import com.omniflow.core.auth.PendingAuthCredentialsStore
import com.omniflow.data.models.auth.AuthUser
import com.omniflow.data.repository.AuthRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class LoginViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var viewModel: LoginViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        viewModel = LoginViewModel(
            authRepository,
            PendingAuthCredentialsStore(),
            SavedStateHandle(),
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initial state is default`() {
        val state = viewModel.uiState.value
        assertEquals(false, state.isLoading)
        assertEquals(false, state.isPasswordVisible)
    }

    @Test
    fun `saved verification email prefills login without password`() {
        viewModel = LoginViewModel(
            authRepository,
            PendingAuthCredentialsStore(),
            SavedStateHandle(mapOf(LOGIN_EMAIL_KEY to "user@example.com")),
        )

        assertEquals("user@example.com", viewModel.uiState.value.email)
    }

    @Test
    fun `validation errors set on empty inputs`() {
        viewModel.onEmailChanged("")
        viewModel.onPasswordChanged("")
        viewModel.onLoginClicked()
        val state = viewModel.uiState.value
        assertNotNull(state.emailError)
        assertNotNull(state.passwordError)
    }

    @Test
    fun `validation error on invalid email`() {
        viewModel.onEmailChanged("invalid-email")
        viewModel.onPasswordChanged("123456")
        viewModel.onLoginClicked()
        val state = viewModel.uiState.value
        assertNotNull(state.emailError)
    }

    @Test
    fun `successful login triggers navigate to home effect`() = runTest {
        coEvery { authRepository.login("test@test.com", "password") } returns ApiResult.Success(
            AuthUser("id", "user", "test@test.com", "role")
        )

        viewModel.onEmailChanged("test@test.com")
        viewModel.onPasswordChanged("password")

        viewModel.effects.test {
            viewModel.onLoginClicked()
            assertEquals(LoginEffect.NavigateToHome, awaitItem())
        }
    }

    @Test
    fun `unauthorized error sets invalid credentials message`() = runTest {
        coEvery { authRepository.login("test@test.com", "wrong") } returns ApiResult.Error(
            code = 401,
            message = com.omniflow.core.common.UiText.DynamicString("")
        )

        viewModel.onEmailChanged("test@test.com")
        viewModel.onPasswordChanged("wrong")
        viewModel.onLoginClicked()

        val state = viewModel.uiState.value
        assertNotNull(state.generalError)
        assertEquals(false, state.isEmailUnverified)
    }

    @Test
    fun `unverified email error sets verification required message`() = runTest {
        coEvery { authRepository.login("test@test.com", "password") } returns ApiResult.Error(
            code = 403,
            message = com.omniflow.core.common.UiText.DynamicString("")
        )

        viewModel.onEmailChanged("test@test.com")
        viewModel.onPasswordChanged("password")
        viewModel.onLoginClicked()

        val state = viewModel.uiState.value
        assertNotNull(state.generalError)
        assertEquals(true, state.isEmailUnverified)
    }
}
