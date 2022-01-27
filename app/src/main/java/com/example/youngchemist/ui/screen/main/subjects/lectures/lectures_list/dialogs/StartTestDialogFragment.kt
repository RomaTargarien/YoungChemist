package com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.example.youngchemist.model.Test
import com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list.LecturesListViewModel

class StartTestDialogFragment(
    private val viewModel: LecturesListViewModel,
    val test: Test
) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return activity?.let {
            val builder = AlertDialog.Builder(it)
            builder.setTitle("Внимание!")
                .setMessage("Тест можно пройти лишь один раз. Вы уверены что хотите начать?")
                .setPositiveButton("Да") { _, _ -> viewModel.navigateToTestScreen(test) }
                .setNegativeButton("Нет") { _, _ -> }
                .create()
        } ?: throw IllegalStateException("Activity cannot be null")
    }

}