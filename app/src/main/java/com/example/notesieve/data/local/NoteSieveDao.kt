package com.example.notesieve.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteSieveDao {

    @Query("SELECT * FROM notifications_table ORDER BY timestamp DESC")
    fun getAllNotifications(): Flow<List<NoteSieveEntity>>

    @Query("UPDATE notifications_table SET isFavorite = 1 WHERE id = :notificationId")
    suspend fun starNotification(notificationId: Int)

    @Query("UPDATE notifications_table SET isFavorite = 0 WHERE id = :notificationId")
    suspend fun unstarNotification(notificationId: Int)

    @Query("SELECT * FROM notifications_table WHERE isFavorite = 1 ORDER BY timestamp DESC")
    fun getAllStarredNotifications(): Flow<List<NoteSieveEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotification(notification: NoteSieveEntity)

    @Query("SELECT packageName,  MAX(appName) as appName, COUNT(id) as notificationCount FROM notifications_table GROUP BY packageName, appName")
    fun getAppModels(): Flow<List<AppModelData>>

    @Query("DELETE FROM notifications_table WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: Int)

}