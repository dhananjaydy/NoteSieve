package com.example.notesieve.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.text.format.DateUtils
import com.example.notesieve.R
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import androidx.core.graphics.createBitmap


// this should ideally be fetched from a remote source but will keep it constant for the time being
const val FORM_URL = "https://docs.google.com/forms/d/e/1FAIpQLSfyBY2IPc0Jl1CId_Pto9WlVPPk14y-a61CLkfr1SfDHRw4TA/viewform?vc=0&c=0&w=1&flr=0"

fun Drawable.toBitmap(): Bitmap {

    if (this is BitmapDrawable) {
        return this.bitmap
    }

    val bitmap = createBitmap(intrinsicWidth, intrinsicHeight)
    val canvas = android.graphics.Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}


fun Context.epochLongToString(timestamp: Long): String {

    val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())
    val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault())

    val calendarNow = Calendar.getInstance()
    val calendarThen = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }

    val time = timeFormat.format(Date(timestamp))

    return when {
        DateUtils.isToday(timestamp) -> {
            getString(R.string.today_at, time)
        }

        DateUtils.isToday(timestamp + DateUtils.DAY_IN_MILLIS) -> {
            getString(R.string.yesterday_at, time)
        }

        calendarNow.get(Calendar.YEAR) == calendarThen.get(Calendar.YEAR) -> {
            getString(R.string.at, dateFormat.format(Date(timestamp)), time)
        }

        else -> {
            SimpleDateFormat(getString(R.string.mmm_d_yyyy_at_h_mm_a), Locale.getDefault())
                .format(Date(timestamp))
        }
    }
}

fun String.getAppName(context: Context): String {
    return try {
        val packageManager = context.applicationContext.packageManager
        val applicationInfo = packageManager.getApplicationInfo(this, 0)
        packageManager.getApplicationLabel(applicationInfo).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        context.getString(R.string.unknown_app)
    }
}

fun clipToClipboard(
    ctx: Context,
    notification: String
) {

    val clipboardManager = ctx.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clipData = ClipData.newPlainText("Notification", notification)
    clipboardManager.setPrimaryClip(clipData)

}

fun openChooser(ctx: Context, notification: String) {
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, notification)
        type = "text/plain"
    }

    val chooser = Intent.createChooser(shareIntent, "Share via")
    ctx.startActivity(chooser)
}
