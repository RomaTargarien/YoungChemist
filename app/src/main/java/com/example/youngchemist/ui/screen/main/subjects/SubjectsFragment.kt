package com.example.youngchemist.ui.screen.main.subjects

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentSubjectsBinding
import com.example.youngchemist.model.Subject

class SubjectsFragment : Fragment() {

    private lateinit var binding: FragmentSubjectsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentSubjectsBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val subjects = listOf(
            Subject("Радиохимия", R.drawable.nuclear_chemistry_icon),
            Subject("Органика",R.drawable.organic_chemistry_icon),
            Subject("Неорганика",R.drawable.inorganic_chemistry_icon)
        )
        val adapter = SubjectsAdapter(subjects)

        binding.rvSubjects.layoutManager = GridLayoutManager(this.requireContext(),3)
        binding.rvSubjects.adapter = adapter
    }
}