package com.example.docwatcher

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri

/*
 * Created by Sudhanshu Kumar on 24/09/23.
 */

class PdfDownloader(
    private val context: Context,
    private val listener: DocView.DownloadListener
) {

    companion object {
        private const val TAG = "DownloadUrl"
    }

    private val downloadManager = context.getSystemService(DownloadManager::class.java)

    fun download(url: String) {
        val request = DownloadManager.Request(url.toUri())
            .setMimeType("application/pdf")
            .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
            .setTitle("pdfDownloader")
            .setDestinationInExternalFilesDir(context, null, "downloaded.pdf")
        val downloadID = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadID)
        val handler = Handler(Looper.getMainLooper())
        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)

                val cursor = downloadManager.query(query)
                if(cursor != null && cursor.moveToFirst()) {
                    val downloaded = cursor.getLong(cursor.getColumnIndexOrThrow(
                        DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR
                    ))
                    val total = cursor.getLong(cursor.getColumnIndexOrThrow(
                        DownloadManager.COLUMN_TOTAL_SIZE_BYTES
                    ))
                    val columnIndex = cursor.getColumnIndexOrThrow(DownloadManager.COLUMN_STATUS)
                    val status = cursor.getInt(columnIndex)
                    val percentage = (downloaded * 100) / total
                    listener.onProgress(percentage)
                    Log.d(TAG, "percentage: $percentage")
                    if(status == DownloadManager.STATUS_SUCCESSFUL) {
                        Log.d(TAG, "status: Success")
                        val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                        val localUri = cursor.getString(uriIndex)
                        val downloadedFile = Uri.parse(localUri).path
                        listener.onCompleted(downloadedFile)
                    } else if(status == DownloadManager.STATUS_FAILED) {
                        listener.onFailed("Error")
                        Log.d(TAG, "status: Failed")
                    }
                }
            }
        }
        val scanUri = "content://downloads/my_downloads"
        context.contentResolver.registerContentObserver(Uri.parse(scanUri), true, observer)
    }

}