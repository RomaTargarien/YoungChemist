package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Spannable
import android.text.method.LinkMovementMethod
import android.text.style.ImageSpan
import android.text.style.URLSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.example.youngchemist.databinding.FragmentLectureBinding
import com.example.youngchemist.ui.util.ImageGetter
import com.example.youngchemist.ui.util.ResourceNetwork
import dagger.hilt.android.AndroidEntryPoint

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
        viewModel.contentState.observe(viewLifecycleOwner, Observer {
            when (it) {
                is ResourceNetwork.Loading -> {

                }
                is ResourceNetwork.Success -> {
                    it.data?.let {
                        val imageGetter = ImageGetter(resources,binding.tvHtml)
                        val styledText = HtmlCompat.fromHtml(
                            it.data[0],    //Instead of copying pasting, I kept it as a string
                            HtmlCompat.FROM_HTML_MODE_LEGACY,
                            imageGetter,
                            null
                        )
                        imageClick(styledText as Spannable)
                        binding.tvHtml.text = styledText
                        binding.tvHtml.movementMethod = LinkMovementMethod.getInstance()
                    }


                }
                is ResourceNetwork.Error -> {

                }
            }
        })

//        binding.ivCh4.setOnClickListener {
//            val intent = Intent(Intent.ACTION_VIEW)
//            val uri = Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
//                .appendQueryParameter(
//                    "file",
//                    "https://firebasestorage.googleapis.com/v0/b/youngchemist-c52a2.appspot.com/o/carbon_red-2.glb?alt=media&token=02c2a897-c722-4e04-a63c-096129c5cd2b"
//                )
//                .appendQueryParameter("mode", "3d_only")
//                .build()
//            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//            intent.setData(uri)
//            intent.setPackage("com.google.ar.core")
//            startActivity(intent)
//            try {
//                val model = storage.getReference("carbon_red-2.glb")
//                model.downloadUrl.addOnSuccessListener {
//                    val intent = Intent(Intent.ACTION_VIEW)
//                    Log.d("TAG",it.toString())
//                    val uri = Uri.parse("https://arvr.google.com/scene-viewer/1.0").buildUpon()
//                        .appendQueryParameter(
//                            "file",
//                            "https://firebasestorage.googleapis.com/v0/b/youngchemist-c52a2.appspot.com/o/carbon_red-2.glb?alt=media&token=02c2a897-c722-4e04-a63c-096129c5cd2b"
//                        )
//                        .appendQueryParameter("mode", "3d_only")
//                        .build()
//                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                    intent.setData(uri)
//                    intent.setPackage("com.google.ar.core")
//                    startActivity(intent)
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
        }

    private fun start3DModelActivity(uri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setData(uri)
        intent.setPackage("com.google.ar.core")
        startActivity(intent)
    }

    fun imageClick(html: Spannable) {
        for (span in html.getSpans(0, html.length, ImageSpan::class.java)) {
            val flags = html.getSpanFlags(span)
            val start = html.getSpanStart(span)
            val end = html.getSpanEnd(span)
            html.setSpan(object : URLSpan(span.source) {
                override fun onClick(v: View) {
                    Log.d("TAG", "onClick: url is ${span.source}")
                }
            }, start, end, flags)
        }
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