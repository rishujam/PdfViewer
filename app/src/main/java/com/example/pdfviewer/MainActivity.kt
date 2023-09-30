package com.example.pdfviewer

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.example.docwatcher.state.DownloadState
import com.example.pdfviewer.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    companion object {
        private const val INTERNAL_STORAGE_PATH =
            "/data/data/com.example.pdfviewer/files/sample_pdf.pdf"
        private const val INTERNET_PATH =
            "https://research.nhm.org/pdfs/10840/10840-001.pdf"
        private const val EXTERNAL_STORAGE_PATH = ""
    }

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.docView.loadData(
            INTERNET_PATH,
            true,
        )
        binding.pbPdf.visibility = View.VISIBLE
        binding.tvFileSize.visibility = View.VISIBLE
        binding.docView.setDownloadStateChangeListener {
            when (it) {
                is DownloadState.Error -> {
                    binding.pbPdf.visibility = View.GONE
                    binding.tvFileSize.visibility = View.GONE
                    Toast.makeText(this, it.message, Toast.LENGTH_SHORT).show()
                }

                is DownloadState.Completed -> {
                    binding.pbPdf.visibility = View.GONE
                    binding.tvFileSize.visibility = View.GONE
                }

                is DownloadState.InProgress -> {
                    binding.tvFileSize.text = getFileSizeText(it.totalSize)
                    binding.pbPdf.progress = it.progress.toInt()
                }
            }
        }
    }

    private fun getFileSizeText(bytes: Long): String {
        if (bytes < 0) return ""
        val sizeInKb = bytes / 1000
        return if(sizeInKb >= 1000) {
            val sizeInMb = sizeInKb / 1000
            "$sizeInMb MB Downloading..."
        } else {
            "$sizeInKb KB Downloading..."
        }
    }
}