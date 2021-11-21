package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
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
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.youngchemist.databinding.FragmentLectureBinding
import com.example.youngchemist.ui.util.GLB
import com.example.youngchemist.ui.util.ImageGetter
import com.example.youngchemist.ui.util.ResourceNetwork
import dagger.hilt.android.AndroidEntryPoint
import java.net.URI
import java.nio.file.Paths
import kotlin.io.path.name

@AndroidEntryPoint
class LectureFragment : Fragment() {

    private lateinit var binding: FragmentLectureBinding
    private val viewModel: LectureFragmentViewModel by viewModels()
    private var lectureTitle: String? = null
    private var subjectTitle: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            lectureTitle = it.getString(LECTURE_TITLE)
            subjectTitle = it.getString(SUBJECT_TITLE)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentLectureBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.getContent(subjectTitle!!,lectureTitle!!)
        val adapter = PageAdapter(resources)
        binding.vpLecturePages.adapter = adapter
        viewModel.pagesState.observe(viewLifecycleOwner,{
            it.let {
                adapter.pages = it
            }
        })
        adapter.setOnClickListener {
            viewModel.get3DModelUri(it)
        }

        viewModel.uriState.observe(viewLifecycleOwner,{
            when (it) {
                is ResourceNetwork.Loading -> {

                }
                is ResourceNetwork.Success -> {
                    it.data?.let {
                        start3DModelActivity(it)
                    }

                }
                is ResourceNetwork.Error -> {

                }
            }
        })
        }

    private fun start3DModelActivity(uri: Uri) {
        Log.d("TAG",uri.toString())
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setData(uri)
        intent.setPackage("com.google.ar.core")
        startActivity(intent)
    }

    companion object {
        private const val LECTURE_TITLE = "param1"
        private const val SUBJECT_TITLE = "param2"

        fun newInstance(lectureTitle: String, subjectTitle: String) =
            LectureFragment().apply {
                arguments = Bundle().apply {
                    putString(LECTURE_TITLE, lectureTitle)
                    putString(SUBJECT_TITLE, subjectTitle)
                }
            }
    }
}