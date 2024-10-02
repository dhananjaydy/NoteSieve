package com.example.notesieve

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

