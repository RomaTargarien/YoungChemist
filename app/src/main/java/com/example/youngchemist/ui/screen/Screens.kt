package com.example.youngchemist.ui.screen

import com.example.youngchemist.ui.screen.auth.AuthFragment
import com.example.youngchemist.ui.screen.auth.login.LoginFragment
import com.example.youngchemist.ui.screen.auth.password_restore.RestorePasswordFragment
import com.example.youngchemist.ui.screen.auth.register.RegisterFragment
import com.example.youngchemist.ui.screen.main.MainFragment
import com.example.youngchemist.ui.screen.main.qr.scan.ScanFragment
import com.example.youngchemist.ui.screen.main.subjects.SubjectsFragment
import com.example.youngchemist.ui.screen.main.subjects.lectures.lecture.LectureFragment
import com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list.LecturesListFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    fun authScreen() = FragmentScreen {
        AuthFragment()
    }

    fun loginScreen() = FragmentScreen {
        LoginFragment()
    }

    fun registerScreen() = FragmentScreen {
        RegisterFragment()
    }

    fun mainScreen(qrCodeRawValue: String? = null) = FragmentScreen {
        MainFragment.newInstance(qrCodeRawValue)
    }

    fun lecturesListScreen(subjectName: String) = FragmentScreen {
        LecturesListFragment.newInstance(subjectName)
    }

    fun lectureScreen(lectureTitle: String,subjectTitle: String) = FragmentScreen {
        LectureFragment.newInstance(lectureTitle, subjectTitle)
    }

    fun scanScreen() = FragmentScreen {
        ScanFragment()
    }

    fun restorePasswordScreen() = FragmentScreen {
        RestorePasswordFragment()
    }

}