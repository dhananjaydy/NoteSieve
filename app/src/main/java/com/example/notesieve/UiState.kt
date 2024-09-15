package com.example.notesieve

import androidx.compose.runtime.Stable
import com.example.notesieve.data.local.AppModel
import com.example.notesieve.data.local.NoteSieveModel

@Stable
data class UiState(
    val notifications: List<NoteSieveModel> = emptyList(),
    val searchQuery: String = "",
    val notificationsHashMap: Map<Int, NoteSieveModel> = mapOf()
)

@Stable
data class GroupedScreenUiState(
    val notifications: List<NoteSieveModel> = emptyList(),
    val appInfos: List<AppModel> = emptyList(),
    val gridSearchQuery: String = "",
    val listSearchQuery: String = "",
    val clickedAppNotifications: List<NoteSieveModel> = emptyList()
)

