package com.example.youngchemist.ui.screen.main.subjects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentSubjectsBinding
import com.example.youngchemist.model.Subject
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SubjectsFragment : Fragment() {

    private lateinit var binding: FragmentSubjectsBinding
    private val viewModel: SubjectsFragmentViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentSubjectsBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val subjects = listOf(
            Subject("Радиохимия", R.drawable.ic_icon_nuclear_chemistry),
            Subject("Органика",R.drawable.ic_icon_organic_chemistry),
            Subject("Неорганика",R.drawable.ic_icon_inorganic_chemistry)
        )
        val adapter = SubjectsAdapter(subjects)

        binding.rvSubjects.layoutManager = GridLayoutManager(this.requireContext(),3)
        binding.rvSubjects.adapter = adapter
        adapter.setOnClickListener {
            viewModel.navigateToLecturesListScreen(it)
        }
    }
}