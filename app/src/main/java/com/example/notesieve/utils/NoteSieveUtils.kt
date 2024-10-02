package com.example.notesieve.utils

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.example.notesieve.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// this should ideally be fetched from a remote source but will keep it constant for the time being
const val FORM_URL = "https://docs.google.com/forms/d/e/1FAIpQLSfyBY2IPc0Jl1CId_Pto9WlVPPk14y-a61CLkfr1SfDHRw4TA/viewform?vc=0&c=0&w=1&flr=0"

fun Drawable.toBitmap(): Bitmap {

    if (this is BitmapDrawable) {
        return this.bitmap
    }

    val bitmap = Bitmap.createBitmap(intrinsicWidth, intrinsicHeight, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(bitmap)
    setBounds(0, 0, canvas.width, canvas.height)
    draw(canvas)
    return bitmap
}


fun Context.epochLongToString(timestamp: Long): String {

    val date = Date(timestamp)
    val formatPattern = getString(R.string.timestamp_to_date_time_format)
    val formatter = SimpleDateFormat(formatPattern, Locale.getDefault())
    return formatter.format(date)
}

fun String.getAppName(context: Context): String {
    return try {
        val packageManager = context.applicationContext.packageManager
        val applicationInfo = packageManager.getApplicationInfo(this, 0)
        packageManager.getApplicationLabel(applicationInfo).toString()
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown App"
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
