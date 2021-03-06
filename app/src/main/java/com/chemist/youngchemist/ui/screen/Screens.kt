package com.chemist.youngchemist.ui.screen

import com.chemist.youngchemist.model.Subject
import com.chemist.youngchemist.model.Test
import com.chemist.youngchemist.model.ui.LectureUi
import com.chemist.youngchemist.ui.screen.auth.AuthFragment
import com.chemist.youngchemist.ui.screen.auth.login.LoginFragment
import com.chemist.youngchemist.ui.screen.auth.password_restore.RestorePasswordFragment
import com.chemist.youngchemist.ui.screen.auth.register.RegisterFragment
import com.chemist.youngchemist.ui.screen.main.MainFragment
import com.chemist.youngchemist.ui.screen.main.qr.qr_code.QrCodeFragment
import com.chemist.youngchemist.ui.screen.main.qr.scan.ScanFragment
import com.chemist.youngchemist.ui.screen.main.subjects.lectures.lecture.LectureFragment
import com.chemist.youngchemist.ui.screen.main.subjects.lectures.lectures_list.LecturesListFragment
import com.chemist.youngchemist.ui.screen.main.subjects.lectures.test.result.TestResultFragment
import com.chemist.youngchemist.ui.screen.main.subjects.lectures.test.tests.RootTestFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen
import kotlinx.coroutines.FlowPreview

@FlowPreview
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

    fun mainScreen(lastSelectedItemPosition: Int?) = FragmentScreen {
        MainFragment.newInstance(lastSelectedItemPosition)
    }

    fun lecturesListScreen(subject: Subject) = FragmentScreen {
        LecturesListFragment.newInstance(subject)
    }

    fun lectureScreen(lecture: LectureUi) = FragmentScreen {
        LectureFragment.newInstance(lecture)
    }

    fun scanScreen(lastSelectedAnswerPosition: Int) = FragmentScreen {
        ScanFragment.newInstance(lastSelectedAnswerPosition)
    }

    fun restorePasswordScreen() = FragmentScreen {
        RestorePasswordFragment()
    }

    fun rootTestScreen(test: Test) = FragmentScreen {
        RootTestFragment.newInstance(test)
    }

    fun testResultScreen(mark: Double) = FragmentScreen {
        TestResultFragment.newInstance(mark)
    }

    fun qrCodeFragmnet(result: String,lastSelectedItemPosition: Int) = FragmentScreen {
        QrCodeFragment.newInstance(result,lastSelectedItemPosition)
    }
}