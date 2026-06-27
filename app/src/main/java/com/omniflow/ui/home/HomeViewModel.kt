package com.omniflow.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.core.common.UiState
import com.omniflow.core.network.ApiResult
import com.omniflow.data.repository.HomeRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val homeRepository: HomeRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadHome()
    }

    fun onRefresh() {
        if (_uiState.value.isRefreshing) return
        loadHome(isRefresh = true)
    }

    fun retry() {
        loadHome()
    }

    private fun loadHome(isRefresh: Boolean = false) {
        _uiState.update {
            if (isRefresh && it.contentState is UiState.Success) {
                it.copy(isRefreshing = true)
            } else {
                it.copy(contentState = UiState.Loading, isRefreshing = false)
            }
        }

        viewModelScope.launch {
            when (val result = homeRepository.getHomeData()) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            contentState = UiState.Success(result.data.toUiModel()),
                            isRefreshing = false,
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { current ->
                        if (isRefresh && current.contentState is UiState.Success) {
                            current.copy(isRefreshing = false)
                        } else {
                            current.copy(contentState = UiState.Error(result.message), isRefreshing = false)
                        }
                    }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }
}
