package com.example.notesieve.ui.homescreen.composables.commons

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay

@Composable
fun SearchBar(
    hint: String,
    onSearchTextChanged: (String) -> Unit,
    modifier: Modifier = Modifier,
    initialValue: String = ""
) {
    var searchText by remember { mutableStateOf(
        TextFieldValue(text = initialValue, selection = TextRange(initialValue.length))
    ) }

    val debouncedSearchText by remember {
        derivedStateOf {
            searchText.text.trim()
        }
    }

    LaunchedEffect(debouncedSearchText) {
        delay(500)
        onSearchTextChanged(debouncedSearchText)
    }

    TextField(
        value = searchText,
        onValueChange = { newValue ->
            searchText = newValue
        },
        modifier = modifier.fillMaxWidth(),
        placeholder = {
            Text(
                text = hint,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 6.dp),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        leadingIcon = {
            Icon(
                Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        trailingIcon = {
            if (searchText.text.isNotEmpty()) {
                IconButton(
                    onClick = {
                        searchText = TextFieldValue("")
                        onSearchTextChanged("")
                    }
                ) {
                    Icon(
                        Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        },
        singleLine = true
    )
}