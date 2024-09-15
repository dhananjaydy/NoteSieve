package com.example.notesieve.data.local

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.ImageBitmap

@Stable
data class AppModel(
    val packageName: String,
    val appName: String,
    val icon: ImageBitmap?,
    val notificationCount: Int
)

@Stable
data class AppModelData(
    val packageName: String,
    val appName: String,
    val notificationCount: Int
)
