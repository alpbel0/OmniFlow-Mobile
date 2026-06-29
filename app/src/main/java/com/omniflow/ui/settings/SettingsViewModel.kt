package com.omniflow.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.core.auth.TokenStore
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val tokenStore: TokenStore,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    fun onRowClick(id: String) {
        // Sub-screen navigation: handled by NavHost via callback
    }

    fun onLogout() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoggingOut = true) }
            tokenStore.clearSession()
            _uiState.update { it.copy(isLoggingOut = false, loggedOut = true) }
        }
    }
}
