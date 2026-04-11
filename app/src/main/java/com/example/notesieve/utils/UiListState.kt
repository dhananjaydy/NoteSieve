package com.example.notesieve.utils

import androidx.compose.runtime.Stable
import com.example.notesieve.data.local.AppModel
import com.example.notesieve.data.local.NoteSieveModel

@Stable
data class UiListState(
    val notifications: List<NoteSieveModel> = emptyList(),
    val searchQuery: String = ""
)

@Stable
data class UiGridState(
    val appModels: List<AppModel> = emptyList(),
    val searchQuery: String = ""
)

@Stable
data class DeleteScreenUiState(
    val apps: List<AppModel> = emptyList(),
    val selectedApps: Map<String, Boolean> = emptyMap(),
    val selectAll: Boolean = false,
    val selectedTimeFrame: String = "Last 7 days",
    val searchQuery: String = ""
)

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