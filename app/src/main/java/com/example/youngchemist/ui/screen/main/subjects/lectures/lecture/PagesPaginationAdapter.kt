package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.youngchemist.databinding.ItemPageNumberBinding
import com.example.youngchemist.model.Page

class PagesPaginationAdapter :
    RecyclerView.Adapter<PagesPaginationAdapter.PagePaginationViewHolder>() {

    private val differCallBack = object : DiffUtil.ItemCallback<Page>() {
        override fun areItemsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Page, newItem: Page): Boolean {
            return oldItem.data == newItem.data
        }
    }

    private var previousPageNumber: Int = 0
    private val mapBinding: MutableMap<Int, ItemPageNumberBinding> = mutableMapOf()

    private val differ = AsyncListDiffer(this, differCallBack)

    var pages: List<Page>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class PagePaginationViewHolder(val binding: ItemPageNumberBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(position: Int) {
            //binding.pageNumberContainer.alpha = 0.5f
            binding.pageNumberContainer.setBackgroundColor(Color.BLUE)
            //mapBinding[previousPageNumber]?.pageNumberContainer?.alpha = 1f
            mapBinding[previousPageNumber]?.pageNumberContainer?.setBackgroundColor(Color.GREEN)
            binding.tvPageNumber.setText((position + 1).toString())
            binding.pageNumberContainer.setOnClickListener {
                onClick?.let { click ->
                   // binding.pageNumberContainer.alpha = 1f
                    binding.pageNumberContainer.setBackgroundColor(Color.GREEN)
                    //mapBinding[previousPageNumber]?.pageNumberContainer?.alpha = 0.5f
                    mapBinding[previousPageNumber]?.pageNumberContainer?.setBackgroundColor(Color.BLUE)
                    previousPageNumber = position
                    click(position)
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        PagePaginationViewHolder(
            ItemPageNumberBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )


    override fun onBindViewHolder(holder: PagePaginationViewHolder, position: Int) {
        mapBinding[position] = holder.binding
        mapBinding.filterValues { it == holder.binding }.keys
                .toList()
                .filter { it != position }
                .forEach {
                    mapBinding.remove(it)
                }
        holder.bind(position)
    }

    override fun getItemCount() = pages.size

    private var onClick: ((Int) -> Unit)? = null
    fun setOnClickListener(listener: (Int) -> Unit) {
        onClick = listener
    }

}