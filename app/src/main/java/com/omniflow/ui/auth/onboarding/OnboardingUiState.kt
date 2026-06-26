package com.omniflow.ui.auth.onboarding

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes

data class OnboardingUiState(
    val currentPage: Int = 0,
    val isSaving: Boolean = false,
    val showPersistenceError: Boolean = false,
)

enum class OnboardingDestination {
    Login,
}

data class OnboardingPage(
    @DrawableRes val imageRes: Int,
    @StringRes val heroTitleRes: Int,
    @StringRes val eyebrowRes: Int,
    @StringRes val titleRes: Int,
    @StringRes val bodyRes: Int,
)
