package com.example.notesieve.ui

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.notesieve.ui.permissionscreen.PermissionCheckerScreen
import com.example.notesieve.ui.permissionscreen.PermissionViewModel

@Composable
fun NoteSieveApp(viewModel: PermissionViewModel = hiltViewModel()) {

    val isPermissionGranted by viewModel.permissionState.collectAsStateWithLifecycle()
    val permissionState by viewModel.currentStep.collectAsStateWithLifecycle()

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
