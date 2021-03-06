package com.chemist.youngchemist.ui.screen.main.subjects.lectures.lecture_base

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.chemist.youngchemist.model.Test

class StartTestDialogFragment(
    private val viewModel: BaseLaunchTestViewModel,
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