package com.example.notesieve.ui.permissionscreen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notesieve.PermissionChecker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    private val permissionChecker: PermissionChecker
) : ViewModel() {

    private val _permissionState = MutableStateFlow(false)
    val permissionState: StateFlow<Boolean> = _permissionState.asStateFlow()

    private val _currentStep = MutableStateFlow<PermissionFlowStep>(PermissionFlowStep.Introduction)
    val currentStep: StateFlow<PermissionFlowStep> = _currentStep.asStateFlow()

    init {
        checkPermissionState()
    }

    fun checkPermissionState() {
        viewModelScope.launch {
            _permissionState.value = permissionChecker.isNotificationListenerEnabled()
            if (_permissionState.value && _currentStep.value == PermissionFlowStep.Explanation) {
                _currentStep.value = PermissionFlowStep.Completed
            }
        }
    }

    fun moveToExplanation() {
        _currentStep.value = PermissionFlowStep.Explanation
    }

    fun updatePermissionState() {
        _permissionState.value = !_permissionState.value
    }

}
sealed interface PermissionFlowStep {
    data object Introduction : PermissionFlowStep
    data object Explanation : PermissionFlowStep
    data object Completed : PermissionFlowStep
}