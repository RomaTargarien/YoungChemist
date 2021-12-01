package com.example.youngchemist.ui.screen.main.subjects.lectures.test.tests.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.youngchemist.ui.screen.main.subjects.lectures.test.tests.TestFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestNoSaveDialogFragment(private val viewModel: TestFragmentViewModel) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Внимание!")
                .setMessage("Нажимая эту кнопку результат теста не сохранится и вы не сможете пройти его вновь")
                .setPositiveButton("Согласен") { dialog,id ->
                    viewModel.saveTest(true,true)
                }
                .setNegativeButton("Нет! Хочу вернуться") { dialog,id ->

                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}