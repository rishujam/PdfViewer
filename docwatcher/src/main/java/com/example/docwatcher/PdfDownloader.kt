package com.example.docwatcher

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.util.Log
import java.io.File

/*
 * Created by Sudhanshu Kumar on 24/09/23.
 */

class PdfDownloader(
    private val context: Context,
    private val downloadListener: DocView.DownloadListener
) {

    private var downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
    private var downloadID: Long = -1

    private val downloadReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val action = intent?.action
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE == action) {
                val query = DownloadManager.Query().setFilterById(downloadID)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)
                    val status = cursor.getInt(columnIndex)
                    if (status == DownloadManager.STATUS_SUCCESSFUL) {
                        val uriIndex = cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                        val localUri = cursor.getString(uriIndex)
                        val downloadedFile = Uri.parse(localUri).path
                        Log.d("PdfDownloader", downloadedFile.toString())
                        downloadListener.onCompleted(downloadedFile)
                    } else if (status == DownloadManager.STATUS_FAILED) {
                        Log.d("PdfDownloader", "Download FAILED")
                    }
                }
                cursor.close()
            }
        }
    }

    fun download(url: String) {
        val request = DownloadManager.Request(Uri.parse(url))
        request.setTitle("PDF Download")
        request.setDescription("Downloading a PDF file")
        request.setDestinationInExternalFilesDir(context, null, "sample.pdf")

        downloadID = downloadManager.enqueue(request)
        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
        context.registerReceiver(downloadReceiver, filter)
    }

}