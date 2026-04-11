package com.example.notesieve.ui.homescreen.commons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import com.example.notesieve.data.local.NoteSieveModel

@Composable
fun NotificationList(
    notifications: LazyPagingItems<NoteSieveModel>,
    listState: LazyListState,
    onStarClick: (Int, Boolean) -> Unit,
    onCopyClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onBodyClick: (Int, Boolean) -> Unit,
    onUrlClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        state = listState,
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(
            count = notifications.itemCount,
            key = notifications.itemKey { it.id }
        ) { index ->
            val notification = notifications[index] ?: return@items
            NotificationItem(
                noteSieveModel = notification,
                onStarClick = { id, isFavorite -> onStarClick(id, isFavorite) },
                onCopyClick = { onCopyClick(it) },
                onShareClick = { onShareClick(it) },
                onDeleteClick = { onDeleteClick(it) },
                onBodyClick = { id, showOptions -> onBodyClick(id, showOptions) },
                onUrlClick = { onUrlClick(it) }
            )
        }
    }
}