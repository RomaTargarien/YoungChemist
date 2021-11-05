package com.example.youngchemist.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.example.youngchemist.R
import com.example.youngchemist.ui.screen.auth.AuthFragment
import com.example.youngchemist.ui.screen.auth.login.LoginFragment
import com.example.youngchemist.ui.screen.auth.register.RegisterFragment
import com.github.terrakok.cicerone.androidx.AppNavigator

class MainNavigator(activity: FragmentActivity, containerId: Int) :
    AppNavigator(activity, containerId) {
    override fun setupFragmentTransaction(
        fragmentTransaction: FragmentTransaction,
        currentFragment: Fragment?,
        nextFragment: Fragment?
    ) {
        when {
            currentFragment == null && nextFragment is AuthFragment -> {
            }
            nextFragment is LoginFragment || nextFragment is RegisterFragment -> {
                fragmentTransaction.setCustomAnimations(
                    R.anim.enter_anim,
                    R.anim.exit_anim,
                    R.anim.pop_enter_anim,
                    R.anim.pop_exit_anim
                )
            }
            else -> {}

        }
    }
}