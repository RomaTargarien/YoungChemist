package com.example.youngchemist.ui.screen.main.subjects.lectures.test

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestNoSaveDialogFragment : DialogFragment() {

    private val viewModel: TestFragmentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            Log.d("TAG",viewModel.toString())
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Внимание!")
                .setMessage("Нажимая эту кнопку результат теста не сохранится и вы не сможете пройти его вновь")
                .setPositiveButton("Согласен") { dialog,id ->
                    viewModel.exit()
                }
                .setNegativeButton("Нет! Хочу вернуться") { dialog,id ->

                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}