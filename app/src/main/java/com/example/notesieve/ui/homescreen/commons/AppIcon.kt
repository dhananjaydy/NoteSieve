package com.example.notesieve.ui.homescreen.commons

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.ProgressBar
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.notesieve.utils.toBitmap

@Composable
fun AppIcon(
    packageName: String,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp
) {

    val context = LocalContext.current
    var icon by remember { mutableStateOf<Drawable?>(null) }
    var isError by remember { mutableStateOf(false) }

    LaunchedEffect(packageName) {
        icon = try {
            val packageManager: PackageManager = context.packageManager
            val applicationInfo: ApplicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationIcon(applicationInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            Log.e("AppIconLoader", "Package not found: $packageName")
            isError = true
            null
        }
    }

    Box(
        modifier = modifier
            .size(size)
            .wrapContentSize(Alignment.Center)

    ) {
        when {
            isError -> {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Error Loading Icon",
                    tint = Color.Red,
                    modifier = Modifier.fillMaxSize()
                )
            }
            icon != null -> {
                val bitmap = remember(icon) {
                    try {
                        icon?.toBitmap()?.asImageBitmap()
                    } catch (e: Exception) {
                        Log.e("AppIconLoader", "Failed to convert drawable to bitmap", e)
                        null
                    }
                }
                if (bitmap != null) {
                    Image(
                        bitmap = bitmap,
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    ErrorIndicator(48.dp)
                }
            }
            else -> {
                ProgressBar(context)
            }
        }
    }
}