package com.example.lab12.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import com.example.lab12.worker.SyncWorker
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NetworkChangeReceiver : BroadcastReceiver() {

    @Inject
    lateinit var workManager: WorkManager

    override fun onReceive(context: Context, intent: Intent) {
        if (isNetworkAvailable(context)) {
            Log.d("NetworkChangeReceiver", "Network available, enqueueing SyncWorker")
            val request = OneTimeWorkRequestBuilder<SyncWorker>().build()
            workManager.enqueue(request)
        }
    }

    private fun isNetworkAvailable(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}
