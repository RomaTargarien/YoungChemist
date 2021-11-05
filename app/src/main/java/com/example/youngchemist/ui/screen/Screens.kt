package com.example.youngchemist.ui.screen

import com.example.youngchemist.ui.screen.auth.AuthFragment
import com.example.youngchemist.ui.screen.auth.login.LoginFragment
import com.example.youngchemist.ui.screen.auth.register.RegisterFragment
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

}