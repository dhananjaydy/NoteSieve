package com.example.notesieve.ui.homescreen.subscreens.grouped

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.notesieve.data.local.AppModel
import com.example.notesieve.ui.homescreen.commons.SearchBar

@Composable
fun AppsSuccessScreen(
    appModels: List<AppModel>,
    searchQuery: String,
    onSearch: (String) -> Unit,
    onAppSelected: (String) -> Unit,
    hint: String,
    errorMessage: String,
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
            onSearchTextChanged = { onSearch(it) },
            initialValue = searchQuery
        )

        if (appModels.isEmpty()) {
            Text(
                text = errorMessage,
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
            )
        } else {
            Spacer(modifier = Modifier.height(8.dp))
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(
                    items = appModels,
                    key = { appModel ->
                        appModel.key
                    }
                ) { appModel ->
                    GroupedByGridItem(
                        appModel = appModel,
                        onClick = { onAppSelected(appModel.packageName) }
                    )
                }
            }
        }
    }

}