package com.kj.steady.logic

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.core.content.FileProvider
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val releaseNotes: String,
    val downloadUrl: String
)

object UpdateManager {

    const val DEFAULT_UPDATE_URL = "https://raw.githubusercontent.com/lycanthropekj/kj-steady/main/version.json"

    suspend fun checkForUpdate(endpointUrl: String, currentVersionCode: Int = 3): UpdateInfo? = withContext(Dispatchers.IO) {
        try {
            val url = URL(endpointUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 8000
            conn.readTimeout = 8000
            conn.requestMethod = "GET"
            conn.setRequestProperty("Accept", "application/json")

            if (conn.responseCode == 200) {
                val jsonString = conn.inputStream.bufferedReader().use { it.readText() }
                val gson = Gson()
                val root = gson.fromJson(jsonString, JsonObject::class.java)
                val remoteVersionCode = root.get("versionCode")?.asInt ?: return@withContext null
                val remoteVersionName = root.get("versionName")?.asString ?: "New Version"
                val releaseNotes = root.get("releaseNotes")?.asString ?: "Bug fixes and improvements"
                val downloadUrl = root.get("downloadUrl")?.asString ?: return@withContext null

                if (remoteVersionCode > currentVersionCode) {
                    return@withContext UpdateInfo(
                        versionCode = remoteVersionCode,
                        versionName = remoteVersionName,
                        releaseNotes = releaseNotes,
                        downloadUrl = downloadUrl
                    )
                }
            }
            null
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun downloadApk(
        context: Context,
        downloadUrl: String,
        onProgress: (Float) -> Unit
    ): File? = withContext(Dispatchers.IO) {
        try {
            val updatesDir = File(context.cacheDir, "updates")
            if (!updatesDir.exists()) updatesDir.mkdirs()
            val targetFile = File(updatesDir, "Stratos-update.apk")
            if (targetFile.exists()) targetFile.delete()

            val url = URL(downloadUrl)
            val conn = url.openConnection() as HttpURLConnection
            conn.connectTimeout = 15000
            conn.readTimeout = 30000
            conn.connect()

            val fileLength = conn.contentLength
            val input = conn.inputStream
            val output = FileOutputStream(targetFile)

            val data = ByteArray(8192)
            var total: Long = 0
            var count: Int
            while (input.read(data).also { count = it } != -1) {
                total += count
                if (fileLength > 0) {
                    onProgress(total.toFloat() / fileLength.toFloat())
                }
                output.write(data, 0, count)
            }

            output.flush()
            output.close()
            input.close()

            targetFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun promptInstallApk(context: Context, apkFile: File) {
        try {
            // Check Android 8.0+ permission to install unknown apps
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (!context.packageManager.canRequestPackageInstalls()) {
                    val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                        data = Uri.parse("package:${context.packageName}")
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    context.startActivity(settingsIntent)
                    return
                }
            }

            val apkUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                apkFile
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_GRANT_READ_URI_PERMISSION
            }
            context.startActivity(installIntent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
