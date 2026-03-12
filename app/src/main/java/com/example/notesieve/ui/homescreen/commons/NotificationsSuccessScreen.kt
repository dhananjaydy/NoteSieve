package com.example.notesieve.ui.homescreen.commons

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notesieve.data.local.NoteSieveModel
import kotlinx.coroutines.launch

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
    onUrlClick: (String) -> Unit,
    hint: String,
    errorMessage: String,
    modifier: Modifier = Modifier
) {

    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    val showScrollTop by remember {
        derivedStateOf {
            listState.firstVisibleItemIndex > 5
        }
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {

        Column(
            modifier = Modifier
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
                    listState = listState,
                    onStarClick = onStarClick,
                    onCopyClick = onCopyClick,
                    onShareClick = onShareClick,
                    onDeleteClick = onDeleteClick,
                    onBodyClick = onBodyClick,
                    onUrlClick = onUrlClick
                )
            }
        }

        AnimatedVisibility(
            visible = showScrollTop,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        ) {

            FloatingActionButton(
                onClick = {
                    scope.launch {
                        listState.animateScrollToItem(0)
                    }
                }
            ) {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Scroll to top"
                )
            }
        }
    }
}