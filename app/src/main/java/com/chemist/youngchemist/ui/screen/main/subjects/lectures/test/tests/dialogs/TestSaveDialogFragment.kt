package com.chemist.youngchemist.ui.screen.main.subjects.lectures.test.tests.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import com.chemist.youngchemist.ui.screen.main.subjects.lectures.test.tests.TestFragmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TestSaveDialogFragment(private val viewModel: TestFragmentViewModel) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            Log.d("TAG",viewModel.toString())
            builder.setTitle("Готово")
                .setMessage("Вы уверены что хотите завершить тест?")
                .setPositiveButton("Да") { dialog,id ->
                    viewModel.saveTest(true,false)
                }
                .setNegativeButton("Нет") { dialog,id ->

                }
            builder.create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }
}