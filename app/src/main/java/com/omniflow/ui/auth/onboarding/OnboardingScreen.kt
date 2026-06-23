package com.omniflow.ui.auth.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omniflow.R
import kotlinx.coroutines.flow.distinctUntilChanged

@Composable
fun OnboardingScreen(
    paddingValues: PaddingValues,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(
        initialPage = uiState.currentPage,
        pageCount = { onboardingPages.size },
    )

    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }
            .distinctUntilChanged()
            .collect(viewModel::onPageChanged)
    }
    LaunchedEffect(uiState.currentPage) {
        if (pagerState.currentPage != uiState.currentPage) {
            pagerState.animateScrollToPage(uiState.currentPage)
        }
    }
    LaunchedEffect(viewModel) {
        viewModel.effects.collect { destination ->
            when (destination) {
                OnboardingDestination.Login -> onLoginClick()
                OnboardingDestination.Register -> onRegisterClick()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxSize(),
            userScrollEnabled = !uiState.isSaving,
        ) { pageIndex ->
            OnboardingPageContent(
                page = onboardingPages[pageIndex],
                pageIndex = pageIndex,
                pageCount = onboardingPages.size,
                isSaving = uiState.isSaving,
                showPersistenceError = uiState.showPersistenceError,
                onSkip = viewModel::onSkip,
                onPrimaryClick = {
                    if (pageIndex == onboardingPages.lastIndex) {
                        viewModel.onGetStarted()
                    } else {
                        viewModel.onNext()
                    }
                },
            )
        }
    }
}

private val onboardingPages = listOf(
    OnboardingPage(
        imageRes = R.drawable.onboarding_discover,
        heroTitleRes = R.string.onboarding_discover_hero,
        eyebrowRes = R.string.onboarding_discover_eyebrow,
        titleRes = R.string.onboarding_discover_title,
        bodyRes = R.string.onboarding_discover_body,
    ),
    OnboardingPage(
        imageRes = R.drawable.onboarding_build,
        heroTitleRes = R.string.onboarding_build_hero,
        eyebrowRes = R.string.onboarding_build_eyebrow,
        titleRes = R.string.onboarding_build_title,
        bodyRes = R.string.onboarding_build_body,
    ),
    OnboardingPage(
        imageRes = R.drawable.onboarding_share,
        heroTitleRes = R.string.onboarding_share_hero,
        eyebrowRes = R.string.onboarding_share_eyebrow,
        titleRes = R.string.onboarding_share_title,
        bodyRes = R.string.onboarding_share_body,
    ),
)
