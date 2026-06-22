package com.omniflow.features.auth.ui.register

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
fun RegisterScreen(
    paddingValues: PaddingValues,
    onRegisterSuccess: () -> Unit,
    onLoginClick: () -> Unit,
) {
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Create account",
            style = MaterialTheme.typography.headlineMedium,
        )
        OmniTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = "Full name",
            modifier = Modifier.padding(top = 16.dp),
        )
        OmniTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            modifier = Modifier.padding(top = 12.dp),
        )
        OmniButton(
            text = "Continue",
            onClick = onRegisterSuccess,
            modifier = Modifier.padding(top = 16.dp),
            enabled = fullName.isNotBlank() && email.isNotBlank(),
        )
        OmniButton(
            text = "I already have an account",
            onClick = onLoginClick,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}
