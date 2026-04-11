package com.example.notesieve.data.repository

import android.content.pm.PackageManager
import androidx.compose.ui.graphics.asImageBitmap
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.example.notesieve.data.local.AppModel
import com.example.notesieve.data.local.NoteSieveDao
import com.example.notesieve.data.local.NoteSieveEntity
import com.example.notesieve.utils.toBitmap
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Named

class NoteSieveRepositoryImpl @Inject constructor(
    private val noteSieveDao: NoteSieveDao,
    private val appPackageManager: PackageManager,
    @Named("IoDispatcher") private val ioDispatcher: CoroutineDispatcher,
    @Named("DefaultDispatcher") private val defaultDispatcher: CoroutineDispatcher
) : NoteSieveRepository {

    override suspend fun starNotification(notificationId: Int) {
        withContext(ioDispatcher) {
            noteSieveDao.starNotification(notificationId)
        }
    }

    override suspend fun unstarNotification(notificationId: Int) {
        withContext(ioDispatcher) {
            noteSieveDao.unstarNotification(notificationId)
        }
    }

    override suspend fun addNotification(notification: NoteSieveEntity) {
        withContext(ioDispatcher) { noteSieveDao.addNotification(notification) }
    }

    override fun getAppModels(): Flow<List<AppModel>> {
        return noteSieveDao.getAppModels().map { appModelList ->
            appModelList.map { appModelData ->
                val appIcon = try {
                    appPackageManager.getApplicationIcon(appModelData.packageName)
                        .toBitmap()
                        .asImageBitmap()
                } catch (e: PackageManager.NameNotFoundException) {
                    null
                }
                AppModel(
                    packageName = appModelData.packageName,
                    appName = appModelData.appName,
                    icon = appIcon,
                    notificationCount = appModelData.notificationCount,
                    key = generateCustomKey(
                        packageName = appModelData.packageName,
                        notificationCount = appModelData.notificationCount
                    )
                )
            }.sortedBy { it.appName }
        }
    }

    override suspend fun deleteNotification(notificationId: Int) {
        withContext(ioDispatcher) { noteSieveDao.deleteNotification(notificationId) }
    }

    override fun getAppNamesWithNotifications(): Flow<List<String>> {
        return noteSieveDao.getAppNamesWithNotifications().flowOn(ioDispatcher)
    }

    override suspend fun deleteNotificationsForApps(packageNames: List<String>, startTime: Long): Int {
        return withContext(ioDispatcher) {
            noteSieveDao.deleteNotificationsForApps(packageNames, startTime)
        }
    }

    private fun String.asLikeQuery(): String =
        if (isEmpty()) "" else "%$this%"

    override fun getAllNotificationsPaged(query: String): Flow<PagingData<NoteSieveEntity>> =
        Pager(pagingConfig()) {
            noteSieveDao.getAllNotificationsPaged(query.asLikeQuery())
        }.flow

    override fun getAllStarredNotificationsPaged(query: String): Flow<PagingData<NoteSieveEntity>> =
        Pager(pagingConfig()) {
            noteSieveDao.getAllStarredNotificationsPaged(query.asLikeQuery())
        }.flow

    override fun getNotificationsForAppPaged(
        packageName: String,
        query: String
    ): Flow<PagingData<NoteSieveEntity>> =
        Pager(pagingConfig()) {
            noteSieveDao.getNotificationsForAppPaged(packageName, query.asLikeQuery())
        }.flow

    private fun pagingConfig() = PagingConfig(
        pageSize = 20,
        prefetchDistance = 5,
        enablePlaceholders = false
    )
}

private fun generateCustomKey(packageName: String, notificationCount: Int) =
    "$packageName:${System.currentTimeMillis()}:$notificationCount"


interface NoteSieveRepository {

    suspend fun starNotification(notificationId: Int)
    suspend fun unstarNotification(notificationId: Int)
    suspend fun addNotification(notification: NoteSieveEntity)
    fun getAppModels(): Flow<List<AppModel>>
    suspend fun deleteNotification(notificationId: Int)
    fun getAppNamesWithNotifications(): Flow<List<String>>
    suspend fun deleteNotificationsForApps(packageNames: List<String>, startTime: Long): Int
    fun getAllNotificationsPaged(query: String = ""): Flow<PagingData<NoteSieveEntity>>
    fun getAllStarredNotificationsPaged(query: String = ""): Flow<PagingData<NoteSieveEntity>>
    fun getNotificationsForAppPaged(packageName: String, query: String = ""): Flow<PagingData<NoteSieveEntity>>

}