package com.example.notesieve.ui.homescreen.viewmodel

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.snapshotFlow
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesieve.UiGridState
import com.example.notesieve.UiListState
import com.example.notesieve.data.local.AppModel
import com.example.notesieve.data.local.NoteSieveModel
import com.example.notesieve.data.local.asNoteSieveModel2
import com.example.notesieve.data.repository.LinkHandlerRepository
import com.example.notesieve.data.repository.NoteSieveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteSieveRepository: NoteSieveRepository,
    private val linkHandlerRepository: LinkHandlerRepository,
    @Named("DefaultDispatcher") private val defaultDispatcher: CoroutineDispatcher,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val searchQueries = MutableStateFlow(SearchQueries())
    private val selectedAppPackageName = MutableStateFlow<String?>(null)
    private val showOptions = MutableStateFlow(ShowOptionsState())

    @OptIn(FlowPreview::class)
    private val showOptionsFlow = snapshotFlow {
        ShowOptionsSnapshot(
            allOptions = showOptions.value.allOptions.toMap(),
            clickedOptions = showOptions.value.clickedOptions.toMap(),
            starredOptions = showOptions.value.starredOptions.toMap()
        )
    }.debounce(50)

    fun resetSelectedApp() {
        selectedAppPackageName.value = null
    }

    private val _linkState = MutableStateFlow<LinkClickState>(LinkClickState.Idle)
    val linkState: StateFlow<LinkClickState> = _linkState.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        LinkClickState.Idle
    )

    val allNotificationsState: StateFlow<UiDataState<UiListState>> = combine(
        noteSieveRepository.getAllNotifications().flowOn(ioDispatcher),
        searchQueries.map { it.all }.distinctUntilChanged(),
        showOptionsFlow.distinctUntilChanged()
    ) { notifications, query, optionsSnapshot ->

        val filteredNotifications = filterNotifications(
            notifications.map {
                it.asNoteSieveModel2(optionsSnapshot.allOptions[it.id] ?: false)
            },
            query
        )

        if (filteredNotifications.isEmpty()) {
            UiDataState.Empty(UiListState(searchQuery = query))
        } else {
            UiDataState.Success(
                UiListState(
                    notifications = filteredNotifications,
                    searchQuery = query
                )
            )
        }

    }.flowOn(defaultDispatcher).distinctUntilChanged().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiDataState.Loading)

    private val allNotifications = noteSieveRepository.getAllNotifications()
        .flowOn(ioDispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val clickedAppState: StateFlow<UiDataState<UiListState>> = combine(
        selectedAppPackageName,
        allNotifications,
        searchQueries.map { it.groupedList },
        showOptionsFlow
    ) { packageName, allNotes, query, optionsSnapshot ->

        if (packageName == null) return@combine UiDataState.Loading

        val selectedAppNotifications = allNotes
            .filter { it.packageName == packageName }
            .map { it.asNoteSieveModel2(optionsSnapshot.clickedOptions[it.id] ?: false) }
            .let { filterNotifications(it, query) }

        if (selectedAppNotifications.isEmpty()) {
            UiDataState.Empty(UiListState(searchQuery = query))
        } else {
            UiDataState.Success(
                UiListState(
                    searchQuery = query,
                    notifications = selectedAppNotifications
                )
            )
        }

    }.flowOn(defaultDispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiDataState.Loading)


    val gridAppsState: StateFlow<UiDataState<UiGridState>> = combine(
        noteSieveRepository.getAppModels(),
        searchQueries.map { it.groupedGrid }
    ) { appModels, query ->

        val filteredAppModels = filterAppNames(appModels, query)

        if (filteredAppModels.isEmpty()) {
            UiDataState.Empty(UiGridState(searchQuery = query))
        } else {
            UiDataState.Success(UiGridState(searchQuery = query, appModels = filteredAppModels))
        }

    }.flowOn(defaultDispatcher)
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiDataState.Loading)

    val starredNotificationsState: StateFlow<UiDataState<UiListState>> = combine(
        noteSieveRepository.getAllStarredNotifications().flowOn(defaultDispatcher),
        searchQueries.map { it.starred },
        showOptionsFlow
    ) { notifications, query, optionsSnapshot ->

        val filteredNotifications = filterNotifications(
            notifications.map {
                it.asNoteSieveModel2(optionsSnapshot.starredOptions[it.id] ?: false)
            },
            query
        )

        if (filteredNotifications.isEmpty()) {
            UiDataState.Empty(UiListState(searchQuery = query))
        } else {
            UiDataState.Success(
                UiListState(
                    notifications = filteredNotifications,
                    searchQuery = query
                )
            )
        }
    }.flowOn(defaultDispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiDataState.Loading)


    private suspend fun filterNotifications(
        notifications: List<NoteSieveModel>,
        query: String
    ): List<NoteSieveModel> {
        return withContext(ioDispatcher) {
            if (query.isEmpty()) {
                notifications
            } else {
                notifications.filter { it.matchesSearch(query) }
            }
        }
    }

    private fun NoteSieveModel.matchesSearch(query: String): Boolean =
        notificationTitle.contains(query, ignoreCase = true) ||
                notificationContent.contains(query, ignoreCase = true) ||
                appName.contains(query, ignoreCase = true)

    private suspend fun filterAppNames(
        appInfos: List<AppModel>,
        query: String
    ): List<AppModel> = withContext(defaultDispatcher) {
        if (query.isEmpty()) appInfos else appInfos.filter { it.matchesAppSearch(query) }
    }

    private fun AppModel.matchesAppSearch(query: String): Boolean =
        appName.contains(query, ignoreCase = true) || packageName.contains(query, ignoreCase = true)

    fun handleStarClick(id: Int, isFavorite: Boolean) {
        viewModelScope.launch(ioDispatcher) {
            if (isFavorite) {
                noteSieveRepository.unstarNotification(id)
            } else {
                noteSieveRepository.starNotification(id)
            }
        }
    }

    fun updateSearchQuery(screen: Screen, query: String) {
        searchQueries.update { currentQueries ->
            when (screen) {
                Screen.ALL -> currentQueries.copy(all = query)
                Screen.STARRED -> currentQueries.copy(starred = query)
                Screen.GROUPED_GRID -> currentQueries.copy(groupedGrid = query)
                Screen.GROUPED_LIST -> currentQueries.copy(groupedList = query)
            }
        }
    }

    fun updateOptionsVisibility(screen: ToggleUpdation, isVisible: Boolean, id: Int) {
        viewModelScope.launch {
            when (screen) {
                ToggleUpdation.ALL -> showOptions.value.allOptions[id] = !isVisible
                ToggleUpdation.CLICKED -> showOptions.value.clickedOptions[id] = !isVisible
                ToggleUpdation.STARRED -> showOptions.value.starredOptions[id] = !isVisible
            }
        }
    }



    fun deleteNotification(notificationId: Int) {
        viewModelScope.launch {
            noteSieveRepository.deleteNotification(notificationId)
        }
    }

    fun setSelectedAppModel(packageName: String) {
        selectedAppPackageName.value = packageName
    }

    fun onLinkClicked(url: String) {
        _linkState.value = when {
            !linkHandlerRepository.isValidUrl(url) -> {
                LinkClickState.PerformingAction(LinkClickAction.InvalidUrl)
            }
            linkHandlerRepository.canOpenLink(url) -> {
                LinkClickState.PerformingAction(LinkClickAction.OpenLink(url))
            } else -> {
                LinkClickState.PerformingAction(LinkClickAction.NoApp)
            }
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
        const val TAG =  "HomeViewModel"
    }

}

