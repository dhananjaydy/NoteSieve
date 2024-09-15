package com.example.notesieve.ui.homescreen.viewmodel

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesieve.GroupedScreenUiState
import com.example.notesieve.UiState
import com.example.notesieve.data.local.AppModel
import com.example.notesieve.data.local.NoteSieveModel
import com.example.notesieve.data.local.asNoteSieveModel
import com.example.notesieve.data.repository.NoteSieveRepository
import dagger.hilt.android.lifecycle.HiltViewModel
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
import javax.inject.Inject

@HiltViewModel
class NoteSieveViewModel @Inject constructor(
    private val repository: NoteSieveRepository
): ViewModel() {

    private val searchQueries = MutableStateFlow(SearchQueries())
    private val selectedAppPackageName = MutableStateFlow<String?>(null)
    private val showOptions = MutableStateFlow(ShowOptionsHashMap())

    val allNotificationsState: StateFlow<UiDataState<UiState>> = combine(
        repository.getAllNotifications().flowOn(Dispatchers.IO),
        searchQueries.map { it.all }
    ) { notifications, query ->
        val filteredNotifications = filterNotifications(
            notifications.map { it.asNoteSieveModel() },
            query
        )

        if (filteredNotifications.isEmpty()) {
            UiDataState.Empty(UiState(searchQuery = query))
        } else {
            UiDataState.Success(
                UiState(
                    notifications = filteredNotifications,
                    searchQuery = query
                )
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UiDataState.Loading)

    val starredNotificationsState2: StateFlow<UiDataState<UiState>> = combine(
        repository.getAllStarredNotifications().flowOn(Dispatchers.IO),
        searchQueries.map { it.starred }
    ) { notifications, query ->

        val filteredNotifications = filterNotifications(
            notifications.map { it.asNoteSieveModel() },
            query
        )

        if (filteredNotifications.isEmpty()) {
            UiDataState.Empty(UiState(searchQuery = query))
        } else {
            UiDataState.Success(
                UiState(
                    notifications = filteredNotifications,
                    searchQuery = query
                )
            )
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiDataState.Loading)


    val groupedNotificationsState: StateFlow<GroupedScreenUiState> = combine(
        repository.getAppModels().flowOn(Dispatchers.IO),
        selectedAppPackageName,
        searchQueries
    ) { appModels, selectedApp, queries ->
        val filteredApps = filterAppNames(appModels, queries.groupedGrid)
        val selectedAppNotifications = selectedApp?.let {
            filterNotifications(
                repository.fetchNotificationsForApp(it).map { it.asNoteSieveModel() },
                queries.groupedList
            )
        } ?: emptyList()

        GroupedScreenUiState(
            appInfos = filteredApps,
            clickedAppNotifications = selectedAppNotifications,
            gridSearchQuery = queries.groupedGrid,
            listSearchQuery = queries.groupedList
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), GroupedScreenUiState())

    private fun filterNotifications(
        notifications: List<NoteSieveModel>,
        query: String
    ): List<NoteSieveModel> =
        if (query.isEmpty()) notifications else notifications.filter { it.matchesSearch(query) }

    private fun NoteSieveModel.matchesSearch(query: String): Boolean =
        notificationTitle.contains(query, ignoreCase = true) ||
                notificationContent.contains(query, ignoreCase = true) ||
                appName.contains(query, ignoreCase = true)

    private fun filterAppNames(
        appInfos: List<AppModel>,
        query: String
    ): List<AppModel> =
        if (query.isEmpty()) appInfos else appInfos.filter { it.matchesAppSearch(query) }

    private fun AppModel.matchesAppSearch(query: String): Boolean =
        appName.contains(query, ignoreCase = true) || packageName.contains(query, ignoreCase = true)

    fun handleStarClick(id: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
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
        /*viewModelScope.launch {
            showOptions.update { currentState ->
                val targetMap = when (screen) {
                    ToggleUpdation.ALL -> currentState.allHashMap
                    ToggleUpdation.CLICKED -> currentState.clickedHashMap
                    ToggleUpdation.STARRED -> currentState.starredHashMap
                }

                targetMap.putIfAbsent(id, false)
                targetMap[id] = !isVisible
                currentState
            }
        }*/
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

data class ShowOptionsHashMap(
    val allHashMap: MutableMap<Int, Boolean> = mutableMapOf(),
    val clickedHashMap: MutableMap<Int, Boolean> = mutableMapOf(),
    val starredHashMap: MutableMap<Int, Boolean> = mutableMapOf()
)

enum class ToggleUpdation {
    ALL, CLICKED, STARRED
}


sealed interface UiDataState<out T> {
    data object Loading : UiDataState<Nothing>

    data class Empty<T>(val uiState: T) : UiDataState<T>

    data class Success<T>(val uiState: T) : UiDataState<T>
}

