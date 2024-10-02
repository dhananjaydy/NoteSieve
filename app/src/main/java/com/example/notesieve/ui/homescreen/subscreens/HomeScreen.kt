package com.example.notesieve.ui.homescreen.subscreens

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Grading
import androidx.compose.material.icons.filled.GridView
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.notesieve.R
import com.example.notesieve.ui.NoteSieveTabs
import com.example.notesieve.ui.deletescreen.deleteScreen
import com.example.notesieve.ui.feedbackscreen.feedbackScreen
import com.example.notesieve.ui.gridFilledIcon
import com.example.notesieve.ui.gridOutlinedIcon
import com.example.notesieve.ui.homescreen.subscreens.all.AllScreen
import com.example.notesieve.ui.homescreen.subscreens.grouped.GroupedByAppsScreen
import com.example.notesieve.ui.homescreen.subscreens.starred.StarredScreen
import com.example.notesieve.ui.homescreen.viewmodel.HomeViewModel
import com.example.notesieve.ui.homescreen.viewmodel.Screen
import com.example.notesieve.ui.homescreen.viewmodel.ToggleUpdation
import com.example.notesieve.ui.listFilledIcon
import com.example.notesieve.ui.listOutlinedIcon
import com.example.notesieve.utils.clipToClipboard
import com.example.notesieve.utils.openChooser

fun NavGraphBuilder.homeScreen() {
    composable(NoteSieveTabs.Home.name) {
        HomeScreen()
    }
}

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    val bottomNavigationItems = remember {
        listOf(
            HomeScreenTabs.All,
            HomeScreenTabs.Grouped,
            HomeScreenTabs.Starred
        )
    }

    val context = LocalContext.current

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            Surface(
                color = Color.White,
                tonalElevation = 3.dp
            ) {
                HomeNavigation(
                    navController = navController,
                    items = bottomNavigationItems
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = HomeScreenTabs.All.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())

        ) {
            composable(route = HomeScreenTabs.All.route) {
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
                    onBodyClick = { id, showOptions ->
                        viewModel.updateOptionsVisibility(ToggleUpdation.ALL, showOptions, id)
                    }
                )
            }
            composable(route = HomeScreenTabs.Grouped.route) {
                val gridState by viewModel.gridAppsState.collectAsStateWithLifecycle()
                val listState by viewModel.clickedAppState.collectAsStateWithLifecycle()
                GroupedByAppsScreen(
                    gridScreenState = gridState,
                    listScreenState = listState,
                    onGridSearch = { viewModel.updateSearchQuery(Screen.GROUPED_GRID, it) },
                    onListSearch = { viewModel.updateSearchQuery(Screen.GROUPED_LIST, it) },
                    onAppSelected = viewModel::setSelectedAppModel,
                    onStarClick = { id, isFavorite ->
                        viewModel.handleStarClick(
                            id,
                            isFavorite
                        )
                    },
                    onShareClick = { openChooser(context, it) },
                    onCopyClick = { clipToClipboard(context, it) },
                    onDeleteClick = viewModel::deleteNotification,
                    onBodyClick = { showOptions, id ->
                        viewModel.updateOptionsVisibility(
                            ToggleUpdation.CLICKED,
                            id,
                            showOptions
                        )
                    },
                    resetQuery = {
                        viewModel.updateSearchQuery(Screen.GROUPED_LIST, "") }
                )
            }
            composable(route = HomeScreenTabs.Starred.route) {
                val screenState2 by viewModel.starredNotificationsState.collectAsStateWithLifecycle()
                StarredScreen(
                    screenState = screenState2,
                    onStarClick = { id, isStarred ->
                        viewModel.handleStarClick(id, isStarred)
                    },
                    onSearch = { viewModel.updateSearchQuery(Screen.STARRED, it) },
                    onDeleteClick = viewModel::deleteNotification,
                    onCopyClick = { notification ->
                        clipToClipboard(context, notification)
                    },
                    onShareClick = { notification ->
                        openChooser(context, notification)
                    },
                    onBodyClick = { id, showOptions ->
                        viewModel.updateOptionsVisibility(
                            ToggleUpdation.STARRED,
                            showOptions,
                            id
                        )
                    }
                )
            }
            deleteScreen()
            feedbackScreen()
        }
    }
}


@Composable
fun HomeNavigation(
    navController: NavHostController,
    items: List<HomeScreenTabs>,
    modifier: Modifier = Modifier
) {

    val windowInsets = WindowInsets.navigationBars

    NavigationBar(
        modifier = modifier
            .windowInsetsPadding(windowInsets.only(WindowInsetsSides.Horizontal))
            .fillMaxWidth(),
        tonalElevation = 0.dp,
        windowInsets = WindowInsets(0, 0, 0, 0)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentDestination = navBackStackEntry?.destination

        items.forEach { screen ->
            val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
            NavigationBarItem(
                selected = selected,
                icon = {
                    Icon(
                        imageVector = if (selected) screen.filledIcon else screen.outlinedIcon,
                        contentDescription = stringResource(screen.resourceId)
                    )
                },
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
                },
            )
        }
    }
}

enum class HomeScreenTabs(
    val route: String,
    @StringRes val resourceId: Int,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
) {
    All(
        "all",
        R.string.all,
        filledIcon = listFilledIcon,
        outlinedIcon = listOutlinedIcon
    ),
    Grouped(
        "grouped",
        R.string.grouped,
        filledIcon = gridFilledIcon,
        outlinedIcon = gridOutlinedIcon
    ),
    Starred(
        "starred",
        R.string.starred,
        filledIcon = Icons.Default.Star,
        outlinedIcon = Icons.Outlined.StarOutline
    )
}

@Preview
@Composable
private fun StarPreview() {
    Row {
        Icon(imageVector = Icons.Default.GridView, contentDescription = null)
        Icon(imageVector = Icons.AutoMirrored.Default.Grading, contentDescription = null)
    }
}