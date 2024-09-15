package com.example.notesieve.service

import android.app.Notification
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import android.util.Log
import com.example.notesieve.data.local.NoteSieveEntity
import com.example.notesieve.data.repository.NoteSieveRepository
import com.example.notesieve.utils.getAppName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CompletableJob
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NoteSieveService : NotificationListenerService() {

    @Inject
    lateinit var repository : NoteSieveRepository

    lateinit var job: Job
    lateinit var serviceScope: CoroutineScope

    override fun onCreate() {
        super.onCreate()
        job = SupervisorJob()
        serviceScope = CoroutineScope(context = Dispatchers.Default + job)
    }

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        val notification = sbn.notification
        val extras = notification.extras

        val isLikelySticky = checkIfNotificationSticky(notification, sbn)

        if (!isLikelySticky) {
            val newNotification = with(sbn) {

                NoteSieveEntity(
                    packageName = packageName,
                    notificationContent = extras.getString(Notification.EXTRA_TEXT).orEmpty(),
                    notificationTitle = extras.getString(Notification.EXTRA_TITLE).orEmpty(),
                    timestamp = postTime,
                    isFavorite = false,
                    appName = packageName.getAppName(packageManager)
                )
            }

            serviceScope.launch { repository.addNotification(newNotification) }
        }
    }

    private fun checkIfNotificationSticky(
        notification: Notification,
        sbn: StatusBarNotification
    ): Boolean {
        val isOngoingEvent = (notification.flags and Notification.FLAG_ONGOING_EVENT) != 0
        val isNoClear = (notification.flags and Notification.FLAG_NO_CLEAR) != 0
        val isCallOrTransport = notification.category in listOf(Notification.CATEGORY_CALL, Notification.CATEGORY_TRANSPORT)
        val isForegroundService = sbn.isOngoing
        val isMediaStyle = notification.extras.getString(Notification.EXTRA_TEMPLATE)?.contains("MediaStyle") == true
        return isOngoingEvent || isNoClear || isCallOrTransport || isForegroundService || isMediaStyle
    }

    override fun onDestroy() {
        super.onDestroy()
        job.cancel()
        serviceScope.cancel()
    }
}

