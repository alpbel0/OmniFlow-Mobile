package com.omniflow.features.auth.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.omniflow.core.designsystem.components.LoadingIndicator
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    paddingValues: PaddingValues,
    onContinue: () -> Unit,
) {
    LaunchedEffect(Unit) {
        delay(600)
        onContinue()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center,
    ) {
        LoadingIndicator()
    }
}
