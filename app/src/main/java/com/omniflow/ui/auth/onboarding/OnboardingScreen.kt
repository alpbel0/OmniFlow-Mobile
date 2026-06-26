package com.omniflow.ui.auth.onboarding

import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omniflow.R
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    paddingValues: PaddingValues,
    onLoginClick: () -> Unit,
    viewModel: OnboardingViewModel = hiltViewModel(),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(
        initialPage = uiState.currentPage,
        pageCount = { onboardingPages.size },
    )
    val pagerProgress = (pagerState.currentPage + pagerState.currentPageOffsetFraction)
        .coerceIn(0f, onboardingPages.lastIndex.toFloat())
    val coroutineScope = rememberCoroutineScope()
    var isCardExpanded by rememberSaveable { mutableStateOf(false) }

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
                pagerProgress = pagerProgress,
                isCardExpanded = isCardExpanded,
                isSaving = uiState.isSaving,
                showPersistenceError = uiState.showPersistenceError,
                onSkip = viewModel::onSkip,
                onCardToggle = { isCardExpanded = !isCardExpanded },
                onPrimaryClick = {
                    if (pageIndex == onboardingPages.lastIndex) {
                        viewModel.onSkip()
                    } else {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(
                                page = pageIndex + 1,
                                animationSpec = tween(durationMillis = NEXT_PAGE_ANIMATION_DURATION_MS),
                            )
                        }
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

private const val NEXT_PAGE_ANIMATION_DURATION_MS = 420
