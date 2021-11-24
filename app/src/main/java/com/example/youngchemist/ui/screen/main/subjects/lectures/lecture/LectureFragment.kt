package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Interpolator
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentLectureBinding
import com.example.youngchemist.ui.listeners.OnPageNumberChangedListener
import com.example.youngchemist.ui.listeners.OnUriGetting
import com.example.youngchemist.ui.util.ResourceNetwork
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.common.io.LineReader
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("SetJavaScriptEnabled")
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
        val adapter = PageAdapter()
        adapter.setOnEventListener(object : OnUriGetting {
            override fun onUriGetted(uri: String) {
                Log.d("TAG",uri)
                val intentUri = Uri.parse("https://arvr.google.com/scene-viewer/1.0")
                    .buildUpon()
                    .appendQueryParameter(
                        "file",
                        uri
                    )
                    .appendQueryParameter("mode","3d_only")
                    .build()
                start3DModelActivity(intentUri)
            }
        })
        val pagesPaginationAdapter = PagesPaginationAdapter()
        binding.bmSheet.rvPagesPagination.setOnEventListener(object : OnPageNumberChangedListener {
            override fun onPageNumberChanged(page: Int) {
                binding.vpLecturePages.currentItem = page
            }
        })
        binding.bmSheet.rvPagesPagination.initialize(pagesPaginationAdapter)
        binding.bmSheet.rvPagesPagination.setViewsToChangeColor(listOf(R.id.list_item_page_number_background))
        binding.viewModel = viewModel

        binding.vpLecturePages.adapter = adapter
        var size = 0
        viewModel.pagesState.observe(viewLifecycleOwner,{
            it.let {
                adapter.pages = it
                pagesPaginationAdapter.setItems(it)
                size = it.size
            }
        })
        pagesPaginationAdapter.setOnClickListener {
            binding.vpLecturePages.currentItem = it
        }
        binding.bmSheet.viewModel = viewModel
        binding.vpLecturePages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                binding.bmSheet.rvPagesPagination.smoothScrollToPosition(position)
                binding.tvPageNumber.text = "${position+1}/$size"
                binding.bnBeginTest.isVisible = (position+1) == size
            }
        })

        val bottomSheet = binding.bmSheet.bottomSheetContainer
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        viewModel.isPaginationVisible.observe(viewLifecycleOwner,{
            bottomSheetBehavior.state = if (it) BottomSheetBehavior.STATE_EXPANDED else BottomSheetBehavior.STATE_COLLAPSED
        })
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                binding.bmSheet.ivLeftArrowUp.animate().rotation(slideOffset*180).setDuration(0).start()
                binding.bmSheet.ivRightArrowUp.animate().rotation(-slideOffset*180).setDuration(0).start()
            }
        })

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