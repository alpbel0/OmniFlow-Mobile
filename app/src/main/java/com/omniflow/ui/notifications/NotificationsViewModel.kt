package com.omniflow.ui.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.core.common.UiState
import com.omniflow.core.network.ApiResult
import com.omniflow.data.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUiState())
    val uiState = _uiState.asStateFlow()

    private var allItems: List<NotificationUiItem> = emptyList()

    init {
        loadNotifications()
    }

    fun onRefresh() {
        if (_uiState.value.isRefreshing) return
        loadNotifications(isRefresh = true)
    }

    fun retry() {
        loadNotifications()
    }

    fun onFilterChange(filter: NotifFilter) {
        _uiState.update { it.copy(activeFilter = filter) }
        applyFilter(filter)
    }

    fun onNotifClick(item: NotificationUiItem) {
        if (!item.isRead) {
            markAsRead(item.id)
        }
    }

    fun onNotifLongPress() {
        _uiState.update { it.copy(isSelectMode = true) }
    }

    fun onToggleSelect(id: String) {
        _uiState.update { state ->
            val newSelection = if (id in state.selectedIds) {
                state.selectedIds - id
            } else {
                state.selectedIds + id
            }
            state.copy(selectedIds = newSelection)
        }
    }

    fun onSelectAll() {
        val currentList = (_uiState.value.contentState as? UiState.Success)?.data ?: return
        _uiState.update { it.copy(selectedIds = currentList.map { item -> item.id }.toSet()) }
    }

    fun onExitSelectMode() {
        _uiState.update { it.copy(isSelectMode = false, selectedIds = emptySet()) }
    }

    fun onMarkReadSelected() {
        val selectedIds = _uiState.value.selectedIds
        if (selectedIds.isEmpty()) return
        viewModelScope.launch {
            selectedIds.forEach { id -> notificationRepository.markAsRead(id) }
            allItems = allItems.map { if (it.id in selectedIds) it.copy(isRead = true) else it }
            _uiState.update { it.copy(isSelectMode = false, selectedIds = emptySet()) }
            applyFilter(_uiState.value.activeFilter)
        }
    }

    fun onMarkAllRead() {
        viewModelScope.launch {
            when (notificationRepository.markAllAsRead()) {
                is ApiResult.Success -> {
                    allItems = allItems.map { it.copy(isRead = true) }
                    applyFilter(_uiState.value.activeFilter)
                }
                else -> Unit
            }
        }
    }

    private fun loadNotifications(isRefresh: Boolean = false) {
        _uiState.update {
            if (isRefresh) it.copy(isRefreshing = true)
            else it.copy(contentState = UiState.Loading)
        }

        viewModelScope.launch {
            when (val result = notificationRepository.getNotifications()) {
                is ApiResult.Success -> {
                    allItems = result.data.items.map { it.toUiItem() }
                    _uiState.update {
                        it.copy(
                            contentState = UiState.Success(allItems),
                            isRefreshing = false,
                        )
                    }
                    applyFilter(_uiState.value.activeFilter)
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            contentState = UiState.Error(result.message),
                            isRefreshing = false,
                        )
                    }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    private fun markAsRead(id: String) {
        viewModelScope.launch {
            when (notificationRepository.markAsRead(id)) {
                is ApiResult.Success -> {
                    allItems = allItems.map { if (it.id == id) it.copy(isRead = true) else it }
                    applyFilter(_uiState.value.activeFilter)
                }
                else -> Unit
            }
        }
    }

    private fun applyFilter(filter: NotifFilter) {
        val filtered = if (filter == NotifFilter.ALL) {
            allItems
        } else {
            allItems.filter { it.type.filterGroup() == filter }
        }
        _uiState.update { it.copy(contentState = UiState.Success(filtered)) }
    }
}
