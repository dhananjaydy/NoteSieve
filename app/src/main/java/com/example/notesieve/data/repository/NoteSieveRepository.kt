package com.example.notesieve.data.repository

import android.content.pm.PackageManager
import androidx.compose.ui.graphics.asImageBitmap
import com.example.notesieve.data.local.AppModel
import com.example.notesieve.data.local.NoteSieveDao
import com.example.notesieve.data.local.NoteSieveEntity
import com.example.notesieve.utils.toBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.withContext
import javax.inject.Inject


class NoteSieveRepositoryImpl @Inject constructor(
    private val noteSieveDao: NoteSieveDao,
    private val appPackageManager: PackageManager
) : NoteSieveRepository {

    override fun getAllNotifications(): Flow<List<NoteSieveEntity>> {
        return noteSieveDao.getAllNotifications()
    }

    override suspend fun starNotification(notificationId: Int) {
        noteSieveDao.starNotification(notificationId)
    }

    override suspend fun unstarNotification(notificationId: Int) {
        noteSieveDao.unstarNotification(notificationId)
    }

    override fun getAllStarredNotifications(): Flow<List<NoteSieveEntity>> {
        return noteSieveDao.getAllStarredNotifications()
    }

    override suspend fun addNotification(notification: NoteSieveEntity) {
        withContext(Dispatchers.IO) { noteSieveDao.addNotification(notification) }
    }

    override fun getAppModels(): Flow<List<AppModel>> {
        return noteSieveDao.getAppModels().map { appModelList ->
            appModelList.map { appModelData ->
                val appIcon = try {
                    appPackageManager.getApplicationIcon(appModelData.packageName).toBitmap().asImageBitmap()
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }
                AppModel(
                    packageName = appModelData.packageName,
                    appName = appModelData.appName,
                    icon = appIcon,
                    notificationCount = appModelData.notificationCount
                )
            }.sortedBy { it.appName }
        }
    }

    override suspend fun fetchNotificationsForApp(packageName: String): List<NoteSieveEntity> {
        return withContext(Dispatchers.IO) {
            packageName.let { name ->
                getAllNotifications().first().filter { it.packageName == name }
            }
        }
    }

    override suspend fun deleteNotification(notificationId: Int) {
        withContext(Dispatchers.IO) {
            noteSieveDao.deleteNotification(notificationId)
        }
    }
}

interface NoteSieveRepository {

    fun getAllNotifications(): Flow<List<NoteSieveEntity>>
    suspend fun starNotification(notificationId: Int)
    suspend fun unstarNotification(notificationId: Int)
    fun getAllStarredNotifications(): Flow<List<NoteSieveEntity>>
    suspend fun addNotification(notification: NoteSieveEntity)
    fun getAppModels(): Flow<List<AppModel>>
    suspend fun fetchNotificationsForApp(packageName: String): List<NoteSieveEntity>
    suspend fun deleteNotification(notificationId: Int)
}