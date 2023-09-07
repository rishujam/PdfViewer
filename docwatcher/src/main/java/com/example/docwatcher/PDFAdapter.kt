package com.example.docwatcher

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.docwatcher.databinding.DocPageItemBinding
import com.example.docwatcher.model.PdfUiModel

/*
 * Created by Sudhanshu Kumar on 04/09/23.
 */

class PDFAdapter : RecyclerView.Adapter<PDFAdapter.PDFViewHolder>() {

    inner class PDFViewHolder (val binding: DocPageItemBinding): RecyclerView.ViewHolder(binding.root)

    private val differCallback = object : DiffUtil.ItemCallback<PdfUiModel>() {
        override fun areItemsTheSame(oldItem: PdfUiModel, newItem: PdfUiModel): Boolean {
            return oldItem.pageNo == newItem.pageNo
        }

        override fun areContentsTheSame(oldItem: PdfUiModel, newItem: PdfUiModel): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PDFViewHolder {
        return PDFViewHolder(DocPageItemBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: PDFViewHolder, position: Int) {
        val model = differ.currentList[position]
        holder.binding.zoomPdfImage.setImageBitmap(model.bitmap)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}