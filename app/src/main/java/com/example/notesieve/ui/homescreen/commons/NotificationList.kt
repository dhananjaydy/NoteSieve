package com.example.notesieve.ui.homescreen.commons

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notesieve.data.local.NoteSieveModel

@Composable
fun NotificationList(
    notifications: List<NoteSieveModel>,
    onStarClick: (Int, Boolean) -> Unit,
    onCopyClick: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onBodyClick: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 8.dp)
    ) {
        items(
            notifications,
            key = { notification ->
                notification.id
            }
        ) { notification ->
            NotificationItem(
                noteSieveModel = notification,
                onStarClick = { id, isFavorite ->
                    onStarClick(id, isFavorite)
                },
                onCopyClick = { onCopyClick(it) },
                onShareClick = { onShareClick(it) },
                onDeleteClick = { onDeleteClick(it) },
                onBodyClick = { id, showOptions ->
                    onBodyClick(id, showOptions)
                }
            )
        }
    }
}