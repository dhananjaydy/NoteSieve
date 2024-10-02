package com.example.notesieve.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.DrawerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.notesieve.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun NoteSieveDrawer(
    currentScreen: NoteSieveTabs,
    drawerState: DrawerState,
    navController: NavHostController,
    items: List<NoteSieveTabs>,
) {

    val scope = rememberCoroutineScope()

    ModalDrawerSheet(drawerState = drawerState, modifier = Modifier.width(IntrinsicSize.Min)) {

        DrawerHeader()

        Spacer(modifier = Modifier.height(16.dp))

        items.forEachIndexed { index, tab ->

            NavigationDrawerItem(
                label = { Text(stringResource(tab.title)) },
                selected = currentScreen.route == tab.route,
                onClick = {
                    scope.launch {
                        delay(300)
                        drawerState.close()
                    }
                    navController.navigate(tab.route) {
                        navController.graph.startDestinationRoute?.let { route ->
                            popUpTo(route) {
                                saveState = true
                            }
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = {
                    Icon(
                        imageVector = if (currentScreen.route == tab.route) {
                            tab.filledIcon
                        } else {
                            tab.outlinedIcon
                        },
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth().padding(NavigationDrawerItemDefaults.ItemPadding)
            )
            if (index < items.lastIndex) {
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun DrawerHeader(modifier: Modifier = Modifier) {
    Column(
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer)
            .padding(16.dp)
            .fillMaxWidth()
    ) {

        Image(
            painterResource(id = R.drawable.app_icon),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = modifier
                .size(70.dp)

        )
        Spacer(modifier = Modifier.padding(8.dp))
        Text(
            text = stringResource(id = R.string.app_name),
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}