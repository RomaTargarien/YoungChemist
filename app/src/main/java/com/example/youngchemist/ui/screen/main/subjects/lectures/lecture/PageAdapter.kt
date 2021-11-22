package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemPageBinding
import com.example.youngchemist.model.Page

class PageAdapter : RecyclerView.Adapter<PageAdapter.PageViewHolder>() {

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
//            binding.wvHtml.addJavascriptInterface(,"Android")
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
}