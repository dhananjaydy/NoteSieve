package com.example.notesieve.ui.deletescreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesieve.DeleteScreenUiState
import com.example.notesieve.data.local.AppModel
import com.example.notesieve.data.repository.NoteSieveRepository
import com.example.notesieve.ui.homescreen.viewmodel.UiDataState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

@HiltViewModel
class DeleteViewModel @Inject constructor(
    private val repository: NoteSieveRepository,
    @Named("DefaultDispatcher") private val defaultDispatcher: CoroutineDispatcher,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
    @Named("MainDispatcher") private val mainDispatcher: CoroutineDispatcher
) : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    private val _selectedApps = MutableStateFlow<Map<String, Boolean>>(emptyMap())
    private val _selectAll = MutableStateFlow(false)
    private val _selectedTimeFrame = MutableStateFlow("Last 7 days")

    private val appModelListFlow = repository.getAppModels().flowOn(defaultDispatcher)
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    init {

        viewModelScope.launch(ioDispatcher) {
            appModelListFlow.collect { appModels ->
                _selectedApps.update { currentMap ->
                    appModels.associate { appModel ->
                        appModel.packageName to (currentMap[appModel.packageName] ?: false)
                    }
                }
            }
        }
    }

    private fun AppModel.matchesAppSearch(query: String): Boolean =
        appName.contains(query, ignoreCase = true)

    private suspend fun filterAppNames(
        appInfos: List<AppModel>,
        query: String
    ): List<AppModel> = withContext(defaultDispatcher) {
        if (query.isEmpty()) appInfos else appInfos.filter { it.matchesAppSearch(query) }
    }

    val uiState: StateFlow<UiDataState<DeleteScreenUiState>> = combine(
        appModelListFlow,
        _searchQuery,
        _selectAll,
        _selectedApps,
        _selectedTimeFrame
    ) { appModelList, searchQuery, selectAll, selectedApps, selectedTimeFrame ->

        val filteredAppModels = filterAppNames(appModelList, searchQuery)

        when {
            appModelList.isEmpty() -> UiDataState.Loading
            filteredAppModels.isEmpty() -> UiDataState.Empty(
                DeleteScreenUiState(
                    searchQuery = searchQuery,
                    selectedTimeFrame = selectedTimeFrame
                )
            )
            else -> UiDataState.Success(
                DeleteScreenUiState(
                    searchQuery = searchQuery,
                    apps = filteredAppModels,
                    selectedApps = selectedApps,
                    selectAll = selectAll,
                    selectedTimeFrame = selectedTimeFrame
                )
            )
        }

    }.flowOn(defaultDispatcher)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), UiDataState.Loading)


    fun onTimeFrameSelected(timeFrame: String) {
        _selectedTimeFrame.value = timeFrame
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onAppSelectionChanged(app: String, isSelected: Boolean) {
        viewModelScope.launch(defaultDispatcher) {
            _selectedApps.update { currentMap ->
                currentMap + (app to isSelected)
            }

            val size = _selectedApps.value.size
            var totalSelected = 0

            if (isSelected) {
                for (key in _selectedApps.value.keys) {
                    if (_selectedApps.value[key] == true) {
                        totalSelected++
                    }
                }

                if (totalSelected == size) {
                    _selectAll.update { true }
                }
            } else {
                if (_selectAll.value == true) {
                    _selectAll.update { false }
                }
            }
        }
    }

    fun onSelectAllChanged(isSelected: Boolean) {
        viewModelScope.launch(defaultDispatcher) {
            _selectAll.value = isSelected
            _selectedApps.update { currentMap ->
                currentMap.mapValues { isSelected }
            }
        }
    }

    fun onDeleteClicked(deleteCount: (Int) -> Unit) {
        viewModelScope.launch(ioDispatcher) {

            val selectedApps = _selectedApps.value.filter { it.value }.keys.toList()
            val startTime = getStartTimeForSelectedTimeFrame(_selectedTimeFrame.value)
            val countDeleted = repository.deleteNotificationsForApps(selectedApps, startTime)

            withContext(mainDispatcher) {
                deleteCount(countDeleted)
            }
        }
    }

    private fun getStartTimeForSelectedTimeFrame(timeFrame: String): Long {
        val currentTime = System.currentTimeMillis()
        return when (timeFrame) {
            "Last 15 minutes" -> currentTime - 15L * 60L * 1000L
            "Last hour" -> currentTime - 60L * 60L * 1000L
            "Last 24 hours" -> currentTime - 24L * 60L * 60L * 1000L
            "Last 7 days" -> currentTime - 7L * 24L * 60L * 60L * 1000L
            "Last 30 days" -> currentTime - 30L * 24L * 60L * 60L * 1000L
            "All time" -> 0L
            else -> currentTime - 7L * 24L * 60L * 60L * 1000L
        }
    }
}

