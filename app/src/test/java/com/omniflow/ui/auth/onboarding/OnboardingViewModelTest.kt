package com.omniflow.ui.auth.onboarding

import app.cash.turbine.test
import com.omniflow.core.preferences.OnboardingStore
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class OnboardingViewModelTest {
    private val dispatcher: TestDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(dispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `next advances pages without persisting onboarding`() {
        val store = mockk<OnboardingStore>(relaxed = true)
        val viewModel = OnboardingViewModel(store)

        viewModel.onNext()
        viewModel.onNext()
        viewModel.onNext()

        assertEquals(2, viewModel.uiState.value.currentPage)
        coVerify(exactly = 0) { store.setOnboardingSeen(any()) }
    }

    @Test
    fun `skip persists onboarding before navigating login`() = runTest(dispatcher) {
        val store = mockk<OnboardingStore>()
        coEvery { store.setOnboardingSeen(true) } returns Unit
        val viewModel = OnboardingViewModel(store)

        viewModel.effects.test {
            viewModel.onSkip()
            runCurrent()

            coVerify(exactly = 1) { store.setOnboardingSeen(true) }
            assertEquals(OnboardingDestination.Login, awaitItem())
            assertFalse(viewModel.uiState.value.isSaving)
        }
    }

    @Test
    fun `get started persists onboarding before navigating register`() = runTest(dispatcher) {
        val store = mockk<OnboardingStore>()
        coEvery { store.setOnboardingSeen(true) } returns Unit
        val viewModel = OnboardingViewModel(store)

        viewModel.effects.test {
            viewModel.onGetStarted()
            runCurrent()

            assertEquals(OnboardingDestination.Register, awaitItem())
        }
    }

    @Test
    fun `persistence failure keeps onboarding visible`() = runTest(dispatcher) {
        val store = mockk<OnboardingStore>()
        coEvery { store.setOnboardingSeen(true) } throws IllegalStateException("disk error")
        val viewModel = OnboardingViewModel(store)

        viewModel.effects.test {
            viewModel.onSkip()
            runCurrent()

            expectNoEvents()
            assertTrue(viewModel.uiState.value.showPersistenceError)
            assertFalse(viewModel.uiState.value.isSaving)
        }
    }

    @Test
    fun `repeated completion while saving writes once`() = runTest(dispatcher) {
        val gate = CompletableDeferred<Unit>()
        val store = mockk<OnboardingStore>()
        coEvery { store.setOnboardingSeen(true) } coAnswers { gate.await() }
        val viewModel = OnboardingViewModel(store)

        viewModel.onSkip()
        runCurrent()
        viewModel.onSkip()
        gate.complete(Unit)
        runCurrent()

        coVerify(exactly = 1) { store.setOnboardingSeen(true) }
    }
}
