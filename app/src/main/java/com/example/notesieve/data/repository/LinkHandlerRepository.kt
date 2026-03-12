package com.example.notesieve.data.repository

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.util.Patterns
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class LinkHandlerRepositoryImpl @Inject constructor(
  @ApplicationContext private val context: Context
) : LinkHandlerRepository {

    override fun isValidUrl(url: String): Boolean {
        return Patterns.WEB_URL.matcher(url).matches()
    }

    override fun canOpenLink(url: String): Boolean {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        return intent.resolveActivity(context.packageManager) != null
    }

    override fun openLink(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        Log.d(TAG, "openLink: $url")
        context.startActivity(intent)
    }

    companion object {
        const val TAG = "LinkHandlerRepository"
    }

}

interface LinkHandlerRepository {
    fun isValidUrl(url: String): Boolean
    fun canOpenLink(url: String): Boolean
    fun openLink(url: String)
}

