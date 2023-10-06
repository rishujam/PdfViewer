package com.docs.docwatcher.state

/*
 * Created by Sudhanshu Kumar on 24/09/23.
 */

sealed class DownloadState {

    data class Error(val message: String) : DownloadState()
    object Completed : DownloadState()
    data class InProgress(val progress: Long, val totalSize: Long) : DownloadState()

}
