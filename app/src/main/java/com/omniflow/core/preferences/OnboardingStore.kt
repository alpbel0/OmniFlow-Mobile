package com.omniflow.core.preferences

import kotlinx.coroutines.flow.Flow

interface OnboardingStore {
    val onboardingSeen: Flow<Boolean>

    suspend fun setOnboardingSeen(seen: Boolean)
}
