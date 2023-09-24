package com.example.docwatcher.type

/*
 * Created by Sudhanshu Kumar on 24/09/23.
 */

sealed class PdfPathType {
    object InternalStorage : PdfPathType()
    object ExternalStorage : PdfPathType()
    object Internet : PdfPathType()
}
