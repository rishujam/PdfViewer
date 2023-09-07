package com.example.docwatcher

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.docwatcher.model.PdfUiModel
import com.example.docwatcher.util.show
import com.example.docwatcher.util.showPagesViewForSomeSeconds
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

    init {
        removeAllViews()
        LayoutInflater.from(context).inflate(R.layout.doc_view, this, true)
        pageText = findViewById(R.id.tvPageNo)
        pageCard = findViewById(R.id.cdPages)
        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        pdfAdapter = PDFAdapter()
        val rv = findViewById<RecyclerView>(R.id.pdfRv)
        rv.apply {
            adapter = pdfAdapter
            layoutManager = LinearLayoutManager(context)
            setOnScrollChangeListener { _, _, _, _, _ ->
                val visiblePosition = (layoutManager as LinearLayoutManager).findFirstCompletelyVisibleItemPosition()
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
    }

    fun setData(uri: String) {
        val list = generateBitmaps(uri)
        noOfPages = list.size.toString()
        pdfAdapter.differ.submitList(list)
        pageCard.show()
        pageCard.showPagesViewForSomeSeconds(coroutineScope, context)
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

}