package com.omniflow.ui.auth.splash

import app.cash.turbine.test
import com.omniflow.core.auth.SessionState
import com.omniflow.core.auth.TokenStore
import com.omniflow.core.preferences.OnboardingStore
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SplashViewModelTest {
    private val dispatcher: TestDispatcher = StandardTestDispatcher()
    private lateinit var sessionState: MutableStateFlow<SessionState>
    private lateinit var onboardingSeen: MutableStateFlow<Boolean>

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
        sessionState = MutableStateFlow(SessionState.Unknown)
        onboardingSeen = MutableStateFlow(false)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `unknown session remains loading`() = runTest(dispatcher) {
        viewModel().uiState.test {
            assertEquals(SplashUiState.Loading, awaitItem())
            expectNoEvents()
        }
    }

    @Test
    fun `signed in session navigates home`() = runTest(dispatcher) {
        val viewModel = viewModel()

        viewModel.uiState.test {
            assertEquals(SplashUiState.Loading, awaitItem())
            sessionState.value = SessionState.SignedIn
            runCurrent()
            expectNoEvents()
            advanceTimeBy(MINIMUM_SPLASH_DURATION_MILLIS)
            runCurrent()
            assertEquals(
                SplashUiState.Ready(SplashDestination.Home),
                awaitItem(),
            )
        }
    }

    @Test
    fun `first signed out launch navigates onboarding`() = runTest(dispatcher) {
        val viewModel = viewModel()

        viewModel.uiState.test {
            assertEquals(SplashUiState.Loading, awaitItem())
            sessionState.value = SessionState.SignedOut
            advanceTimeBy(MINIMUM_SPLASH_DURATION_MILLIS)
            runCurrent()
            assertEquals(
                SplashUiState.Ready(SplashDestination.Onboarding),
                awaitItem(),
            )
        }
    }

    @Test
    fun `returning signed out user navigates login`() = runTest(dispatcher) {
        onboardingSeen.value = true
        val viewModel = viewModel()

        viewModel.uiState.test {
            assertEquals(SplashUiState.Loading, awaitItem())
            sessionState.value = SessionState.SignedOut
            advanceTimeBy(MINIMUM_SPLASH_DURATION_MILLIS)
            runCurrent()
            assertEquals(
                SplashUiState.Ready(SplashDestination.Login),
                awaitItem(),
            )
        }
    }

    @Test
    fun `development override always navigates onboarding`() = runTest(dispatcher) {
        onboardingSeen.value = true
        sessionState.value = SessionState.SignedIn
        val viewModel = viewModel(alwaysShowOnboarding = true)

        viewModel.uiState.test {
            assertEquals(SplashUiState.Loading, awaitItem())
            advanceTimeBy(MINIMUM_SPLASH_DURATION_MILLIS)
            runCurrent()
            assertEquals(
                SplashUiState.Ready(SplashDestination.Onboarding),
                awaitItem(),
            )
        }
    }

    private fun viewModel(alwaysShowOnboarding: Boolean = false): SplashViewModel {
        val tokenStore = mockk<TokenStore> {
            every { sessionState } returns this@SplashViewModelTest.sessionState
        }
        val onboardingStore = mockk<OnboardingStore> {
            every { onboardingSeen } returns this@SplashViewModelTest.onboardingSeen
        }
        return SplashViewModel(tokenStore, onboardingStore, alwaysShowOnboarding)
    }

    private companion object {
        const val MINIMUM_SPLASH_DURATION_MILLIS = 1_000L
    }
}
