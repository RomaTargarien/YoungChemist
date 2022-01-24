package com.example.youngchemist.ui.screen.main.subjects.lectures.lecture

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.viewpager2.widget.ViewPager2
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentLectureBinding
import com.example.youngchemist.model.ui.LectureUi
import com.example.youngchemist.ui.listeners.OnPageNumberChangedListener
import com.example.youngchemist.ui.listeners.OnUriGetting
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@SuppressLint("SetJavaScriptEnabled")
class LectureFragment : Fragment() {

    private lateinit var binding: FragmentLectureBinding
    private val viewModel: LectureFragmentViewModel by viewModels()
    private lateinit var lecture: LectureUi

    private var lastPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            (it.getParcelable(LECTURE_PARAM) as LectureUi?)?.let {
                lecture = it
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentLectureBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        val adapter = PageAdapter()
        adapter.pages = lecture.data
        binding.vpLecturePages.adapter = adapter
        val size = lecture.data.size
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
        pagesPaginationAdapter.setItems(lecture.data)


        pagesPaginationAdapter.setOnClickListener {
            binding.vpLecturePages.currentItem = it
        }
        binding.bmSheet.viewModel = viewModel
        binding.vpLecturePages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                if (lastPage < position+1) {
                    lastPage = position+1
                }
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
        }

    private fun start3DModelActivity(uri: Uri) {
        Log.d("TAG",uri.toString())
        val intent = Intent(Intent.ACTION_VIEW)
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.setData(uri)
        intent.setPackage("com.google.ar.core")
        startActivity(intent)
    }

    override fun onPause() {
        super.onPause()
        viewModel.saveProgress(lecture.userProgress,lastPage,lecture.data.size)
    }

    companion object {
        private const val LECTURE_PARAM = "lectures.lecture"

        fun newInstance(lecture: LectureUi) =
            LectureFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(LECTURE_PARAM, lecture)
                }
            }
    }
}