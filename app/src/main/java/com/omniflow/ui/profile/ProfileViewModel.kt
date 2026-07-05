package com.omniflow.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.core.common.UiState
import com.omniflow.core.network.ApiResult
import com.omniflow.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val profileRepository: ProfileRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState = _uiState.asStateFlow()

    private var cachedModel: ProfileUiModel? = null

    init {
        loadProfile()
    }

    fun onRefresh() {
        if (_uiState.value.isRefreshing) return
        loadProfile(isRefresh = true)
    }

    fun retry() {
        loadProfile()
    }

    fun onTabChange(tab: ProfileTab) {
        _uiState.update { it.copy(activeTab = tab) }
    }

    fun onEditProfile() {
        val model = cachedModel ?: return
        _uiState.update {
            it.copy(
                variant = ProfileVariant.EDIT,
                editBio = model.bio,
                editLocation = "",
                editSelectedStyles = emptySet(),
            )
        }
    }

    fun onBioChange(bio: String) {
        _uiState.update { it.copy(editBio = bio) }
    }

    fun onLocationChange(location: String) {
        _uiState.update { it.copy(editLocation = location) }
    }

    fun onStyleToggle(style: String) {
        _uiState.update { state ->
            val newStyles = if (style in state.editSelectedStyles) {
                state.editSelectedStyles - style
            } else {
                state.editSelectedStyles + style
            }
            state.copy(editSelectedStyles = newStyles)
        }
    }

    fun onSaveEdit() {
        val bio = _uiState.value.editBio
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            when (profileRepository.updateBio(bio)) {
                is ApiResult.Success -> {
                    _uiState.update { it.copy(isSaving = false, variant = ProfileVariant.VIEW) }
                    loadProfile()
                }
                is ApiResult.Error -> {
                    _uiState.update { it.copy(isSaving = false) }
                }
                is ApiResult.Loading -> Unit
            }
        }
    }

    fun onCancelEdit() {
        _uiState.update { it.copy(variant = ProfileVariant.VIEW) }
    }

    fun onChangePhoto() {
        // TODO: Photo picker → uploadProfilePhoto
    }

    private fun loadProfile(isRefresh: Boolean = false) {
        _uiState.update {
            if (isRefresh) it.copy(isRefreshing = true)
            else it.copy(contentState = UiState.Loading, variant = ProfileVariant.VIEW)
        }

        viewModelScope.launch {
            when (val result = profileRepository.getMyProfileContent()) {
                is ApiResult.Success -> {
                    val uiModel = result.data.toUiModel()
                    cachedModel = uiModel
                    val variant = if (uiModel.trips.isEmpty() && uiModel.posts.isEmpty()) {
                        ProfileVariant.EMPTY
                    } else {
                        ProfileVariant.VIEW
                    }
                    _uiState.update {
                        it.copy(
                            contentState = UiState.Success(uiModel),
                            variant = variant,
                            isRefreshing = false,
                        )
                    }
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
}
