package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentLecturesListBinding
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.util.ResourceNetwork
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val LECTURE_NAME = "param1"

@AndroidEntryPoint
class LecturesListFragment : Fragment() {

    private lateinit var binding: FragmentLecturesListBinding
    private var param1: String? = null
    private val viewModel: LecturesListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(LECTURE_NAME)
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
        binding.rvLecturesList.layoutManager = LinearLayoutManager(this.requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rvLecturesList.addItemDecoration(SpacesItemVerticalDecoration(10))
        binding.rvLecturesList.adapter = adapter
        param1?.let {
            Log.d("TAG",it)
            viewModel.getAllLectures(it)
        }
        adapter.setOnClickListener {
            viewModel.navigateToLectureScreen(param1!!,it)
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
        fun newInstance(param1: String) =
            LecturesListFragment().apply {
                arguments = Bundle().apply {
                    putString(LECTURE_NAME, param1)
                }
            }
    }
}