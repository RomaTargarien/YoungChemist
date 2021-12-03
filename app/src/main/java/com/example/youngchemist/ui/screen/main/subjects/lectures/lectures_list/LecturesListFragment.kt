package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentLecturesListBinding
import com.example.youngchemist.model.Subject
import com.example.youngchemist.ui.listeners.OnPageNumberChangedListener
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.screen.main.subjects.lectures.lecture.PagesPaginationAdapter
import com.example.youngchemist.ui.screen.main.subjects.lectures.test.HorizontalItemDecoration
import com.example.youngchemist.ui.util.BitmapUtils
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val SUBJECT = "param1"

@AndroidEntryPoint
class LecturesListFragment : Fragment() {

    private lateinit var binding: FragmentLecturesListBinding
    private var param1: Subject? = null
    private val viewModel: LecturesListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getParcelable(SUBJECT)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentLecturesListBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val adapter = LecturesListAdapter()
        binding.viewModel = viewModel
        binding.rvLectures.layoutManager = GridLayoutManager(requireContext(),2)
        binding.rvLectures.adapter = adapter
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }
        param1?.let {
            val bitmap = BitmapUtils.convertCompressedByteArrayToBitmap(it.iconByteArray)
            binding.ivSubject.setImageBitmap(bitmap)
            binding.textView6.text = it.title
        }
        adapter.setOnClickListener {
            viewModel.navigateToLectureScreen(it)
        }
        adapter.setOnBeginTestListener {
            Log.d("TAG",it)
            viewModel.navigateToTestScreen(it)
        }

        param1?.let {
            Log.d("TAG",it.toString())
            viewModel.getAllLectures(it.collectionId)
        }
        viewModel.lecturesListState.observe(viewLifecycleOwner,{
            when (it) {
                is ResourceNetwork.Loading -> {
                    binding.progressFlask.isVisible = true
                }
                is ResourceNetwork.Success -> {
                    binding.progressFlask.isVisible = false
                    it.data?.let {
                        adapter.lectures = it
                    }

                }
                is ResourceNetwork.Error -> {

                }
            }
        })
    }

    companion object {
        @JvmStatic
        fun newInstance(subject: Subject) =
            LecturesListFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(SUBJECT,subject)
                }
            }
    }
}