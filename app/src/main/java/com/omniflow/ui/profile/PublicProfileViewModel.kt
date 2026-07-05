package com.omniflow.ui.profile

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.core.common.UiState
import com.omniflow.core.common.UiText
import com.omniflow.core.network.ApiResult
import com.omniflow.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class PublicProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    private val username: String = savedStateHandle.get<String>("username") ?: ""

    private val _uiState = MutableStateFlow(PublicProfileUiState())
    val uiState = _uiState.asStateFlow()

    private var userId: String = ""

    init {
        loadProfile()
    }

    fun retry() {
        loadProfile()
    }

    fun onTabChange(tab: PublicProfileTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun onFollow() {
        if (userId.isEmpty() || _uiState.value.followActionLoading) return
        val prevFollowing = _uiState.value.isFollowing
        val prevState = _uiState.value

        _uiState.update { state ->
            state.copy(
                isFollowing = true,
                followActionLoading = true,
                contentState = (state.contentState as? UiState.Success)?.let { success ->
                    UiState.Success(success.data.copy(followerCount = success.data.followerCount + 1))
                } ?: state.contentState,
            )
        }

        viewModelScope.launch {
            when (profileRepository.followUser(userId)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(followActionLoading = false) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(
                        isFollowing = prevFollowing,
                        followActionLoading = false,
                        contentState = prevState.contentState,
                    ) }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onUnfollow() {
        if (userId.isEmpty() || _uiState.value.followActionLoading) return
        val prevFollowing = _uiState.value.isFollowing
        val prevState = _uiState.value

        _uiState.update { state ->
            state.copy(
                isFollowing = false,
                followActionLoading = true,
                contentState = (state.contentState as? UiState.Success)?.let { success ->
                    UiState.Success(success.data.copy(
                        followerCount = (success.data.followerCount - 1).coerceAtLeast(0),
                    ))
                } ?: state.contentState,
            )
        }

        viewModelScope.launch {
            when (profileRepository.unfollowUser(userId)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(followActionLoading = false) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(
                        isFollowing = prevFollowing,
                        followActionLoading = false,
                        contentState = prevState.contentState,
                    ) }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onBlock() {
        if (userId.isEmpty() || _uiState.value.blockActionLoading) return
        _uiState.update { it.copy(blockActionLoading = true) }

        viewModelScope.launch {
            when (profileRepository.blockUser(userId)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isBlockedByMe = true, blockActionLoading = false) }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(blockActionLoading = false) }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onUnblock() {
        if (userId.isEmpty() || _uiState.value.blockActionLoading) return
        _uiState.update { it.copy(blockActionLoading = true) }

        viewModelScope.launch {
            when (profileRepository.unblockUser(userId)) {
                is ApiResult.Success -> {
                    val model = (_uiState.value.contentState as? UiState.Success)?.data
                    _uiState.update {
                        it.copy(
                            isBlockedByMe = false,
                            blockActionLoading = false,
                            contentState = model?.let { m ->
                                UiState.Success(m)
                            } ?: it.contentState,
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(blockActionLoading = false) }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onMoreMenu() {
        // TODO: Show bottom sheet with Block/Report options
    }

    private fun loadProfile() {
        if (username.isEmpty()) return
        _uiState.update { it.copy(contentState = UiState.Loading) }

        viewModelScope.launch {
            when (val result = profileRepository.getUserProfile(username)) {
                is ApiResult.Success -> {
                    val uiModel = result.data.toPublicProfileUiModel()
                    userId = uiModel.userId
                    _uiState.update {
                        it.copy(
                            contentState = UiState.Success(uiModel),
                            isFollowing = result.data.profile.isFollowing,
                            isBlockedByMe = result.data.profile.isBlockedByMe,
                        )
                    }
                }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(contentState = UiState.Error(result.message))
                    }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }
}
