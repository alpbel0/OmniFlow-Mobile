package com.omniflow.ui.trips

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.omniflow.core.auth.SessionState
import com.omniflow.core.auth.TokenStore
import com.omniflow.core.network.ApiResult
import com.omniflow.data.remote.ProfileService
import com.omniflow.data.repository.TripPanePreferences
import com.omniflow.data.repository.TripPanePreferencesRepository
import com.omniflow.data.repository.TripRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class TripDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val tripRepository: TripRepository,
    private val profileService: ProfileService,
    private val panePreferencesRepository: TripPanePreferencesRepository,
    private val tokenStore: TokenStore,
) : ViewModel() {

    private val tripId: String = savedStateHandle.get<String>("tripId") ?: ""

    private val isAnonymous: Boolean
        get() = tokenStore.sessionState.value != SessionState.SignedIn

    private val _uiState = MutableStateFlow(TripDetailUiState())
    val uiState: StateFlow<TripDetailUiState> = _uiState.asStateFlow()

    init {
        loadPanePreferences()
        loadTripDetail()
    }

    fun onMapModeChange(mode: MapMode) {
        _uiState.update { it.copy(mapMode = mode) }
        if (mode == MapMode.ROAD &&
            _uiState.value.routePoints.isEmpty() &&
            !_uiState.value.routeUnavailable
        ) {
            loadRoute()
        }
    }

    /**
     * Spec: "ORS proxy hata dönerse sessizce Kuş Bakışı'na dön, Yol toggle'ı
     * devre dışı kalır, ayrı bir hata state'i yok."
     */
    private fun loadRoute() = viewModelScope.launch {
        when (val result = tripRepository.getRoute(tripId)) {
            is ApiResult.Success -> _uiState.update { it.copy(routePoints = result.data) }
            is ApiResult.Error -> _uiState.update {
                it.copy(mapMode = MapMode.BIRDS_EYE, routeUnavailable = true)
            }
            else -> Unit
        }
    }

    fun onDaySelected(index: Int) {
        _uiState.update { it.copy(selectedDayIndex = index) }
        persistPanePreferences()
    }

    fun onDisplayModeChanged(mode: DisplayMode) {
        _uiState.update { it.copy(displayMode = mode) }
        persistPanePreferences()
    }

    /**
     * Sadece drag-end / tap-snap anında çağrılır (frame başına değil).
     * Compose katmanı canlı drag state'i local tutar, commit edilen değer buraya gelir.
     */
    fun onPaneResize(detaylarFraction: Float, mapFraction: Float) {
        _uiState.update {
            it.copy(
                detaylarFraction = detaylarFraction.coerceIn(DETAYLAR_MIN, DETAYLAR_MAX),
                mapFraction = mapFraction.coerceIn(MAP_MIN, MAP_MAX),
            )
        }
        persistPanePreferences()
    }

    fun onLandscapePaneResize(timelineFraction: Float, detaylarFraction: Float) {
        _uiState.update {
            it.copy(
                landscapeTimelineFraction = timelineFraction.coerceIn(LANDSCAPE_TIMELINE_MIN, LANDSCAPE_TIMELINE_MAX),
                landscapeDetaylarFraction = detaylarFraction.coerceIn(LANDSCAPE_DETAYLAR_MIN, LANDSCAPE_DETAYLAR_MAX),
            )
        }
        persistPanePreferences()
    }

    /**
     * Backend (B0.9) henüz yok → `updateChecklistItem` her zaman hata dönecek.
     * Kullanıcı kararı: bilerek geri almıyoruz (revert yok) — toggle lokalde kalıcı
     * görünür, hata sessizce yutulur. B0.9 gelince upvote/save'deki gibi
     * optimistic+revert-on-error'a geçilecek.
     */
    fun onToggleChecklistItem(itemKey: String) {
        if (!_uiState.value.isOwner) return
        var newValue = false
        _uiState.update { state ->
            val updatedCards = state.categoryCards.map { card ->
                card.copy(
                    entries = card.entries.map { entry ->
                        if (entry.itemKey == itemKey) {
                            newValue = !entry.isConfirmed
                            entry.copy(isConfirmed = newValue)
                        } else entry
                    },
                )
            }
            state.copy(
                categoryCards = updatedCards,
                dayEntries = updatedCards.flatMap { it.entries }
                    .sortedWith(compareBy({ it.dayIndex }, { it.time })),
            )
        }
        viewModelScope.launch {
            tripRepository.updateChecklistItem(tripId, itemKey, newValue)
        }
    }

    fun onUnlockEntry(entryId: String) {
        if (!_uiState.value.isOwner) return
        updateEntry(entryId) { it.copy(isLocked = false) }
        viewModelScope.launch { tripRepository.unlockTimelineEntry(tripId, entryId) }
    }

    fun onDeleteEntry(entryId: String) {
        val entry = _uiState.value.dayEntries.find { it.id == entryId } ?: return
        if (!_uiState.value.isOwner || entry.isLocked) return
        updateEntry(entryId) { it.copy(hasLinkedEntry = false) }
        _uiState.update { it.copy(selectedEntryId = null) }
        viewModelScope.launch { tripRepository.deleteTimelineEntry(tripId, entryId) }
    }

    private fun updateEntry(entryId: String, transform: (CategoryEntry) -> CategoryEntry) {
        _uiState.update { state ->
            val updated = state.categoryCards.map { card ->
                card.copy(entries = card.entries.map { if (it.id == entryId) transform(it) else it })
            }
            state.copy(
                categoryCards = updated,
                dayEntries = updated.flatMap { it.entries }
                    .sortedWith(compareBy({ it.dayIndex }, { it.time })),
            )
        }
    }

    fun onViewEntryDetail(entryId: String) {
        _uiState.update { it.copy(selectedEntryId = entryId) }
    }

    fun onDismissEntryDetail() {
        _uiState.update { it.copy(selectedEntryId = null) }
    }

    fun onAction(action: TripDetailAction) {
        if (action in AUTH_REQUIRED_ACTIONS && isAnonymous) {
            _uiState.update { it.copy(showLoginRequiredDialog = true) }
            return
        }
        when (action) {
            TripDetailAction.PUBLISH -> publish()
            TripDetailAction.ARCHIVE -> archive()
            TripDetailAction.UNARCHIVE -> unarchive()
            TripDetailAction.UPVOTE -> toggleUpvote()
            TripDetailAction.SAVE -> onSaveClick()
            TripDetailAction.FORK -> fork()
            TripDetailAction.UNPUBLISH, TripDetailAction.DELETE -> { /* confirm-gated, see onRequest */ }
        }
    }

    fun onDismissLoginRequiredDialog() {
        _uiState.update { it.copy(showLoginRequiredDialog = false) }
    }

    /**
     * Spec: Kaydet henüz kaydedilmemişse koleksiyon-seçim bottom sheet'i açar
     * (kaydedince hangi koleksiyona gideceği sorulur); zaten kaydedilmişse
     * ("Kaydı Kaldır") direkt toggle eder, bottom sheet açılmaz.
     */
    private fun onSaveClick() {
        if (_uiState.value.isSaved) {
            toggleSave()
        } else {
            _uiState.update { it.copy(showCollectionPicker = true) }
        }
    }

    fun onDismissCollectionPicker() {
        _uiState.update { it.copy(showCollectionPicker = false) }
    }

    fun onCollectionSelected(collection: String) {
        _uiState.update { it.copy(showCollectionPicker = false) }
        toggleSave()
    }

    fun onRequestMoveToDraft() {
        _uiState.update { it.copy(showMoveToDraftDialog = true) }
    }

    fun onDismissMoveToDraftDialog() {
        _uiState.update { it.copy(showMoveToDraftDialog = false) }
    }

    fun onConfirmMoveToDraft() {
        _uiState.update { it.copy(showMoveToDraftDialog = false) }
        unpublish()
    }

    fun onRequestDelete() {
        _uiState.update { it.copy(showDeleteConfirmDialog = true) }
    }

    fun onDismissDeleteDialog() {
        _uiState.update { it.copy(showDeleteConfirmDialog = false) }
    }

    fun onConfirmDelete() {
        _uiState.update { it.copy(showDeleteConfirmDialog = false) }
        delete()
    }

    private fun publish() = runAction(TripDetailAction.PUBLISH) {
        tripRepository.publishTrip(tripId)
    }

    private fun archive() = runAction(TripDetailAction.ARCHIVE) {
        tripRepository.archiveTrip(tripId)
    }

    private fun unarchive() = runAction(TripDetailAction.UNARCHIVE) {
        tripRepository.unarchiveTrip(tripId)
    }

    private fun unpublish() = runAction(TripDetailAction.UNPUBLISH) {
        tripRepository.unpublishTrip(tripId)
    }

    private fun delete() = runAction(TripDetailAction.DELETE) {
        tripRepository.deleteTrip(tripId)
    }

    private fun toggleUpvote() = viewModelScope.launch {
        val upvoted = _uiState.value.isUpvoted
        _uiState.update { it.copy(isUpvoted = !upvoted, upvoteCount = it.upvoteCount + if (upvoted) -1 else 1) }
        val result = if (upvoted) tripRepository.removeUpvote(tripId) else tripRepository.upvoteTrip(tripId)
        if (result is ApiResult.Error) {
            _uiState.update { it.copy(isUpvoted = upvoted, upvoteCount = it.upvoteCount + if (upvoted) 1 else -1) }
        }
    }

    private fun toggleSave() = viewModelScope.launch {
        val saved = _uiState.value.isSaved
        _uiState.update { it.copy(isSaved = !saved) }
        val result = if (saved) tripRepository.unsaveTrip(tripId) else tripRepository.saveTrip(tripId)
        if (result is ApiResult.Error) {
            _uiState.update { it.copy(isSaved = saved) }
        }
    }

    private fun fork() = runAction(TripDetailAction.FORK) {
        tripRepository.forkTrip(tripId)
    }

    private fun runAction(action: TripDetailAction, block: suspend () -> ApiResult<*>) {
        viewModelScope.launch {
            _uiState.update { it.copy(actionInProgress = action.name) }
            val result = block()
            if (result is ApiResult.Error) {
                _uiState.update { it.copy(error = result.message.toString()) }
            }
            _uiState.update { it.copy(actionInProgress = null) }
            loadTripDetail()
        }
    }

    private fun loadTripDetail() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            var currentUserId: String? = null
            try {
                val me = profileService.getMyProfile()
                currentUserId = me.id
            } catch (_: Exception) { }
            when (val result = tripRepository.getTripDetail(tripId)) {
                is ApiResult.Success -> {
                    // Bug fix: mevcut görüntüleme-tercihi alanlarını (panele/report
                    // oranları, displayMode, selectedDayIndex) koru — mapper taze
                    // sonucunun default'larıyla komple üstüne yazma.
                    val fresh = result.data.toTripDetailUiState(currentUserId)
                    _uiState.update { current ->
                        fresh.copy(
                            isLoading = false,
                            detaylarFraction = current.detaylarFraction,
                            mapFraction = current.mapFraction,
                            landscapeTimelineFraction = current.landscapeTimelineFraction,
                            landscapeDetaylarFraction = current.landscapeDetaylarFraction,
                            displayMode = current.displayMode,
                            selectedDayIndex = current.selectedDayIndex,
                        )
                    }
                    loadSecondaryData()
                }
                is ApiResult.Error -> {
                    // Task 3.3.3 — teknik hata detayı (404/500/network) hiç sızdırılmaz;
                    // Draft/Archived trip'in var olup olmadığı bile belli edilmez. Gerçek
                    // erişim-engelleme backend'in işi (B0.10, henüz yok).
                    _uiState.update { it.copy(isLoading = false, error = "Bu gezi bulunamadı") }
                }
                else -> _uiState.update { it.copy(isLoading = false) }
            }
        }
    }

    /**
     * Task 3.2.9 — Veri Orkestrasyonu. GetById yukarıda zaten bloklayıcı olarak
     * yüklendi (tam ekran hata/retry). Bu 3 çağrı SPEC'e göre bağımsız/non-blocking
     * olmalı — ama backend'in hiçbiri (Timeline gerçek endpoint, Budget Summary,
     * Checklist/B0.9) henüz yok, yani hepsi hata dönecek. Mevcut mapper mock'u
     * (categoryCards/budget) zaten doğru veriyi gösterdiği için sonuç SESSİZCE
     * yutulur — kullanıcıya hata/retry gösterilmez (getTripDetail/getMyTrips'teki
     * mock-fallback konvansiyonuyla tutarlı). Backend gelince: her ApiResult.Success
     * dalı categoryCards/budget/checklist'i gerçek veriyle besleyecek şekilde
     * genişletilecek, o zaman "inline hata + Tekrar Dene" UI'ı da eklenecek
     * (şu an test edilemeyeceği için bilerek yazılmadı).
     */
    private fun loadSecondaryData() {
        viewModelScope.launch {
            awaitAll(
                async { tripRepository.getTimeline(tripId) },
                async { tripRepository.getBudgetSummary(tripId) },
                async { tripRepository.getChecklist(tripId) },
            )
        }
    }

    private fun loadPanePreferences() {
        viewModelScope.launch {
            val prefs = panePreferencesRepository.get(tripId) ?: return@launch
            _uiState.update {
                it.copy(
                    detaylarFraction = prefs.detaylarFraction.coerceIn(DETAYLAR_MIN, DETAYLAR_MAX),
                    mapFraction = prefs.mapFraction.coerceIn(MAP_MIN, MAP_MAX),
                    landscapeTimelineFraction = prefs.landscapeTimelineFraction.coerceIn(LANDSCAPE_TIMELINE_MIN, LANDSCAPE_TIMELINE_MAX),
                    landscapeDetaylarFraction = prefs.landscapeDetaylarFraction.coerceIn(LANDSCAPE_DETAYLAR_MIN, LANDSCAPE_DETAYLAR_MAX),
                    displayMode = runCatching { DisplayMode.valueOf(prefs.displayMode) }
                        .getOrDefault(DisplayMode.CATEGORY),
                    selectedDayIndex = prefs.selectedDayIndex,
                )
            }
        }
    }

    private fun persistPanePreferences() {
        val s = _uiState.value
        viewModelScope.launch {
            panePreferencesRepository.save(
                tripId,
                TripPanePreferences(
                    detaylarFraction = s.detaylarFraction,
                    mapFraction = s.mapFraction,
                    landscapeTimelineFraction = s.landscapeTimelineFraction,
                    landscapeDetaylarFraction = s.landscapeDetaylarFraction,
                    displayMode = s.displayMode.name,
                    selectedDayIndex = s.selectedDayIndex,
                ),
            )
        }
    }
}

enum class TripDetailAction { PUBLISH, ARCHIVE, UNARCHIVE, UNPUBLISH, DELETE, UPVOTE, SAVE, FORK }

private val AUTH_REQUIRED_ACTIONS = setOf(TripDetailAction.UPVOTE, TripDetailAction.FORK, TripDetailAction.SAVE)

internal const val DETAYLAR_MIN = 0f
internal const val DETAYLAR_MAX = 0.30f
internal const val MAP_MIN = 0f
internal const val MAP_MAX = 0.40f
internal const val DETAYLAR_DEFAULT = 0.30f
internal const val MAP_DEFAULT = 0.30f
internal const val LANDSCAPE_TIMELINE_MIN = 0f
internal const val LANDSCAPE_TIMELINE_MAX = 0.60f
internal const val LANDSCAPE_TIMELINE_DEFAULT = 0.40f
internal const val LANDSCAPE_DETAYLAR_MIN = 0f
internal const val LANDSCAPE_DETAYLAR_MAX = 0.30f
internal const val LANDSCAPE_DETAYLAR_DEFAULT = 0.30f
internal const val EPSILON = 0.0001f