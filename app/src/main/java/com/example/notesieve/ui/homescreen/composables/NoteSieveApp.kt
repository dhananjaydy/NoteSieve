package com.example.notesieve.ui.homescreen.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.notesieve.ui.homescreen.composables.subscreens.NoteSieveScreen
import com.example.notesieve.ui.permissionscreen.PermissionCheckerScreen
import com.example.notesieve.ui.permissionscreen.PermissionViewModel

@Composable
fun NoteSieveApp(viewModel: PermissionViewModel = hiltViewModel()) {

    val isPermissionGranted by viewModel.permissionState.collectAsState()
    val permissionState by viewModel.currentStep.collectAsState()

    if (isPermissionGranted) {
        NoteSieveScreen()
    } else {
        PermissionCheckerScreen(
            permissionState = permissionState,
            moveToExplanation = { viewModel.moveToExplanation() },
            checkPermissionState = { viewModel.checkPermissionState() },
            permissionGranted = { viewModel.updatePermissionState() }
        )
    }

}
