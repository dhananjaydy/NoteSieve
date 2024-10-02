package com.example.notesieve.ui.feedbackscreen

import android.content.ActivityNotFoundException
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FeedbackViewModel @Inject constructor() : ViewModel() {

    private val _uriOpenStatus: MutableStateFlow<UriOpenStatus> = MutableStateFlow(UriOpenStatus.Idle)
    val uriOpenStatus: StateFlow<UriOpenStatus> = _uriOpenStatus.asStateFlow().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        UriOpenStatus.Idle
    )

    fun openUri(openUri: (Uri) -> Unit, uri: String) {
        viewModelScope.launch {
            try {
                openUri(Uri.parse(uri))
                _uriOpenStatus.value = UriOpenStatus.Success
            } catch (e: ActivityNotFoundException) {
                _uriOpenStatus.value = UriOpenStatus.Error("No app found to handle teh exception")
            }
        }
    }

    fun resetStatus() {
        _uriOpenStatus.value = UriOpenStatus.Idle
    }

}

sealed interface UriOpenStatus {
    data object Idle : UriOpenStatus
    data object Success : UriOpenStatus
    data class Error(val message: String) : UriOpenStatus
}