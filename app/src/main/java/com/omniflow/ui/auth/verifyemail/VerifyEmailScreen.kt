package com.omniflow.ui.auth.verifyemail

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.omniflow.R
import kotlinx.coroutines.launch

@Composable
fun VerifyEmailScreen(
    paddingValues: PaddingValues,
    onNavigateHome: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateLogin: (String) -> Unit,
    viewModel: VerifyEmailViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    var snackbarIsError by remember { mutableStateOf(false) }

    LaunchedEffect(viewModel) {
        viewModel.effects.collect { effect ->
            when (effect) {
                VerifyEmailEffect.NavigateHome -> onNavigateHome()
                VerifyEmailEffect.NavigateBack -> onNavigateBack()
                is VerifyEmailEffect.NavigateLogin -> onNavigateLogin(effect.email)
                VerifyEmailEffect.OpenMailApp -> {
                    try {
                        context.startActivity(createEmailAppIntent(context))
                    } catch (_: ActivityNotFoundException) {
                        snackbarIsError = true
                        launch {
                            snackbarHostState.showSnackbar(
                                context.getString(R.string.verify_email_mail_app_missing),
                            )
                        }
                    }
                }
                is VerifyEmailEffect.ShowSnackbar -> {
                    snackbarIsError = effect.isError
                    launch { snackbarHostState.showSnackbar(effect.message.resolve(context)) }
                }
            }
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        VerifyEmailContent(
            state = state,
            paddingValues = paddingValues,
            onVerifiedLoginClick = viewModel::onVerifiedLoginClicked,
            onOpenMailClick = viewModel::onOpenMailClicked,
            onResendClick = viewModel::onResendClicked,
            onChangeEmailClick = viewModel::onChangeEmailClicked,
            onNewEmailChange = viewModel::onNewEmailChanged,
            onSubmitEmailChangeClick = viewModel::onSubmitEmailChangeClicked,
            onCancelEmailChangeClick = viewModel::onCancelEmailChangeClicked,
            onBackToLoginClick = viewModel::onBackToLoginClicked,
        )
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(horizontal = 24.dp, vertical = 130.dp),
        ) { data ->
            Snackbar(
                snackbarData = data,
                containerColor = if (snackbarIsError) VerifyError else VerifySuccess,
                contentColor = Color.White,
                shape = RoundedCornerShape(14.dp),
            )
        }
    }
}
