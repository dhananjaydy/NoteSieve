package com.example.notesieve.ui.homescreen.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesieve.UiGridState
import com.example.notesieve.UiListState
import com.example.notesieve.data.local.AppModel
import com.example.notesieve.data.local.NoteSieveModel
import com.example.notesieve.data.local.asNoteSieveModel2
import com.example.notesieve.data.repository.NoteSieveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
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
    private val repository: NoteSieveRepository,
    @Named("DefaultDispatcher") private val defaultDispatcher: CoroutineDispatcher,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher
): ViewModel() {

    private val searchQueries = MutableStateFlow(SearchQueries())
    private val selectedAppPackageName = MutableStateFlow<String?>("com.example.notesieve")
    private val showOptions = MutableStateFlow(ShowOptionsState())

    val allNotificationsState: StateFlow<UiDataState<UiListState>> = combine(
        repository.getAllNotifications().flowOn(Dispatchers.IO),
        searchQueries.map { it.all },
        showOptions
    ) { notifications, query, optionsMap ->

        val allOptions = optionsMap.allOptions
        val filteredNotifications = filterNotifications(
            notifications.map {
                it.asNoteSieveModel2(allOptions[it.id] ?: false)
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

    }.flowOn(defaultDispatcher).stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiDataState.Loading)

    val clickedAppState: StateFlow<UiDataState<UiListState>> = combine(
        selectedAppPackageName,
        repository.getAllStarredNotifications(),
        searchQueries.map { it.groupedList },
        showOptions
    ) { packageName, starredNotifications, query, optionsMap ->

        val clickedMap = optionsMap.clickedOptions
        val selectedAppNotifications = packageName?.let {
            filterNotifications(
                repository.fetchNotificationsForApp(packageName).map { it.asNoteSieveModel2(clickedMap[it.id] ?: false) },
                query
            )
        }

        if (selectedAppNotifications!!.isEmpty()) {
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
        repository.getAppModels(),
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
        repository.getAllStarredNotifications().flowOn(Dispatchers.IO),
        searchQueries.map { it.starred },
        showOptions
    ) { notifications, query, optionsMap ->

        val starredOptions = optionsMap.starredOptions
        val filteredNotifications = filterNotifications(
            notifications.map {
                it.asNoteSieveModel2(starredOptions[it.id] ?: false)
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
        return withContext(defaultDispatcher) {
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
                repository.unstarNotification(id)
            } else {
                repository.starNotification(id)
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
            showOptions.update { currentState ->
                when (screen) {
                    ToggleUpdation.ALL -> currentState.copy(
                        allOptions = currentState.allOptions + (id to !isVisible)
                    )
                    ToggleUpdation.CLICKED -> currentState.copy(
                        clickedOptions = currentState.clickedOptions + (id to !isVisible)
                    )
                    ToggleUpdation.STARRED -> currentState.copy(
                        starredOptions = currentState.starredOptions + (id to !isVisible)
                    )
                }
            }
        }
    }
    
    fun deleteNotification(notificationId: Int) {
        viewModelScope.launch {
            repository.deleteNotification(notificationId)
        }
    }

    fun setSelectedAppModel(packageName: String) {
        selectedAppPackageName.value = packageName
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

data class ShowOptionsState(
    val allOptions: Map<Int, Boolean> = mapOf(),
    val clickedOptions: Map<Int, Boolean> = mapOf(),
    val starredOptions: Map<Int, Boolean> = mapOf()
)

enum class ToggleUpdation {
    ALL, CLICKED, STARRED
}

sealed interface UiDataState<out T> {
    data object Loading : UiDataState<Nothing>
    data class Empty<T>(val uiState: T) : UiDataState<T>
    data class Success<T>(val uiState: T) : UiDataState<T>
}

