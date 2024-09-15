package com.example.notesieve.ui.homescreen.composables.subscreens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.notesieve.R
import com.example.notesieve.ui.homescreen.viewmodel.NoteSieveViewModel
import com.example.notesieve.ui.homescreen.viewmodel.Screen
import com.example.notesieve.ui.homescreen.viewmodel.ToggleUpdation
import com.example.notesieve.utils.clipToClipboard
import com.example.notesieve.utils.openChooser

@Composable
fun NoteSieveScreen(
    viewModel: NoteSieveViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {
    val bottomNavigationItems = listOf(
        NoteSieveScreen.All,
        NoteSieveScreen.Grouped,
        NoteSieveScreen.Starred
    )

    val context = LocalContext.current

    Scaffold(
        bottomBar = {
            NoteSieveNavigation(
                navController = navController,
                items = bottomNavigationItems
            )
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = NoteSieveScreen.All.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(route = NoteSieveScreen.All.route) {
                val screenState by viewModel.allNotificationsState.collectAsStateWithLifecycle()
                AllScreen(
                    screenState = screenState,
                    onStarClick = { id, isFavorite ->
                        viewModel.handleStarClick(id, isFavorite)
                    },
                    onSearch = { viewModel.updateSearchQuery(Screen.ALL, it) },
                    onDeleteClick = viewModel::deleteNotification,
                    onCopyClick = { notification ->
                        clipToClipboard(context, notification)
                    },
                    onShareClick = { notification ->
                        openChooser(context, notification)
                    },
                    onBodyClick = { showOptions, id ->
                        viewModel.updateOptionsVisibility(ToggleUpdation.ALL, id, showOptions)
                    }
                )
            }
            composable(route = NoteSieveScreen.Grouped.route) {
                val screenState by viewModel.groupedNotificationsState.collectAsStateWithLifecycle()
                GroupedByAppsScreen(
                    screenState = screenState,
                    onStarClick = { id, isFavorite ->
                        viewModel.handleStarClick(id, isFavorite)
                    },
                    onGridSearch = { viewModel.updateSearchQuery(Screen.GROUPED_GRID, it) },
                    onListSearch = { viewModel.updateSearchQuery(Screen.GROUPED_LIST, it) },
                    onAppSelected = { viewModel.setSelectedAppModel(it) },
                    onDeleteClick = { viewModel.deleteNotification(it) },
                    onCopyClick = { notification ->

                    },
                    onShareClick = { notification ->
                       openChooser(context, notification)
                    },
                    onBodyClick = { showOptions, id ->
                        viewModel.updateOptionsVisibility(ToggleUpdation.CLICKED, id, showOptions)
                    }
                )
            }
            composable(route = NoteSieveScreen.Starred.route) {
                val screenState2 by viewModel.starredNotificationsState2.collectAsStateWithLifecycle()
                StarredScreen(
                    screenState = screenState2,
                    onStarClick = { id, isStarred ->
                        viewModel.handleStarClick(id, isStarred)
                    },
                    onSearch = { viewModel.updateSearchQuery(Screen.STARRED, it) },
                    onDeleteClick = viewModel::deleteNotification,
                    onCopyClick = { notification ->

                    },
                    onShareClick = { notification ->
                        openChooser(context, notification)
                    },
                    onBodyClick = { showOptions, id ->
                        viewModel.updateOptionsVisibility(ToggleUpdation.ALL, id, showOptions)
                    }
                )
            }
        }
    }
}


@Composable
fun NoteSieveNavigation(
    navController: NavHostController,
    items: List<NoteSieveScreen>,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            NavigationBarItem(
                selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                icon = { Icon(imageVector = screen.icon, contentDescription = null) },
                alwaysShowLabel = true,
                label = { Text(text = stringResource(screen.resourceId)) },
                onClick = {
                    navController.navigate(screen.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }
    }
}

sealed class NoteSieveScreen(
    val route: String,
    @StringRes val resourceId: Int,
    val icon: ImageVector
) {
    data object All: NoteSieveScreen(
        "all",
        R.string.all,
        icon = Icons.Default.List
    )
    data object Grouped: NoteSieveScreen(
        "grouped",
        R.string.grouped,
        icon = Icons.Default.GridView
    )
    data object Starred: NoteSieveScreen(
        "starred",
        R.string.starred,
        icon = Icons.Default.Star
    )
}