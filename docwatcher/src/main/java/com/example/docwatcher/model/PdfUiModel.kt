package com.example.docwatcher.model

import android.graphics.Bitmap

/*
 * Created by Sudhanshu Kumar on 04/09/23.
 */

data class PdfUiModel(
    val bitmap: Bitmap,
    val pageNo: Int,
    val totalPages: Int
)
