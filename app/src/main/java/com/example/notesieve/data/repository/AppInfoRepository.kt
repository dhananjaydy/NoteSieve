package com.example.notesieve.data.repository

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.example.notesieve.data.local.AppModel
import com.example.notesieve.utils.toBitmap
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AppInfoRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val packageManager = context.packageManager

    fun getAppName(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }

    fun getAppIcon(packageName: String): ImageBitmap? {
        return try {
            packageManager.getApplicationIcon(packageName).toBitmap().asImageBitmap()
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }

}
