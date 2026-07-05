package com.omniflow.ui.social

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.core.common.UiState
import com.omniflow.core.network.ApiResult
import com.omniflow.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class CommunityViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(CommunityUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadData()
    }

    fun retry() {
        loadData()
    }

    fun onFollowSuggested(user: SuggestedUserUiModel) {
        toggleFollow(user.id)
    }

    fun onFollowContrib(user: ContributorUiModel) {
        toggleFollow(user.id)
    }

    private fun toggleFollow(userId: String) {
        if (userId.isEmpty() || userId in _uiState.value.followLoadingIds) return

        _uiState.update { state ->
            state.copy(
                followLoadingIds = state.followLoadingIds + userId,
                suggestedContent = (state.suggestedContent as? UiState.Success)?.let { success ->
                    UiState.Success(success.data.map {
                        if (it.id == userId) it.copy(isFollowing = !it.isFollowing) else it
                    })
                } ?: state.suggestedContent,
                contributorsContent = (state.contributorsContent as? UiState.Success)?.let { success ->
                    UiState.Success(success.data.map {
                        if (it.id == userId) it.copy(isFollowing = !it.isFollowing) else it
                    })
                } ?: state.contributorsContent,
            )
        }

        viewModelScope.launch {
            val newIsFollowing = _uiState.value.suggestedContent
                .let { (it as? UiState.Success)?.data?.find { u -> u.id == userId }?.isFollowing }
                ?: _uiState.value.contributorsContent
                    .let { (it as? UiState.Success)?.data?.find { u -> u.id == userId }?.isFollowing }
                ?: true

            val result = if (newIsFollowing) {
                profileRepository.followUser(userId)
            } else {
                profileRepository.unfollowUser(userId)
            }

            if (result is ApiResult.Error) {
                _uiState.update { state ->
                    state.copy(
                        followLoadingIds = state.followLoadingIds - userId,
                        suggestedContent = (state.suggestedContent as? UiState.Success)?.let { success ->
                            UiState.Success(success.data.map {
                                if (it.id == userId) it.copy(isFollowing = !it.isFollowing) else it
                            })
                        } ?: state.suggestedContent,
                    )
                }
            } else {
                _uiState.update { it.copy(followLoadingIds = it.followLoadingIds - userId) }
            }
        }
    }

    private fun loadData() {
        _uiState.update {
            it.copy(
                suggestedContent = UiState.Loading,
                contributorsContent = UiState.Loading,
            )
        }

        viewModelScope.launch {
            val suggested = async {
                when (val r = profileRepository.getSuggestedFollows()) {
                    is ApiResult.Success -> UiState.Success(r.data.toSuggestedUiModels())
                    is ApiResult.Error -> UiState.Error(r.message)
                    is ApiResult.Loading -> UiState.Loading
                }
            }
            val contributors = async {
                when (val r = profileRepository.getTopContributors()) {
                    is ApiResult.Success -> UiState.Success(r.data.toContributorUiModels())
                    is ApiResult.Error -> UiState.Error(r.message)
                    is ApiResult.Loading -> UiState.Loading
                }
            }

            _uiState.update {
                it.copy(
                    suggestedContent = suggested.await(),
                    contributorsContent = contributors.await(),
                )
            }
        }
    }
}
