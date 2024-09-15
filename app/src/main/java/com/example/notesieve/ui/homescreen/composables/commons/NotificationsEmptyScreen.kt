package com.example.notesieve.ui.homescreen.composables.commons

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notesieve.R

@Composable
fun NotificationsEmptyScreen(
    query: String,
    hint: String,
    errorMessage: String,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    val keyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {

        SearchBar(
            hint = hint,
            onSearchTextChanged = onSearch,
            initialValue = query,
            modifier = Modifier.focusRequester(focusRequester)
        )

        if (query.isEmpty()) {
            Text(
                text = errorMessage,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        } else {
            Text(
                text = stringResource(
                    R.string.no_notifications_available_with_the_given_search_query,
                    query
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }
    }
}
