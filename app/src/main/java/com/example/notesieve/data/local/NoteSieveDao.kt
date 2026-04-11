package com.example.notesieve.data.local

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteSieveDao {

    @Query("UPDATE notifications_table SET isFavorite = 1 WHERE id = :notificationId")
    suspend fun starNotification(notificationId: Int)

    @Query("UPDATE notifications_table SET isFavorite = 0 WHERE id = :notificationId")
    suspend fun unstarNotification(notificationId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotification(notification: NoteSieveEntity)

    @Query("SELECT packageName, MAX(appName) as appName, COUNT(id) as notificationCount FROM notifications_table GROUP BY packageName, appName")
    fun getAppModels(): Flow<List<AppModelData>>

    @Query("DELETE FROM notifications_table WHERE id = :notificationId")
    suspend fun deleteNotification(notificationId: Int)

    @Query("SELECT DISTINCT appName FROM notifications_table ORDER BY appName ASC")
    fun getAppNamesWithNotifications(): Flow<List<String>>

    @Query("DELETE FROM notifications_table WHERE packageName IN (:appPackageNames) AND timestamp >= :startTime")
    suspend fun deleteNotificationsForApps(appPackageNames: List<String>, startTime: Long): Int

    @Query("""
        SELECT * FROM notifications_table
        WHERE (:query = '' OR notificationTitle LIKE :query OR notificationContent LIKE :query OR appName LIKE :query)
        ORDER BY timestamp DESC
    """)
    fun getAllNotificationsPaged(query: String = ""): PagingSource<Int, NoteSieveEntity>

    @Query("""
        SELECT * FROM notifications_table
        WHERE isFavorite = 1
        AND (:query = '' OR notificationTitle LIKE :query OR notificationContent LIKE :query OR appName LIKE :query)
        ORDER BY timestamp DESC
    """)
    fun getAllStarredNotificationsPaged(query: String = ""): PagingSource<Int, NoteSieveEntity>

    @Query("""
        SELECT * FROM notifications_table
        WHERE packageName = :packageName
        AND (:query = '' OR notificationTitle LIKE :query OR notificationContent LIKE :query OR appName LIKE :query)
        ORDER BY timestamp DESC
    """)
    fun getNotificationsForAppPaged(packageName: String, query: String = ""): PagingSource<Int, NoteSieveEntity>

}