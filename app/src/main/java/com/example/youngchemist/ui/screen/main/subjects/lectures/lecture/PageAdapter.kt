package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.content.res.Resources
import android.os.Build
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemPageBinding
import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.Page
import com.example.youngchemist.ui.util.GLB
import com.example.youngchemist.ui.util.ImageGetter
import java.net.URI
import java.nio.file.Paths

class PageAdapter(private val resoures: Resources) :
    RecyclerView.Adapter<PageAdapter.PageViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<Page>() {
        override fun areItemsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem.data == newItem.data
        }

    }

    private val differ = AsyncListDiffer(this, differCallBack)

    var pages: List<Page>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class PageViewHolder(val binding: ItemPageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(page: Page) {
            binding.wvHtml.loadData(page.data,"text/html", "UTF-8")
            binding.wvHtml.addJavascriptInterface(,"Android")
//            val imageGetter = ImageGetter(resoures, binding.tvHtml)
//            val styledText = HtmlCompat.fromHtml(
//                page.data,
//                HtmlCompat.FROM_HTML_MODE_COMPACT,
//                imageGetter,
//                null
//            )
//            imageClick(styledText as Spannable)
//            binding.tvHtml.text = styledText
//            binding.tvHtml.movementMethod = LinkMovementMethod.getInstance()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PageViewHolder(ItemPageBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position])
    }

    override fun getItemCount() = pages.size

    private var onClick: ((String) -> Unit)? = null
    fun setOnClickListener(listener: (String) -> Unit) {
        onClick = listener
    }

    fun imageClick(html: Spannable) {
        for (span in html.getSpans(0, html.length, ImageSpan::class.java)) {
            val flags = html.getSpanFlags(span)
            val start = html.getSpanStart(span)
            val end = html.getSpanEnd(span)
            html.setSpan(object : URLSpan(span.source) {
                @RequiresApi(Build.VERSION_CODES.O)
                override fun onClick(v: View) {
                    Log.d("TAG", "onClick: url is ${span.source}")
                    val fileName = Paths.get(URI(span.source).path).fileName.toString()
                    val fileNameWithOutExt = fileName.substring(0, fileName.lastIndexOf('.'))
                    onClick?.let { click ->
                        click(fileNameWithOutExt.GLB())
                    }
                }
            }, start, end, flags)
        }
    }
}