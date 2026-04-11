package com.example.notesieve.ui.homescreen.subscreens.all

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import com.example.notesieve.R
import com.example.notesieve.data.local.NoteSieveModel
import com.example.notesieve.ui.homescreen.commons.FullScreenLoader
import com.example.notesieve.ui.homescreen.commons.NotificationsEmptyScreen
import com.example.notesieve.ui.homescreen.commons.NotificationsSuccessScreen

@Composable
fun AllScreen(
    screenState: LazyPagingItems<NoteSieveModel>,
    searchQuery: String,
    onStarClick: (Int, Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onCopyClick: (String) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onUrlClick: (String) -> Unit,
    onBodyClick: (Int, Boolean) -> Unit
) {

    when {

        screenState.loadState.refresh is LoadState.Loading && screenState.itemCount == 0 -> {
            FullScreenLoader()
        }

        screenState.loadState.refresh is LoadState.Error && screenState.itemCount == 0 -> {
            NotificationsEmptyScreen(
                query = searchQuery,
                onSearch = onSearch,
                hint = stringResource(id = R.string.search_all_notifications),
                errorMessage = stringResource(id = R.string.no_notifications_available_yet)
            )
        }

        screenState.itemCount == 0 -> {
            NotificationsEmptyScreen(
                query = searchQuery,
                onSearch = onSearch,
                hint = stringResource(id = R.string.search_all_notifications),
                errorMessage = stringResource(id = R.string.no_notifications_available_yet)
            )
        }

        else -> {
            NotificationsSuccessScreen(
                notifications = screenState,
                searchQuery = searchQuery,
                onStarClick = onStarClick,
                onSearch = onSearch,
                onShareClick = onShareClick,
                onCopyClick = onCopyClick,
                onDeleteClick = onDeleteClick,
                onBodyClick = onBodyClick,
                onUrlClick = onUrlClick,
                hint = stringResource(id = R.string.search_all_notifications),
                errorMessage = stringResource(id = R.string.no_notifications_available_yet)
            )
        }
    }
}