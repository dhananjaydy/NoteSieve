package com.example.notesieve.ui.homescreen.composables.subscreens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.notesieve.R
import com.example.notesieve.UiState
import com.example.notesieve.ui.homescreen.composables.commons.FullScreenLoader
import com.example.notesieve.ui.homescreen.composables.commons.NotificationsEmptyScreen
import com.example.notesieve.ui.homescreen.composables.commons.NotificationsSuccessScreen
import com.example.notesieve.ui.homescreen.viewmodel.UiDataState

@Composable
fun AllScreen(
    screenState: UiDataState<UiState>,
    onStarClick: (Int, Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onCopyClick: (String) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onBodyClick: (Int, Boolean) -> Unit
) {

    when (screenState) {
        is UiDataState.Empty -> {
            val query = screenState.uiState.searchQuery

            NotificationsEmptyScreen(
                query = query,
                onSearch = onSearch,
                hint = stringResource(id = R.string.search_all_notifications),
                errorMessage = stringResource(id = R.string.no_notifications_available_yet)
            )
        }

        UiDataState.Loading -> {
            FullScreenLoader()
        }
        is UiDataState.Success -> {

            val notifications = screenState.uiState.notifications
            val searchQuery = screenState.uiState.searchQuery

            NotificationsSuccessScreen(
                notifications = notifications,
                searchQuery = searchQuery,
                onStarClick = onStarClick,
                onSearch = onSearch,
                onShareClick = onShareClick,
                onCopyClick = onCopyClick,
                onDeleteClick = onDeleteClick,
                onBodyClick = onBodyClick,
                hint = stringResource(id = R.string.search_all_notifications),
                errorMessage = stringResource(id = R.string.no_notifications_available_yet)
            )
        }
    }
}

