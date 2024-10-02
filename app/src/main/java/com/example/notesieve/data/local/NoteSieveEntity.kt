package com.example.notesieve.data.local

import androidx.compose.runtime.Stable
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notifications_table")
data class NoteSieveEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val appName: String = "",
    val notificationTitle: String,
    val notificationContent: String,
    val timestamp: Long,
    val packageName: String,
    val isFavorite: Boolean,
)

@Stable
data class NoteSieveModel(
    val id: Int,
    val appName: String,
    val notificationTitle: String,
    val notificationContent: String,
    val timestamp: Long,
    val packageName: String,
    val isFavorite: Boolean,
    val showOptions: Boolean
)

fun NoteSieveEntity.asNoteSieveModel2(ifShow: Boolean): NoteSieveModel =
    NoteSieveModel(
        id = id,
        appName = appName,
        notificationTitle = notificationTitle,
        notificationContent = notificationContent,
        timestamp = timestamp,
        packageName = packageName,
        isFavorite = isFavorite,
        showOptions = ifShow
    )

