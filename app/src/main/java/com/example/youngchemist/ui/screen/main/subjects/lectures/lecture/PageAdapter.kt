package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.annotation.SuppressLint
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemPageBinding
import com.example.youngchemist.model.Page
import com.example.youngchemist.ui.listeners.OnPageNumberChangedListener
import com.example.youngchemist.ui.listeners.OnUriGetting

@SuppressLint("SetJavaScriptEnabled")
class PageAdapter : RecyclerView.Adapter<PageAdapter.PageViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<Page>() {
        override fun areItemsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem.data == newItem.data
        }
    }
    private lateinit var listener: OnUriGetting
    fun setOnEventListener(uriGetListner: OnUriGetting) {
        listener = uriGetListner
    }

    private val differ = AsyncListDiffer(this, differCallBack)

    var pages: List<Page>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    @SuppressLint("SetJavaScriptEnabled")
    inner class PageViewHolder(val binding: ItemPageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(page: Page,position: Int) {
            binding.wvHtml.settings.javaScriptEnabled = true
            binding.wvHtml.webChromeClient = WebChromeClient()
            binding.wvHtml.addJavascriptInterface(JavaScriptInterface(),"androidImage")
            binding.wvHtml.loadData(page.data,"text/html", "UTF-8")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PageViewHolder(ItemPageBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position],position)
    }

    override fun getItemCount() = pages.size


    private inner class JavaScriptInterface {
        @JavascriptInterface
        fun get3DImageUrl(uri: String) {
            val string = uri
            listener.onUriGetted(string)
        }
    }
}