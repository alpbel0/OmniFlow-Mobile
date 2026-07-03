package com.omniflow.ui.trips

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.core.network.ApiResult
import com.omniflow.data.models.trips.TripModel
import com.omniflow.data.models.trips.TripStatusDto
import com.omniflow.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class MyTripsViewModel @Inject constructor(
    private val tripRepository: TripRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(MyTripsUiState())
    val uiState: StateFlow<MyTripsUiState> = _uiState.asStateFlow()

    private var allTrips: List<TripModel>? = null

    init {
        loadDrafts()
    }

    fun onTabSelected(tab: MyTripsTab) {
        _uiState.update { it.copy(selectedTab = tab) }
        when (tab) {
            MyTripsTab.DRAFT -> loadDrafts()
            MyTripsTab.PUBLISHED -> loadPublished()
            MyTripsTab.SAVED -> loadSaved()
        }
    }

    fun onFilterSelected(filter: String) {
        _uiState.update { it.copy(selectedCollection = filter) }
    }

    fun onAddCollection() {
        _uiState.update { state ->
            val newName = "Koleksiyon ${state.collections.size}"
            state.copy(collections = state.collections + newName)
        }
    }

    fun onRetry() {
        when (_uiState.value.selectedTab) {
            MyTripsTab.DRAFT -> loadDrafts()
            MyTripsTab.PUBLISHED -> loadPublished()
            MyTripsTab.SAVED -> loadSaved()
        }
    }

    private fun loadDrafts() {
        viewModelScope.launch {
            _uiState.update { it.copy(isDraftsLoading = true, error = null) }
            when (val result = tripRepository.getMyTrips(
                status = TripStatusDto.Draft.value,
            )) {
                is ApiResult.Success -> {
                    allTrips = result.data.data
                }
                is ApiResult.Loading -> { /* no-op */ }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isDraftsLoading = false, error = result.message.toString())
                    }
                    return@launch
                }
            }
            val drafts = allTrips
                ?.filter { it.status == TripStatusDto.Draft }
                ?.map { it.toDraftTrip() }
                .orEmpty()
            _uiState.update { it.copy(draftTrips = drafts, isDraftsLoading = false) }
        }
    }

    private fun loadPublished() {
        viewModelScope.launch {
            _uiState.update { it.copy(isPublishedLoading = true, error = null) }
            val publishedList = when (val publishedResult = tripRepository.getMyTrips(
                status = TripStatusDto.Published.value,
            )) {
                is ApiResult.Success -> publishedResult.data.data
                is ApiResult.Loading -> emptyList()
                is ApiResult.Error -> emptyList()
            }
            val archivedList = when (val archivedResult = tripRepository.getMyTrips(
                status = TripStatusDto.Archived.value,
            )) {
                is ApiResult.Success -> archivedResult.data.data
                is ApiResult.Loading -> emptyList()
                is ApiResult.Error -> emptyList()
            }
            allTrips = publishedList + archivedList
            val published = allTrips
                ?.filter { it.status == TripStatusDto.Published || it.status == TripStatusDto.Archived }
                ?.map { it.toPublishedTrip() }
                ?.sortedByDescending { it.badge != PublishedBadge.ARCHIVED }
                .orEmpty()
            _uiState.update {
                it.copy(
                    publishedTrips = published,
                    publishSummary = computePublishSummary(published),
                    isPublishedLoading = false,
                )
            }
        }
    }

    private fun loadSaved() {
        viewModelScope.launch {
            _uiState.update { it.copy(isSavedLoading = true, error = null) }
            when (val result = tripRepository.getSavedTrips()) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            savedTrips = result.data.data.map { m -> m.toSavedTrip() },
                            isSavedLoading = false,
                        )
                    }
                }
                is ApiResult.Loading -> { /* no-op */ }
                is ApiResult.Error -> {
                    _uiState.update {
                        it.copy(isSavedLoading = false, error = result.message.toString())
                    }
                }
            }
        }
    }
}
