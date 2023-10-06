package com.docs.docwatcher

import android.content.Context
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.doc.docwatcher.R
import com.docs.docwatcher.data.PdfDownloader
import com.docs.docwatcher.model.PdfUiModel
import com.docs.docwatcher.state.DownloadState
import com.docs.docwatcher.util.show
import com.docs.docwatcher.util.showPagesViewForSomeSeconds
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import java.io.File

/*
 * Created by Sudhanshu Kumar on 07/09/23.
 */

class DocView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private lateinit var pdfAdapter: PDFAdapter
    private var noOfPages: String? = null
    private val coroutineScope: CoroutineScope = CoroutineScope(Dispatchers.IO)
    private var pageText: TextView
    private var pageCard: CardView
    private var rv: RecyclerView
    private lateinit var pdfDownloader: PdfDownloader
    private var downloadCompletedBlock: ((DownloadState) -> Unit)? = null

    init {
        removeAllViews()
        LayoutInflater.from(context).inflate(R.layout.doc_view, this, true)
        pageText = findViewById(R.id.tvPageNo)
        pageCard = findViewById(R.id.cdPages)
        rv = findViewById(R.id.pdfRv)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        pdfAdapter = PDFAdapter()
        rv.apply {
            adapter = pdfAdapter
            layoutManager = LinearLayoutManager(context)
        }
    }

    fun setDownloadStateChangeListener(block: (DownloadState) -> Unit) {
        downloadCompletedBlock = block
    }

    fun loadData(uri: String, fromInternet: Boolean) {
        if(!fromInternet) {
            setData(uri)
        } else {
            val listener = object : DownloadListener {
                override fun onCompleted(path: String?) {
                    downloadCompletedBlock?.let { it(DownloadState.Completed) }
                    setData(path.toString())
                }

                override fun onFailed(message: String) {
                    downloadCompletedBlock?.let { it(DownloadState.Error(message)) }
                }

                override fun onProgress(progress: Long, totalSize: Long) {
                    downloadCompletedBlock?.let { it(DownloadState.InProgress(progress, totalSize)) }
                }
            }
            pdfDownloader = PdfDownloader(context, listener)
            pdfDownloader.download(uri)
        }
    }

    private fun setData(uri: String) {
        val list = generateBitmaps(uri)
        noOfPages = list.size.toString()
        pdfAdapter.differ.submitList(list)
        val cardText = "1 / ${list.size}"
        pageText.text = cardText
        pageCard.showPagesViewForSomeSeconds(coroutineScope, context)
        rv.setOnScrollChangeListener { _, _, _, _, _ ->
            val visiblePosition = (rv.layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
            if(visiblePosition != -1) {
                val currPage = visiblePosition + 1
                if(!noOfPages.isNullOrEmpty()) {
                    pageCard.show()
                    val text = "$currPage / $noOfPages"
                    pageText.text = text
                    pageCard.showPagesViewForSomeSeconds(coroutineScope, context)
                }
            }
        }
    }

    private fun generateBitmaps(uri: String): List<PdfUiModel> {
        val output = mutableListOf<PdfUiModel>()
        val input = ParcelFileDescriptor.open(File(uri), ParcelFileDescriptor.MODE_READ_ONLY)
        val renderer = PdfRenderer(input)
        val pageCount = renderer.pageCount
        for(i in 0 until pageCount) {
            val page = renderer.openPage(i)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            val model = PdfUiModel(bitmap, i + 1, pageCount)
            output.add(model)
            page.close()
        }
        renderer.close()
        return output
    }

    interface DownloadListener {
        fun onCompleted(path: String?)

        fun onFailed(message: String)

        fun onProgress(progress: Long, totalSize: Long)
    }

}