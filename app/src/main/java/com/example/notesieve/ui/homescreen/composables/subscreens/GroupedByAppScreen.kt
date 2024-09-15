package com.example.notesieve.ui.homescreen.composables.subscreens


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.notesieve.GroupedScreenUiState
import com.example.notesieve.R
import com.example.notesieve.data.local.AppModel
import com.example.notesieve.ui.homescreen.composables.commons.AppIcon
import com.example.notesieve.ui.homescreen.composables.commons.SearchBar
import com.example.notesieve.ui.homescreen.composables.commons.NotificationList
import com.example.notesieve.utils.getAppName


@Composable
fun GroupedByAppsScreen(
    screenState: GroupedScreenUiState,
    onGridSearch: (String) -> Unit,
    onListSearch: (String) -> Unit,
    onAppSelected: (String) -> Unit,
    onStarClick: (Int, Boolean) -> Unit,
    onShareClick: (String) -> Unit,
    onCopyClick: (String) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onBodyClick: (Int, Boolean) -> Unit
) {

    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = GroupedScreen.AppGrid.route) {
        composable(GroupedScreen.AppGrid.route) { it ->
            GroupedByGrid(
                apps = screenState.appInfos,
                onAppSelected = { packageName ->
                    navController.navigate(GroupedScreen.Notifications.createRoute(packageName))
                    onAppSelected(packageName)
                },
                onGridSearch = { onGridSearch(it) },
                searchQuery = screenState.gridSearchQuery
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
                screenUiState = screenState,
                packageName = packageName,
                onSearch = { onListSearch(it) },
                onStarClick = { id, isStarred ->
                    onStarClick(id, isStarred)
                },
                onCopyClick = { onCopyClick(it) },
                onShareClick = { onShareClick(it) },
                onDeleteClick = { onDeleteClick(it) },
                onBodyClick = { id, showOptions ->
                    onBodyClick(id, showOptions)
                }
            )
        }
    }
}


@Composable
fun GroupedByGrid(
    apps: List<AppModel>,
    searchQuery: String,
    onGridSearch: (String) -> Unit,
    onAppSelected: (String) -> Unit,
    modifier: Modifier = Modifier
) {

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ){
        SearchBar(
            hint = stringResource(R.string.search_installed_apps),
            onSearchTextChanged = { onGridSearch(it) },
            initialValue = searchQuery
        )
        Spacer(modifier = Modifier.height(4.dp))
        LazyVerticalGrid(columns = GridCells.Fixed(count = 2)) {
            items(items = apps) { appModel ->
                GroupedByGridItem(
                    appModel = appModel,
                    onClick = { onAppSelected(appModel.packageName) }
                )
            }
        }
    }
}

@Composable
fun GroupedByGridItem(
    appModel: AppModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        modifier = modifier
            .padding(8.dp)
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            AppIcon(packageName = appModel.packageName)
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = appModel.appName,
                textAlign = TextAlign.Center,
                maxLines = 2
            )
            Spacer(modifier = Modifier.size(2.dp))
            Text(
                text = stringResource(R.string.notifications, appModel.notificationCount),
                textAlign = TextAlign.Center,
                letterSpacing = 0.04.sp
            )
        }
    }
}

@Composable
fun ClickedAppScreen(
    screenUiState: GroupedScreenUiState,
    packageName: String,
    onSearch: (String) -> Unit,
    onStarClick: (Int, Boolean) -> Unit,
    onShareClick: (String) -> Unit,
    onDeleteClick: (Int) -> Unit,
    onCopyClick: (String) -> Unit,
    onBodyClick: (Int, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {

    val notifications = screenUiState.clickedAppNotifications
    val searchQuery = screenUiState.listSearchQuery
    val context = LocalContext.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        SearchBar(
            hint = stringResource(
                id = R.string.search_notifications_in_selected_app,
                packageName.getAppName(context.packageManager)
            ),
            onSearchTextChanged = { onSearch(it) },
            initialValue = searchQuery
        )
        if (notifications.isEmpty()) {
            Text(
                text = stringResource(R.string.no_notifications_with_given_search_query_present_in_the_clicked_app),
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(Alignment.Center)
                    .align(Alignment.CenterHorizontally)
            )
        } else {
            NotificationList(
                notifications = notifications,
                onStarClick = { id, isFavorite ->
                    onStarClick(id, isFavorite)
                },
                onShareClick = { onShareClick(it) },
                onDeleteClick = { onDeleteClick(it) },
                onCopyClick = { onCopyClick(it) },
                onBodyClick = { id, showOptions ->
                    onBodyClick(id, showOptions)
                }
            )
        }
    }
}

sealed class GroupedScreen(val route: String) {
    data object AppGrid : GroupedScreen("app_grid")
    data object Notifications : GroupedScreen("notifications/{packageName}") {
        fun createRoute(packageName: String) = "notifications/$packageName"
    }
}