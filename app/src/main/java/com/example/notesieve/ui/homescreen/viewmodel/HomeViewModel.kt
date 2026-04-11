package com.example.notesieve.ui.homescreen.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import com.example.notesieve.data.local.AppModel
import com.example.notesieve.data.local.NoteSieveModel
import com.example.notesieve.data.local.asNoteSieveModel2
import com.example.notesieve.data.repository.LinkHandlerRepository
import com.example.notesieve.data.repository.NoteSieveRepository
import com.example.notesieve.utils.LinkClickAction
import com.example.notesieve.utils.LinkClickState
import com.example.notesieve.utils.UiDataState
import com.example.notesieve.utils.UiGridState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteSieveRepository: NoteSieveRepository,
    private val linkHandlerRepository: LinkHandlerRepository,
    @Named("DefaultDispatcher") private val defaultDispatcher: CoroutineDispatcher,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _searchQueries = MutableStateFlow(SearchQueries())
    private val selectedAppPackageName = MutableStateFlow<String?>(null)

    private val _expandedCards = MutableStateFlow(ExpandedCardsState())

    val allSearchQuery: StateFlow<String> = _searchQueries
        .map { it.all }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val starredSearchQuery: StateFlow<String> = _searchQueries
        .map { it.starred }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    val groupedListSearchQuery: StateFlow<String> = _searchQueries
        .map { it.groupedList }
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), "")

    private val _linkState = MutableStateFlow<LinkClickState>(LinkClickState.Idle)
    val linkState: StateFlow<LinkClickState> = _linkState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        LinkClickState.Idle
    )

    val allNotificationsPaged: Flow<PagingData<NoteSieveModel>> = allSearchQuery
        .flatMapLatest { query ->
            noteSieveRepository.getAllNotificationsPaged(query)
                .map { pagingData ->
                    pagingData.map { entity -> entity.asNoteSieveModel2(false) }
                }
        }
        .flowOn(defaultDispatcher)
        .cachedIn(viewModelScope)
        .combine(_expandedCards) { pagingData, expandedState ->
            pagingData.map { model ->
                model.copy(showOptions = expandedState.allExpanded[model.id] ?: false)
            }
        }

    val starredNotificationsPaged: Flow<PagingData<NoteSieveModel>> = starredSearchQuery
        .flatMapLatest { query ->
            noteSieveRepository.getAllStarredNotificationsPaged(query)
                .map { pagingData ->
                    pagingData.map { entity -> entity.asNoteSieveModel2(false) }
                }
        }
        .flowOn(defaultDispatcher)
        .cachedIn(viewModelScope)
        .combine(_expandedCards) { pagingData, expandedState ->
            pagingData.map { model ->
                model.copy(showOptions = expandedState.starredExpanded[model.id] ?: false)
            }
        }

    val clickedAppPaged: Flow<PagingData<NoteSieveModel>> = combine(
        selectedAppPackageName,
        groupedListSearchQuery
    ) { packageName, query -> packageName to query }
        .distinctUntilChanged()
        .flatMapLatest { (packageName, query) ->
            if (packageName == null) return@flatMapLatest flowOf(PagingData.empty())
            noteSieveRepository.getNotificationsForAppPaged(packageName, query)
                .map { pagingData ->
                    pagingData.map { entity -> entity.asNoteSieveModel2(false) }
                }
        }
        .flowOn(defaultDispatcher)
        .cachedIn(viewModelScope)
        .combine(_expandedCards) { pagingData, expandedState ->
            pagingData.map { model ->
                model.copy(showOptions = expandedState.clickedExpanded[model.id] ?: false)
            }
        }

    val gridAppsState: StateFlow<UiDataState<UiGridState>> = combine(
        noteSieveRepository.getAppModels(),
        _searchQueries.map { it.groupedGrid }.distinctUntilChanged()
    ) { appModels, query ->
        val filtered = filterAppNames(appModels, query)
        if (filtered.isEmpty()) {
            UiDataState.Empty(UiGridState(searchQuery = query))
        } else {
            UiDataState.Success(UiGridState(searchQuery = query, appModels = filtered))
        }
    }
        .distinctUntilChanged()
        .flowOn(defaultDispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiDataState.Loading)

    private fun filterAppNames(appInfos: List<AppModel>, query: String): List<AppModel> =
        if (query.isEmpty()) appInfos else appInfos.filter { it.matchesAppSearch(query) }

    private fun AppModel.matchesAppSearch(query: String): Boolean =
        appName.contains(query, ignoreCase = true) ||
                packageName.contains(query, ignoreCase = true)

    fun resetSelectedApp() {
        selectedAppPackageName.value = null
    }

    fun setSelectedAppModel(packageName: String) {
        selectedAppPackageName.value = packageName
    }

    fun updateSearchQuery(screen: Screen, query: String) {
        _searchQueries.update { current ->
            when (screen) {
                Screen.ALL          -> current.copy(all = query)
                Screen.STARRED      -> current.copy(starred = query)
                Screen.GROUPED_GRID -> current.copy(groupedGrid = query)
                Screen.GROUPED_LIST -> current.copy(groupedList = query)
            }
        }
    }

    fun updateOptionsVisibility(screen: ToggleUpdation, isVisible: Boolean, id: Int) {
        _expandedCards.update { current ->
            when (screen) {
                ToggleUpdation.ALL -> current.copy(
                    allExpanded = current.allExpanded + (id to !isVisible)
                )
                ToggleUpdation.CLICKED -> current.copy(
                    clickedExpanded = current.clickedExpanded + (id to !isVisible)
                )
                ToggleUpdation.STARRED -> current.copy(
                    starredExpanded = current.starredExpanded + (id to !isVisible)
                )
            }
        }
    }

    fun handleStarClick(id: Int, isFavorite: Boolean) {
        viewModelScope.launch {
            if (isFavorite) noteSieveRepository.unstarNotification(id)
            else noteSieveRepository.starNotification(id)
        }
    }

    fun deleteNotification(notificationId: Int) {
        viewModelScope.launch {
            noteSieveRepository.deleteNotification(notificationId)
        }
    }

    fun onLinkClicked(url: String) {
        _linkState.value = when {
            !linkHandlerRepository.isValidUrl(url) ->
                LinkClickState.PerformingAction(LinkClickAction.InvalidUrl)
            linkHandlerRepository.canOpenLink(url) ->
                LinkClickState.PerformingAction(LinkClickAction.OpenLink(url))
            else ->
                LinkClickState.PerformingAction(LinkClickAction.NoApp)
        }
    }

    fun openLink(url: String) {
        linkHandlerRepository.openLink(url)
        _linkState.value = LinkClickState.Completed
    }

    fun resetState() {
        _linkState.value = LinkClickState.Idle
    }

    companion object {
        const val TAG = "HomeViewModel"
    }
}

enum class Screen {
    ALL, STARRED, GROUPED_GRID, GROUPED_LIST
}

enum class ToggleUpdation {
    ALL, CLICKED, STARRED
}

data class SearchQueries(
    val all: String = "",
    val starred: String = "",
    val groupedGrid: String = "",
    val groupedList: String = ""
)

data class ExpandedCardsState(
    val allExpanded: Map<Int, Boolean> = emptyMap(),
    val clickedExpanded: Map<Int, Boolean> = emptyMap(),
    val starredExpanded: Map<Int, Boolean> = emptyMap()
)