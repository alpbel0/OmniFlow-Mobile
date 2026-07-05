package com.omniflow.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.core.common.UiState
import com.omniflow.core.network.ApiResult
import com.omniflow.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class FollowListViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val userId: String = savedStateHandle.get<String>("userId") ?: ""
    private val modeArg: String = savedStateHandle.get<String>("mode") ?: "followers"

    private val _uiState = MutableStateFlow(
        FollowListUiState(
            mode = if (modeArg == "following") FollowListMode.FOLLOWING else FollowListMode.FOLLOWERS,
        )
    )
    val uiState = _uiState.asStateFlow()

    private var searchJob: Job? = null

    init {
        loadList()
    }

    fun retry() {
        loadList()
    }

    fun onSearchChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(300)
            loadList(search = query.ifBlank { null })
        }
    }

    fun onFollow(user: FollowUserUiModel) {
        val userId = user.id
        if (userId.isEmpty() || userId in _uiState.value.followLoadingIds) return
        val prevState = _uiState.value

        _uiState.update { state ->
            state.copy(
                followLoadingIds = state.followLoadingIds + userId,
                contentState = (state.contentState as? UiState.Success)?.let { success ->
                    UiState.Success(success.data.map {
                        if (it.id == userId) it.copy(isFollowing = true) else it
                    })
                } ?: state.contentState,
            )
        }

        viewModelScope.launch {
            when (profileRepository.followUser(userId)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(followLoadingIds = it.followLoadingIds - userId) }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            followLoadingIds = it.followLoadingIds - userId,
                            contentState = prevState.contentState,
                        )
                    }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onUnfollow(user: FollowUserUiModel) {
        val userId = user.id
        if (userId.isEmpty() || userId in _uiState.value.followLoadingIds) return
        val prevState = _uiState.value

        _uiState.update { state ->
            state.copy(
                followLoadingIds = state.followLoadingIds + userId,
                contentState = (state.contentState as? UiState.Success)?.let { success ->
                    UiState.Success(success.data.map {
                        if (it.id == userId) it.copy(isFollowing = false) else it
                    })
                } ?: state.contentState,
            )
        }

        viewModelScope.launch {
            when (profileRepository.unfollowUser(userId)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(followLoadingIds = it.followLoadingIds - userId) }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            followLoadingIds = it.followLoadingIds - userId,
                            contentState = prevState.contentState,
                        )
                    }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    private fun loadList(search: String? = null) {
        _uiState.update {
            it.copy(
                contentState = if (search == null) UiState.Loading else it.contentState,
                isSearching = search != null,
            )
        }

        viewModelScope.launch {
            val result = when (_uiState.value.mode) {
                FollowListMode.FOLLOWERS -> profileRepository.getFollowers(userId, search = search)
                FollowListMode.FOLLOWING -> profileRepository.getFollowing(userId, search = search)
            }

            when (result) {
                is ApiResult.Success -> {
                    val models = result.data.toUiModels()
                    _uiState.update {
                        it.copy(
                            contentState = UiState.Success(models),
                            totalCount = models.size,
                            isSearching = false,
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(
                            contentState = UiState.Error(result.message),
                            isSearching = false,
                        )
                    }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }
}
