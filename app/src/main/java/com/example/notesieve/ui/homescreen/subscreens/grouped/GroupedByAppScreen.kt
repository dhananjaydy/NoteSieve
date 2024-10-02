package com.example.notesieve.ui.homescreen.subscreens.grouped

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notesieve.R
import com.example.notesieve.UiGridState
import com.example.notesieve.UiListState
import com.example.notesieve.ui.homescreen.commons.FullScreenLoader
import com.example.notesieve.ui.homescreen.commons.NotificationsEmptyScreen
import com.example.notesieve.ui.homescreen.commons.NotificationsSuccessScreen
import com.example.notesieve.ui.homescreen.viewmodel.UiDataState
import com.example.notesieve.utils.getAppName

@Composable
fun GroupedByAppsScreen(
    gridScreenState: UiDataState<UiGridState>,
    listScreenState: UiDataState<UiListState>,
    onGridSearch: (String) -> Unit,
    onListSearch: (String) -> Unit,
    onAppSelected: (String) -> Unit,
    onStarClick: (Int, Boolean) -> Unit,
    onShareClick: (String) -> Unit,
    onCopyClick: (String) -> Unit,
    onDeleteClick: (Int) -> Unit,
    resetQuery: () -> Unit,
    onBodyClick: (Int, Boolean) -> Unit
) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = GroupedScreen.AppGrid.route) {

        composable(GroupedScreen.AppGrid.route) { it ->
            GroupedByGrid(
                screenState = gridScreenState,
                onGridSearch = { onGridSearch(it) },
                onAppSelected = { packageName ->
                    navController.navigate(GroupedScreen.Notifications.createRoute(packageName))
                    onAppSelected(packageName)
                }
            )
        }
        composable(
            GroupedScreen.Notifications.route,
            arguments = listOf(navArgument(name = "packageName") { type = NavType.StringType})
        ) { navBackStackEntry ->
            val packageName = navBackStackEntry.arguments?.getString("packageName") ?: stringResource(
                R.string.unknown
            )

            ClickedAppScreen(
                screenState = listScreenState,
                packageName = packageName,
                onListSearch = onListSearch,
                onStarClick = onStarClick,
                onShareClick = onShareClick,
                onDeleteClick = onDeleteClick,
                onCopyClick = onCopyClick,
                onBodyClick = onBodyClick,
                resetQuery = {
                    navController.navigateUp()
                    resetQuery()
                }
            )
        }
    }
}

@Composable
fun ClickedAppScreen(
    screenState: UiDataState<UiListState>,
    packageName: String,
    onListSearch: (String) -> Unit,
    onStarClick: (Int, Boolean) -> Unit,
    onShareClick: (String) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onCopyClick: (String) -> Unit,
    resetQuery: () -> Unit,
    onBodyClick: (Int, Boolean) -> Unit
) {

    val context = LocalContext.current

    BackHandler {
        resetQuery()
    }

    when (screenState) {
        is UiDataState.Empty -> {
            val query = screenState.uiState.searchQuery

            NotificationsEmptyScreen(
                query = query,
                onSearch = onListSearch,
                hint = stringResource(id = R.string.search_all_notifications),
                errorMessage = stringResource(id = R.string.no_notifications_available_yet)
            )
        }

        UiDataState.Loading ->  {
            FullScreenLoader()
        }
        is UiDataState.Success -> {

            val notifications = screenState.uiState.notifications
            val searchQuery = screenState.uiState.searchQuery

            NotificationsSuccessScreen(
                notifications = notifications,
                searchQuery = searchQuery,
                onStarClick = onStarClick,
                onSearch = onListSearch,
                onShareClick = onShareClick,
                onCopyClick = onCopyClick,
                onDeleteClick = onDeleteClick,
                onBodyClick = onBodyClick,
                hint = stringResource(
                    id = R.string.search_notifications_in_selected_app,
                    packageName.getAppName(context = context)
                ),
                errorMessage = stringResource(id = R.string.no_notifications_available_yet)
            )
        }
    }
}
@Composable
fun GroupedByGrid(
    screenState: UiDataState<UiGridState>,
    onGridSearch: (String) -> Unit,
    onAppSelected: (String) -> Unit,
) {

    when (screenState) {
        is UiDataState.Empty -> {
            val query = screenState.uiState.searchQuery
            
            AppsEmptyScreen(
                query = query,
                hint = stringResource(id = R.string.search_installed_apps),
                emptyQueryErrorMessage = stringResource(id = R.string.no_notifications_available_yet),
                onSearch = { onGridSearch(it) }
            )
        }

        UiDataState.Loading -> {
            FullScreenLoader()
        }
        is UiDataState.Success -> {
            val appModels = screenState.uiState.appModels
            val searchQuery = screenState.uiState.searchQuery

            AppsSuccessScreen(
                appModels = appModels,
                searchQuery = searchQuery,
                onSearch = { onGridSearch(it) },
                onAppSelected = { onAppSelected(it) },
                hint = stringResource(id = R.string.search_installed_apps),
                errorMessage = stringResource(id = R.string.no_apps_available_with_the_given_search_query)
            )
        }
    }
}