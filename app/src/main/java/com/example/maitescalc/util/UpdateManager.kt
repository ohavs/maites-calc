package com.example.maitescalc.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import androidx.core.content.FileProvider
import com.example.maitescalc.data.remote.GitHubApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val isUpdateAvailable: Boolean,
    val latestVersion: String,
    val changelog: String,
    val downloadUrl: String,
    val fileSize: Long,
    val releaseName: String
)

class UpdateManager(private val context: Context) {

    companion object {
        const val CURRENT_VERSION = "1.0.0"
        const val UPDATE_FILE_NAME = "maites-calc-update.apk"
    }

    suspend fun checkForUpdate(): Result<UpdateInfo> = withContext(Dispatchers.IO) {
        try {
            val release = GitHubApi.service.getLatestRelease()
            val cleanVersion = release.tag_name.trim().removePrefix("v").removePrefix("V")
            val apkAsset = release.assets.find { it.name.endsWith(".apk", ignoreCase = true) }
                ?: release.assets.firstOrNull()

            val downloadUrl = apkAsset?.browser_download_url ?: ""
            val fileSize = apkAsset?.size ?: 0L

            val isNewer = isVersionNewer(cleanVersion, CURRENT_VERSION)

            Result.success(
                UpdateInfo(
                    isUpdateAvailable = isNewer,
                    latestVersion = release.tag_name,
                    changelog = release.body ?: "אין פירוט שינויים לגרסה זו",
                    downloadUrl = downloadUrl,
                    fileSize = fileSize,
                    releaseName = release.name ?: release.tag_name
                )
            )
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun isVersionNewer(remote: String, local: String): Boolean {
        try {
            val remoteParts = remote.split(".").map { it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0 }
            val localParts = local.split(".").map { it.filter { ch -> ch.isDigit() }.toIntOrNull() ?: 0 }

            val maxLen = maxOf(remoteParts.size, localParts.size)
            for (i in 0 until maxLen) {
                val r = remoteParts.getOrElse(i) { 0 }
                val l = localParts.getOrElse(i) { 0 }
                if (r > l) return true
                if (r < l) return false
            }
            return false
        } catch (_: Exception) {
            return remote != local
        }
    }

    suspend fun downloadAndInstallApk(
        downloadUrl: String,
        onProgress: (percent: Int, bytesRead: Long, totalBytes: Long) -> Unit
    ): Result<File> = withContext(Dispatchers.IO) {
        try {
            val destinationDir = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)
                ?: context.cacheDir
            val destinationFile = File(destinationDir, UPDATE_FILE_NAME)

            if (destinationFile.exists()) {
                destinationFile.delete()
            }

            var currentUrl = downloadUrl
            var connection: HttpURLConnection
            var redirects = 0

            // Follow redirects if any (GitHub releases redirect to AWS S3)
            while (true) {
                val url = URL(currentUrl)
                connection = url.openConnection() as HttpURLConnection
                connection.instanceFollowRedirects = false
                connection.connectTimeout = 15000
                connection.readTimeout = 30000

                val status = connection.responseCode
                if (status == HttpURLConnection.HTTP_MOVED_PERM ||
                    status == HttpURLConnection.HTTP_MOVED_TEMP ||
                    status == 307 || status == 308
                ) {
                    val newUrl = connection.getHeaderField("Location")
                    currentUrl = newUrl
                    redirects++
                    if (redirects > 8) throw IllegalStateException("Too many redirects")
                } else if (status == HttpURLConnection.HTTP_OK) {
                    break
                } else {
                    throw IllegalStateException("HTTP server returned error code $status")
                }
            }

            val totalBytes = connection.contentLengthLong
            val inputStream = connection.inputStream
            val outputStream = FileOutputStream(destinationFile)

            val buffer = ByteArray(8192)
            var bytesReadTotal = 0L
            var bytesRead: Int

            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                outputStream.write(buffer, 0, bytesRead)
                bytesReadTotal += bytesRead

                if (totalBytes > 0) {
                    val percent = ((bytesReadTotal * 100) / totalBytes).toInt()
                    withContext(Dispatchers.Main) {
                        onProgress(percent, bytesReadTotal, totalBytes)
                    }
                }
            }

            outputStream.flush()
            outputStream.close()
            inputStream.close()
            connection.disconnect()

            withContext(Dispatchers.Main) {
                installApk(destinationFile)
            }

            Result.success(destinationFile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun installApk(apkFile: File) {
        // Check permission on Android 8.0+ (Oreo)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!context.packageManager.canRequestPackageInstalls()) {
                val settingsIntent = Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES).apply {
                    data = Uri.parse("package:${context.packageName}")
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(settingsIntent)
                return
            }
        }

        val apkUri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            apkFile
        )

        val installIntent = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(apkUri, "application/vnd.android.package-archive")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }

        context.startActivity(installIntent)
    }
}
