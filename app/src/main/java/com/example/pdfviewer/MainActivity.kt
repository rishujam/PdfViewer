package com.example.pdfviewer

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.docwatcher.type.PdfPathType
import com.example.pdfviewer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val INTERNAL_STORAGE_PATH =
            "/data/data/com.example.pdfviewer/files/sample_pdf.pdf"
        private const val INTERNET_PATH =
            "https://www.adobe.com/support/products/enterprise/knowledgecenter/media/c4611_sample_explain.pdf"
        private const val EXTERNAL_STORAGE_PATH = ""
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.docView.loadData(INTERNAL_STORAGE_PATH, PdfPathType.InternalStorage)
    }
}