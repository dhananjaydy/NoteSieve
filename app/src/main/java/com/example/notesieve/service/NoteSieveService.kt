
package com.example.notesieve.service

import android.app.Notification
import android.os.Bundle
import android.service.notification.NotificationListenerService
import android.service.notification.StatusBarNotification
import com.example.notesieve.data.local.NoteSieveEntity
import com.example.notesieve.data.repository.NoteSieveRepository
import com.example.notesieve.utils.getAppName
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NoteSieveService : NotificationListenerService() {

    @Inject
    lateinit var repository: NoteSieveRepository

    private val serviceJob = SupervisorJob()
    private val serviceScope = CoroutineScope(Dispatchers.Default + serviceJob)

    override fun onNotificationPosted(sbn: StatusBarNotification) {
        super.onNotificationPosted(sbn)
        val notification = sbn.notification
        val extras = notification.extras

        if (shouldCaptureNotification(sbn, notification, extras)) {
            val newNotification = createNoteSieveEntity(sbn, extras)
            serviceScope.launch { repository.addNotification(newNotification) }
        }
    }

    private fun shouldCaptureNotification(
        sbn: StatusBarNotification,
        notification: Notification,
        extras: Bundle
    ): Boolean {

        if (sbn.isOngoing) return false

        if ((notification.flags and Notification.FLAG_ONGOING_EVENT) != 0) return false

        if ((notification.flags and Notification.FLAG_NO_CLEAR) != 0) return false

        if (notification.category == Notification.CATEGORY_CALL) return false

        if (notification.category == Notification.CATEGORY_TRANSPORT) return false

        if (notification.group != null && (notification.flags and Notification.FLAG_GROUP_SUMMARY) != 0) return false

        val title = extras.getString(Notification.EXTRA_TITLE).orEmpty()
        val text = extras.getString(Notification.EXTRA_TEXT).orEmpty()
        if (title.matches(Regex("\\d+ new messages")) || text.matches(Regex("\\d+ new messages"))) return false

        return true
    }

    private fun createNoteSieveEntity(sbn: StatusBarNotification, extras: Bundle): NoteSieveEntity {
        return NoteSieveEntity(
            packageName = sbn.packageName,
            notificationContent = getNotificationContent(extras),
            notificationTitle = extras.getString(Notification.EXTRA_TITLE).orEmpty(),
            timestamp = sbn.postTime,
            isFavorite = false,
            appName = sbn.packageName.getAppName(applicationContext) ?: "Unknown App"
        )
    }

    private fun getNotificationContent(extras: Bundle): String {
        val text = extras.getString(Notification.EXTRA_TEXT).orEmpty()
        val bigText = extras.getCharSequence(Notification.EXTRA_BIG_TEXT)?.toString()
        val inboxLines = extras.getCharSequenceArray(Notification.EXTRA_TEXT_LINES)?.joinToString("\n")

        return when {
            bigText?.isNotEmpty() == true -> bigText
            inboxLines?.isNotEmpty() == true -> inboxLines
            else -> text
        }
    }

    override fun onDestroy() {
        serviceJob.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    companion object {
        const val TAG = "NoteSieveService"
    }
}