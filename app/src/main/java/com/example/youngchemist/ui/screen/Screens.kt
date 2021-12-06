package com.example.youngchemist.ui.screen

import com.example.youngchemist.model.Lecture
import com.example.youngchemist.model.LectureUi
import com.example.youngchemist.model.Subject
import com.example.youngchemist.ui.screen.auth.AuthFragment
import com.example.youngchemist.ui.screen.auth.login.LoginFragment
import com.example.youngchemist.ui.screen.auth.password_restore.RestorePasswordFragment
import com.example.youngchemist.ui.screen.auth.register.RegisterFragment
import com.example.youngchemist.ui.screen.main.MainFragment
import com.example.youngchemist.ui.screen.main.qr.scan.ScanFragment
import com.example.youngchemist.ui.screen.main.subjects.lectures.lecture.LectureFragment
import com.example.youngchemist.ui.screen.main.subjects.lectures.lectures_list.LecturesListFragment
import com.example.youngchemist.ui.screen.main.subjects.lectures.test.tests.RootTestFragment
import com.example.youngchemist.ui.screen.main.subjects.lectures.test.tests.TestFragment
import com.example.youngchemist.ui.screen.main.subjects.lectures.test.result.TestResultFragment
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

    fun lecturesListScreen(subject: Subject) = FragmentScreen {
        LecturesListFragment.newInstance(subject)
    }

    fun lectureScreen(lecture: LectureUi) = FragmentScreen {
        LectureFragment.newInstance(lecture)
    }

    fun scanScreen() = FragmentScreen {
        ScanFragment()
    }

    fun restorePasswordScreen() = FragmentScreen {
        RestorePasswordFragment()
    }

    fun rootTestScreen(testId: String) = FragmentScreen {
        RootTestFragment.newInstance(testId)
    }

    fun testResultScreen(mark: Double) = FragmentScreen {
        TestResultFragment.newInstance(mark)
    }

}