enum class Screen {
    ALL, STARRED, GROUPED_GRID, GROUPED_LIST
}

@Stable
data class SearchQueries(
    val all: String = "",
    val starred: String = "",
    val groupedGrid: String = "",
    val groupedList: String = ""
)

data class ShowOptionsSnapshot(
    val allOptions: Map<Int, Boolean>,
    val clickedOptions: Map<Int, Boolean>,
    val starredOptions: Map<Int, Boolean>
)

enum class ToggleUpdation {
    ALL, CLICKED, STARRED
}

sealed interface UiDataState<out T> {
    data object Loading : UiDataState<Nothing>
    data class Empty<T>(val uiState: T) : UiDataState<T>
    data class Success<T>(val uiState: T) : UiDataState<T>
}

sealed interface LinkClickAction {
    data class OpenLink(val url: String) : LinkClickAction
    data object InvalidUrl : LinkClickAction
    data object NoApp : LinkClickAction
}

sealed interface LinkClickState {
    data object Idle : LinkClickState
    data class PerformingAction(val action: LinkClickAction) : LinkClickState
    data object Completed : LinkClickState
}

data class ShowOptionsState(
    val allOptions: SnapshotStateMap<Int, Boolean> = mutableStateMapOf(),
    val clickedOptions: SnapshotStateMap<Int, Boolean> = mutableStateMapOf(),
    val starredOptions: SnapshotStateMap<Int, Boolean> = mutableStateMapOf()
)

