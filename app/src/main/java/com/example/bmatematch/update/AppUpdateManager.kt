package com.example.bmatematch.update

import android.app.DownloadManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Environment
import androidx.core.content.FileProvider
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class AvailableUpdate(
    val versionCode: Int,
    val apkUrl: String,
    val releaseNotes: String
)

object AppUpdateConfig {
    // Host latest.json and the APK at a stable HTTPS URL.
    const val manifestUrl = "https://YOUR_UPDATE_HOST.example.com/bmatch/latest.json"
}

class AppUpdateManager(private val context: Context) {
    suspend fun check(): AvailableUpdate? = withContext(Dispatchers.IO) {
        if (AppUpdateConfig.manifestUrl.contains("YOUR_UPDATE_HOST")) return@withContext null
        val connection = (URL(AppUpdateConfig.manifestUrl).openConnection() as HttpURLConnection).apply {
            connectTimeout = 8_000
            readTimeout = 8_000
            requestMethod = "GET"
        }
        try {
            if (connection.responseCode !in 200..299) return@withContext null
            val body = connection.inputStream.bufferedReader().use { it.readText() }
            val json = JSONObject(body)
            val versionCode = json.getInt("versionCode")
            if (versionCode <= currentVersionCode()) return@withContext null
            AvailableUpdate(
                versionCode = versionCode,
                apkUrl = json.getString("apkUrl"),
                releaseNotes = json.optString("releaseNotes")
            )
        } finally {
            connection.disconnect()
        }
    }

    fun downloadAndInstall(update: AvailableUpdate) {
        val request = DownloadManager.Request(Uri.parse(update.apkUrl))
            .setTitle("BMatch update")
            .setDescription("Downloading version ${update.versionCode}")
            .setMimeType("application/vnd.android.package-archive")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, "bmatch-${update.versionCode}.apk")
        val downloadId = context.getSystemService(DownloadManager::class.java).enqueue(request)
        ContextCompat.registerReceiver(
            context,
            object : android.content.BroadcastReceiver() {
                override fun onReceive(receiverContext: Context, intent: Intent) {
                    if (intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1) != downloadId) return
                    val apk = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                        ?.resolve("bmatch-${update.versionCode}.apk") ?: return
                    val apkUri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", apk)
                    val installIntent = Intent(Intent.ACTION_VIEW).apply {
                        setDataAndType(apkUri, "application/vnd.android.package-archive")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    context.startActivity(installIntent)
                    context.unregisterReceiver(this)
                }
            },
            android.content.IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE),
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun currentVersionCode(): Int =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.packageManager.getPackageInfo(context.packageName, PackageManager.PackageInfoFlags.of(0)).longVersionCode.toInt()
        } else {
            @Suppress("DEPRECATION")
            context.packageManager.getPackageInfo(context.packageName, 0).versionCode
        }
}
