package com.example.lab12.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.example.lab12.data.repository.UserRepository
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

/**
 * Фоновый Service для загрузки файлов.
 *
 * Аннотация @AndroidEntryPoint позволяет Hilt внедрять зависимости
 * напрямую в поля Service через @Inject.
 * Service работает как foreground-сервис и показывает уведомление о прогрессе.
 */
@AndroidEntryPoint
class FileDownloadService : Service() {

    // Hilt автоматически внедряет зависимости через field injection
    @Inject
    lateinit var okHttpClient: OkHttpClient

    @Inject
    lateinit var userRepository: UserRepository

    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        Log.d(TAG, "FileDownloadService создан")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val fileUrl = intent?.getStringExtra(EXTRA_FILE_URL)
            ?: return START_NOT_STICKY

        val fileName = intent.getStringExtra(EXTRA_FILE_NAME) ?: "downloaded_file"

        startForeground(NOTIFICATION_ID, buildNotification("Загрузка: $fileName", 0))

        serviceScope.launch {
            downloadFile(fileUrl, fileName, startId)
        }

        return START_NOT_STICKY
    }

    private suspend fun downloadFile(url: String, fileName: String, startId: Int) {
        try {
            Log.d(TAG, "Начало загрузки файла: $url")

            val request = Request.Builder().url(url).build()
            val response = okHttpClient.newCall(request).execute()

            if (!response.isSuccessful) {
                Log.e(TAG, "Ошибка загрузки: HTTP ${response.code}")
                showCompletionNotification(fileName, success = false)
                stopSelf(startId)
                return
            }

            val body = response.body ?: run {
                Log.e(TAG, "Пустое тело ответа")
                showCompletionNotification(fileName, success = false)
                stopSelf(startId)
                return
            }

            val outputFile = File(getExternalFilesDir(null), fileName)
            val totalBytes = body.contentLength()
            var downloadedBytes = 0L

            FileOutputStream(outputFile).use { output ->
                body.byteStream().use { input ->
                    val buffer = ByteArray(8 * 1024)
                    var bytesRead: Int
                    while (input.read(buffer).also { bytesRead = it } != -1) {
                        output.write(buffer, 0, bytesRead)
                        downloadedBytes += bytesRead
                        if (totalBytes > 0) {
                            val progress = (downloadedBytes * 100 / totalBytes).toInt()
                            updateProgress(fileName, progress)
                        }
                    }
                }
            }

            Log.d(TAG, "Файл успешно загружен: ${outputFile.absolutePath}")
            showCompletionNotification(fileName, success = true)

        } catch (e: Exception) {
            Log.e(TAG, "Ошибка при загрузке файла", e)
            showCompletionNotification(fileName, success = false)
        } finally {
            stopSelf(startId)
        }
    }

    private fun updateProgress(fileName: String, progress: Int) {
        val notification = buildNotification("Загрузка: $fileName", progress)
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }

    private fun showCompletionNotification(fileName: String, success: Boolean) {
        val message = if (success) "Загружено: $fileName" else "Ошибка загрузки: $fileName"
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(if (success) "Загрузка завершена" else "Ошибка загрузки")
            .setContentText(message)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setAutoCancel(true)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID + 1, notification)
    }

    private fun buildNotification(contentText: String, progress: Int) =
        NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("Загрузка файла")
            .setContentText(contentText)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setProgress(100, progress, progress == 0)
            .setOngoing(true)
            .build()

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Загрузка файлов",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Уведомления о прогрессе загрузки файлов"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        Log.d(TAG, "FileDownloadService уничтожен")
    }

    companion object {
        private const val TAG = "FileDownloadService"
        private const val CHANNEL_ID = "file_download_channel"
        private const val NOTIFICATION_ID = 1001
        const val EXTRA_FILE_URL = "extra_file_url"
        const val EXTRA_FILE_NAME = "extra_file_name"

        fun createIntent(context: Context, fileUrl: String, fileName: String): Intent {
            return Intent(context, FileDownloadService::class.java).apply {
                putExtra(EXTRA_FILE_URL, fileUrl)
                putExtra(EXTRA_FILE_NAME, fileName)
            }
        }
    }
}
