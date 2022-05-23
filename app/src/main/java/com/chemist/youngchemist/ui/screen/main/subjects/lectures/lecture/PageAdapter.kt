package com.chemist.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.chemist.youngchemist.databinding.ItemPageBinding
import com.chemist.youngchemist.ui.listeners.OnUriGetting

@SuppressLint("SetJavaScriptEnabled")
class PageAdapter : RecyclerView.Adapter<PageAdapter.PageViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: String, newItem: String): Boolean {
            return oldItem.toInt() == newItem.toInt()
        }
    }
    private lateinit var listener: OnUriGetting
    fun setOnEventListener(uriGetListner: OnUriGetting) {
        listener = uriGetListner
    }

    private val differ = AsyncListDiffer(this, differCallBack)

    var pages: List<String>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    @SuppressLint("SetJavaScriptEnabled")
    inner class PageViewHolder(val binding: ItemPageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(page: String) {
            binding.wvHtml.settings.javaScriptEnabled = true
            binding.wvHtml.webChromeClient = WebChromeClient()
            binding.wvHtml.addJavascriptInterface(JavaScriptInterface(), "androidImage")
            binding.wvHtml.loadData(page, "text/html", "UTF-8")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PageViewHolder(ItemPageBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: PageViewHolder, position: Int) {
        holder.bind(pages[position])
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