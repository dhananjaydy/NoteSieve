package com.example.notesieve.ui.deletescreen

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.example.notesieve.DeleteScreenUiState
import com.example.notesieve.R
import com.example.notesieve.ui.NoteSieveTabs
import com.example.notesieve.ui.homescreen.commons.AppIcon
import com.example.notesieve.ui.homescreen.commons.FullScreenLoader
import com.example.notesieve.ui.homescreen.commons.SearchBar
import com.example.notesieve.ui.homescreen.viewmodel.UiDataState

fun NavGraphBuilder.deleteScreen() {
    composable(NoteSieveTabs.Delete.name) {
        DeleteScreen()
    }
}


@Composable
fun DeleteScreen(
    viewModel: DeleteViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    when (uiState) {
        is UiDataState.Success -> {

            val successState: DeleteScreenUiState =
                (uiState as UiDataState.Success<DeleteScreenUiState>).uiState
            Column(modifier = Modifier.padding(16.dp)) {

                var expandedDropdown by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedDropdown = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(successState.selectedTimeFrame)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = ""
                        )
                    }
                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "Last 15 minutes",
                            "Last hour",
                            "Last 24 hours",
                            "Last 7 days",
                            "Last 30 days",
                            "All time"
                        ).forEach { timeFrame ->
                            DropdownMenuItem(
                                text = { Text(timeFrame) },
                                onClick = {
                                    viewModel.onTimeFrameSelected(timeFrame)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SearchBar(
                    hint = stringResource(R.string.search_installed_apps),
                    onSearchTextChanged = { viewModel.onSearchQueryChanged(it) },
                    initialValue = successState.searchQuery
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text("Select Apps", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Checkbox(
                        checked = successState.selectAll,
                        onCheckedChange = { viewModel.onSelectAllChanged(it) }
                    )
                    Text(
                        text = stringResource(R.string.select_all),
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
                HorizontalDivider()

                LazyColumn(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {

                    items(successState.apps) { appModel ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        ) {
                            Checkbox(
                                checked = successState.selectedApps[appModel.packageName] ?: false,
                                onCheckedChange = {
                                    viewModel.onAppSelectionChanged(
                                        appModel.packageName,
                                        it
                                    )
                                }
                            )
                            AppIcon(
                                appModel.packageName,
                                size = 28.dp,
                                modifier = Modifier.padding(10.dp)
                            )
                            Text(text = appModel.appName)
                        }
                    }
                }


                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {

                        var foundValidCase = false
                        for (app in successState.selectedApps.keys) {
                            if (successState.selectedApps[app] == true) {
                                foundValidCase = true
                                break
                            }
                        }
                        if (foundValidCase) {
                            viewModel.onDeleteClicked(deleteCount = { count ->
                                Toast.makeText(
                                    context,
                                    "$count notifications deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.no_app_selected_for_deletion),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Delete")
                }
            }
        }

        is UiDataState.Empty -> {

            val emptyState: DeleteScreenUiState =
                (uiState as UiDataState.Empty<DeleteScreenUiState>).uiState
            Column(modifier = Modifier.padding(16.dp)) {

                var expandedDropdown by remember { mutableStateOf(false) }
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedButton(
                        onClick = { expandedDropdown = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(emptyState.selectedTimeFrame)
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Dropdown Arrow"
                        )
                    }
                    DropdownMenu(
                        expanded = expandedDropdown,
                        onDismissRequest = { expandedDropdown = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf(
                            "Last 15 minutes",
                            "Last hour",
                            "Last 24 hours",
                            "Last 7 days",
                            "Last 30 days",
                            "All time"
                        ).forEach { timeFrame ->
                            DropdownMenuItem(
                                text = { Text(timeFrame) },
                                onClick = {
                                    viewModel.onTimeFrameSelected(timeFrame)
                                    expandedDropdown = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                SearchBar(
                    hint = stringResource(R.string.search_installed_apps),
                    onSearchTextChanged = { viewModel.onSearchQueryChanged(it) },
                    initialValue = emptyState.searchQuery
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = stringResource(R.string.no_app_available_with_given_search_query),
                    modifier = Modifier
                        .fillMaxSize()
                        .wrapContentSize(Alignment.Center)
                )


                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {

                        var foundValidCase = false
                        for (app in emptyState.selectedApps.keys) {
                            if (emptyState.selectedApps[app] == true) {
                                foundValidCase = true
                                break
                            }
                        }
                        if (foundValidCase) {
                            viewModel.onDeleteClicked(deleteCount = { count ->
                                Toast.makeText(
                                    context,
                                    "$count notifications deleted",
                                    Toast.LENGTH_SHORT
                                ).show()
                            })
                        } else {
                            Toast.makeText(
                                context,
                                context.getString(R.string.no_app_selected_for_deletion),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    },
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text(text = stringResource(R.string.delete))
                }
            }
        }

        UiDataState.Loading -> {
            FullScreenLoader()
        }
    }


}

@Preview
@Composable
private fun DeleteScreenPreview() {
    DeleteScreen()
}