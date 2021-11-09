package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentLecturesListBinding
import com.example.youngchemist.ui.screen.Screens
import com.github.terrakok.cicerone.Router
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

private const val LECTURE_NAME = "param1"

@AndroidEntryPoint
class LecturesListFragment : Fragment() {

    private lateinit var binding: FragmentLecturesListBinding
    private var param1: String? = null

    @Inject
   lateinit var router: Router

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
        val list = listOf("Лекция 1","Лекция 2","Лекция 3")
        val adapter = LecturesListAdapter(list)
        binding.rvLecturesList.layoutManager = LinearLayoutManager(this.requireContext(),LinearLayoutManager.VERTICAL,false)
        binding.rvLecturesList.addItemDecoration(SpacesItemVerticalDecoration(10))
        param1?.let {
            if (it.equals("Органика")) {
                binding.rvLecturesList.adapter = adapter
            }
        }
        adapter.setOnClickListener {
            router.navigateTo(Screens.lectureScreen())
        }
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