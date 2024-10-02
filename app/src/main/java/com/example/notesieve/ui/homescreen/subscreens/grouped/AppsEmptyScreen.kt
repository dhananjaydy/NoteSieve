package com.example.notesieve.ui.homescreen.subscreens.grouped

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notesieve.R
import com.example.notesieve.ui.homescreen.commons.SearchBar

@Composable
fun AppsEmptyScreen(
    query: String,
    hint: String,
    emptyQueryErrorMessage: String,
    onSearch: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(
                horizontal = 12.dp,
                vertical = 8.dp
            )
    ) {

        SearchBar(
            hint = hint,
            onSearchTextChanged = onSearch,
            initialValue = query
        )
        if (query.isEmpty()) {
            Text(
                text = emptyQueryErrorMessage,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        } else {
            Text(
                text = stringResource(
                    id = R.string.no_apps_available_with_the_given_search_query,
                    query
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        }
    }
}