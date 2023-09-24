package com.example.pdfviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.docwatcher.state.DownloadState
import com.example.docwatcher.type.PdfPathType
import com.example.pdfviewer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val INTERNAL_STORAGE_PATH =
            "/data/data/com.example.pdfviewer/files/sample_pdf.pdf"
        private const val INTERNET_PATH =
            "https://www.eurofound.europa.eu/sites/default/files/ef_publication/field_ef_document/ef1710en.pdf"
        private const val EXTERNAL_STORAGE_PATH = ""
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding
        binding.docView.loadData(
            INTERNET_PATH,
            PdfPathType.Internet,
        )
        binding.pbPdf.visibility = View.VISIBLE
        binding.docView.setDownloadStateChangeListener {
            when(it) {
                is DownloadState.Error -> {
                    binding.pbPdf.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }
                is DownloadState.Completed -> {
                    binding.pbPdf.visibility = View.GONE
                }
                is DownloadState.InProgress -> {
                    binding.pbPdf.progress = it.progress.toInt()
                }
            }
        }
    }
}