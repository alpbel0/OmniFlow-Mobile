package com.omniflow.features.auth.ui.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.omniflow.core.designsystem.components.OmniButton

@Composable
fun OnboardingScreen(
    paddingValues: PaddingValues,
    onLoginClick: () -> Unit,
    onRegisterClick: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Plan. Explore. Share.",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "OmniFlow mobile project skeleton is ready for the first milestone.",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 16.dp),
        )
        OmniButton(text = "Login", onClick = onLoginClick)
        OmniButton(
            text = "Create account",
            onClick = onRegisterClick,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}
