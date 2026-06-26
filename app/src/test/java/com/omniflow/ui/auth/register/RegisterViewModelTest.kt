package com.omniflow.ui.auth.register

import app.cash.turbine.test
import com.omniflow.core.auth.PendingAuthCredentials
import com.omniflow.core.auth.PendingAuthCredentialsStore
import com.omniflow.core.common.UiText
import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.auth.RegistrationResult
import com.omniflow.data.models.common.ValidationErrorDetail
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
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class RegisterViewModelTest {

    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var authRepository: AuthRepository
    private lateinit var credentialsStore: PendingAuthCredentialsStore
    private lateinit var viewModel: RegisterViewModel

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        authRepository = mockk()
        credentialsStore = PendingAuthCredentialsStore()
        viewModel = RegisterViewModel(authRepository, credentialsStore)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `empty submission marks every field invalid`() {
        viewModel.onRegisterClicked()

        val state = viewModel.uiState.value
        assertNotNull(state.usernameError)
        assertNotNull(state.emailError)
        assertNotNull(state.passwordError)
        assertNotNull(state.confirmPasswordError)
    }

    @Test
    fun `password requirements update while typing`() {
        viewModel.onPasswordChanged("ValidPass1!")

        val requirements = viewModel.uiState.value.passwordRequirements
        assertTrue(requirements.hasMinimumLength)
        assertTrue(requirements.hasUppercase)
        assertTrue(requirements.hasLowercase)
        assertTrue(requirements.hasDigit)
        assertTrue(requirements.hasSpecialCharacter)
    }

    @Test
    fun `different confirmation sets mismatch error`() {
        viewModel.onPasswordChanged("ValidPass1!")
        viewModel.onConfirmPasswordChanged("ValidPass2!")

        assertNotNull(viewModel.uiState.value.confirmPasswordError)
    }

    @Test
    fun `successful registration emits verification effect with email`() = runTest {
        coEvery {
            authRepository.register("traveler", "user@test.com", "ValidPass1!", "ValidPass1!")
        } returns ApiResult.Success(RegistrationResult(requiresEmailVerification = true))

        fillValidForm()

        viewModel.effects.test {
            viewModel.onRegisterClicked()
            assertEquals(RegisterEffect.NavigateToVerifyEmail("user@test.com"), awaitItem())
        }
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            PendingAuthCredentials("user@test.com", "ValidPass1!"),
            credentialsStore.get(),
        )
    }

    @Test
    fun `validation response maps backend field error`() = runTest {
        coEvery {
            authRepository.register(any(), any(), any(), any())
        } returns ApiResult.Error(
            code = 422,
            message = UiText.DynamicString("Validation failed."),
            validationErrors = listOf(
                ValidationErrorDetail(
                    field = "Email",
                    message = "A valid email address is required.",
                    code = "EMAIL_INVALID",
                ),
            ),
        )

        fillValidForm(email = "invalid@test.com")
        viewModel.onRegisterClicked()

        assertNotNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun `duplicate email response maps to email field`() = runTest {
        coEvery {
            authRepository.register(any(), any(), any(), any())
        } returns ApiResult.Error(
            code = 400,
            message = UiText.DynamicString("Email address is already registered."),
        )

        fillValidForm()
        viewModel.onRegisterClicked()

        assertNotNull(viewModel.uiState.value.emailError)
    }

    @Test
    fun `duplicate username response maps to username field`() = runTest {
        coEvery {
            authRepository.register(any(), any(), any(), any())
        } returns ApiResult.Error(
            code = 400,
            message = UiText.DynamicString("Username is already taken."),
        )

        fillValidForm()
        viewModel.onRegisterClicked()

        assertNotNull(viewModel.uiState.value.usernameError)
    }

    private fun fillValidForm(email: String = "user@test.com") {
        viewModel.onUsernameChanged("traveler")
        viewModel.onEmailChanged(email)
        viewModel.onPasswordChanged("ValidPass1!")
        viewModel.onConfirmPasswordChanged("ValidPass1!")
    }
}
