package com.example.notesieve.ui

import androidx.annotation.StringRes
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Feedback
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Feedback
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.notesieve.R
import com.example.notesieve.ui.deletescreen.deleteScreen
import com.example.notesieve.ui.feedbackscreen.feedbackScreen
import com.example.notesieve.ui.homescreen.subscreens.homeScreen
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteSieveScreen(
    navController: NavHostController = rememberNavController()
) {

    val items = remember {
        listOf(
            NoteSieveTabs.Home,
            NoteSieveTabs.Delete,
            NoteSieveTabs.Feedback
        )
    }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentScreen = NoteSieveTabs.valueOf(
        navBackStackEntry?.destination?.route ?: NoteSieveTabs.Home.name
    )

    ModalNavigationDrawer(

        drawerState = drawerState,
        drawerContent = {
            NoteSieveDrawer(
                currentScreen,
                drawerState,
                navController,
                items
            )
        },
        gesturesEnabled = true
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(id = currentScreen.title)
                        )
                    },
                    navigationIcon = {
                        IconButton(
                            onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menu"
                            )
                        }
                    }
                )
            },
            content = { innerPadding ->
                NavHost(
                    navController = navController,
                    startDestination = NoteSieveTabs.Home.name,
                    modifier = Modifier.padding(
                        innerPadding
                    )
                ) {
                    homeScreen()
                    deleteScreen()
                    feedbackScreen()
                }
            }
        )
    }
}

enum class NoteSieveTabs(
    val route: String,
    @StringRes val title: Int,
    val filledIcon: ImageVector,
    val outlinedIcon: ImageVector
) {
    Home(
        "home",
        R.string.home,
        Icons.Filled.Home,
        Icons.Outlined.Home
    ),
    Delete(
        "delete",
        R.string.delete,
        Icons.Filled.Delete,
        Icons.Outlined.Delete
    ),
    Feedback(
        "feedback",
        R.string.feedback,
        Icons.Filled.Feedback,
        Icons.Outlined.Feedback
    )
}
