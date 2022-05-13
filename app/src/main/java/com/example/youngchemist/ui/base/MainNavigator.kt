package com.example.youngchemist.ui.base

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.example.youngchemist.R
import com.example.youngchemist.ui.screen.auth.AuthFragment
import com.github.terrakok.cicerone.androidx.AppNavigator
import kotlinx.coroutines.FlowPreview

@FlowPreview
class MainNavigator(activity: FragmentActivity, containerId: Int) :
    AppNavigator(activity, containerId) {
    override fun setupFragmentTransaction(
        fragmentTransaction: FragmentTransaction,
        currentFragment: Fragment?,
        nextFragment: Fragment?
    ) {
        when {
            currentFragment == null && nextFragment is AuthFragment -> { }
            else -> fragmentTransaction.setCustomAnimations(
                R.anim.nav_default_enter_anim,
                R.anim.nav_default_exit_anim,
                R.anim.nav_default_pop_enter_anim,
                R.anim.nav_default_pop_exit_anim
            )
        }
    }
}