package com.example.notesieve.ui.homescreen.commons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notesieve.data.local.NoteSieveModel

@Composable
fun NotificationsSuccessScreen(
    notifications: List<NoteSieveModel>,
    searchQuery: String,
    onStarClick: (Int, Boolean) -> Unit,
    onSearch: (String) -> Unit,
    onShareClick: (String) -> Unit,
    onCopyClick: (String) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onBodyClick: (Int, Boolean) -> Unit,
    hint: String,
    errorMessage: String,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        SearchBar(
            hint = hint,
            onSearchTextChanged = { onSearch(it) },
            initialValue = searchQuery
        )
        if (notifications.isEmpty()) {
            Text(
                text = errorMessage,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            NotificationList(
                notifications = notifications,
                onStarClick = onStarClick,
                onCopyClick = onCopyClick,
                onShareClick = onShareClick,
                onDeleteClick = onDeleteClick,
                onBodyClick = onBodyClick
            )
        }
    }
}