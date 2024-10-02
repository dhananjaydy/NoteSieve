package com.example.notesieve.ui.homescreen.subscreens.grouped


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.notesieve.R
import com.example.notesieve.data.local.AppModel
import com.example.notesieve.ui.homescreen.commons.AppIcon

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
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxSize()
                .heightIn(min = 160.dp)
        ) {
            AppIcon(packageName = appModel.packageName)

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = appModel.appName,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                overflow = TextOverflow.Clip
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = stringResource(R.string.notifications, appModel.notificationCount),
                textAlign = TextAlign.Center,
                letterSpacing = 0.04.sp,
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2,
                overflow = TextOverflow.Clip
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