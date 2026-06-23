package com.omniflow.ui.auth.login

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
import com.omniflow.uicomponents.OmniButton
import com.omniflow.uicomponents.OmniTextField

@Composable
fun LoginScreen(
    paddingValues: PaddingValues,
    onLoginSuccess: () -> Unit,
    onRegisterClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "Welcome back",
            style = MaterialTheme.typography.headlineMedium,
        )
        OmniTextField(
            value = email,
            onValueChange = { email = it },
            label = "Email",
            modifier = Modifier.padding(top = 16.dp),
        )
        OmniTextField(
            value = password,
            onValueChange = { password = it },
            label = "Password",
            modifier = Modifier.padding(top = 12.dp),
        )
        OmniButton(
            text = "Login",
            onClick = onLoginSuccess,
            modifier = Modifier.padding(top = 16.dp),
            enabled = email.isNotBlank() && password.isNotBlank(),
        )
        OmniButton(
            text = "Forgot password",
            onClick = onForgotPasswordClick,
            modifier = Modifier.padding(top = 12.dp),
        )
        OmniButton(
            text = "Create account",
            onClick = onRegisterClick,
            modifier = Modifier.padding(top = 12.dp),
        )
    }
}
