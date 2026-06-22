package com.omniflow.features.auth.ui.resetpassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.omniflow.core.designsystem.components.OmniButton
import com.omniflow.core.designsystem.components.OmniTextField

@Composable
fun ResetPasswordScreen(
    paddingValues: PaddingValues,
    onContinue: () -> Unit,
) {
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Reset password",
            style = MaterialTheme.typography.headlineMedium,
        )
        OmniTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            modifier = Modifier.padding(top = 16.dp),
        )
        OmniButton(
            text = "Send reset link",
            onClick = onContinue,
            modifier = Modifier.padding(top = 16.dp),
            enabled = email.isNotBlank(),
        )
    }
}
