package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemPageBinding
import com.example.youngchemist.databinding.ItemPageNumberBinding
import com.example.youngchemist.model.Page

class PagesPaginationAdapter: RecyclerView.Adapter<PagesPaginationAdapter.PagePaginationViewHolder>() {

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

    inner class PagePaginationViewHolder(val binding: ItemPageNumberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            binding.tvPageNumber.setText((position+1).toString())
            binding.pageNumberContainer.setOnClickListener {
                onClick?.let { click ->
                    click(position)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PagePaginationViewHolder(ItemPageNumberBinding.inflate(LayoutInflater.from(parent.context), parent, false))


    override fun onBindViewHolder(holder: PagePaginationViewHolder, position: Int) {
        holder.bind(position)
    }

    override fun getItemCount() = pages.size

    private var onClick: ((Int) -> Unit)? = null
    fun setOnClickListener(listener: (Int) -> Unit) {
        onClick = listener
    }

}