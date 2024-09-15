package com.example.notesieve.ui.permissionscreen

import android.content.Intent
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.notesieve.R


@Composable
fun PermissionCheckerScreen(
    permissionState: PermissionFlowStep,
    moveToExplanation: () -> Unit,
    checkPermissionState: () -> Unit,
    permissionGranted: () -> Unit,
    modifier: Modifier = Modifier
) {

    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
        checkPermissionState()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (permissionState) {
            PermissionFlowStep.Introduction -> {
                Text(stringResource(R.string.we_need_access_to_your_notifications_to_provide_better_service))
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { moveToExplanation() }) {
                    Text(text = stringResource(R.string.next))
                }
            }
            PermissionFlowStep.Explanation -> {
                Text(stringResource(R.string.you_ll_be_redirected_to_the_system_settings_please_enable_the_permission_for_our_app))
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        val intent = Intent(Settings.ACTION_NOTIFICATION_LISTENER_SETTINGS)
                        permissionLauncher.launch(intent)
                    }
                ) {
                    Text(text = stringResource(R.string.open_settings))
                }
            }
            PermissionFlowStep.Completed -> {
                permissionGranted()
            }
        }
    }
}