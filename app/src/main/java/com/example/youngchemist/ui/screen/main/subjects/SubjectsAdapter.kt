package com.example.youngchemist.ui.screen.main.subjects

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.youngchemist.databinding.ItemSubjectBinding
import com.example.youngchemist.model.Subject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import java.io.IOException
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.CoroutineContext

class SubjectsAdapter: RecyclerView.Adapter<SubjectsAdapter.SubjectViewHolder>() {



    private val differCallBack = object : DiffUtil.ItemCallback<Subject>() {
        override fun areItemsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Subject, newItem: Subject): Boolean {
            return oldItem.icon_url == newItem.icon_url
        }

    }

    private val differ = AsyncListDiffer(this,differCallBack)

    var subjects: List<Subject>
         get() = differ.currentList
         set(value) = differ.submitList(value)

    inner class SubjectViewHolder(val binding: ItemSubjectBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Subject) {
            binding.title.setText(item.title)
            binding.ivSubject.load(item.icon_url)
            binding.ivSubject.setOnClickListener {
                onClick?.let { click ->
                    click(item.title)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubjectViewHolder {
       return SubjectViewHolder(ItemSubjectBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: SubjectViewHolder, position: Int) {
        holder.bind(subjects[position])
    }

    private var onClick: ((String) -> Unit)? = null

    fun setOnClickListener(listener: (String) -> Unit) {
        onClick = listener
    }



    override fun getItemCount() = subjects.size
}