package com.chemist.youngchemist.ui.screen

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.chemist.youngchemist.R
import com.chemist.youngchemist.R.id.activity_container
import com.chemist.youngchemist.ui.base.MainNavigator
import com.github.terrakok.cicerone.NavigatorHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import javax.inject.Inject

@FlowPreview
@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModels()
    private val navigator = MainNavigator(this, activity_container)

    @Inject
    lateinit var navigatorHolder: NavigatorHolder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        navigationBarColor()
        window.statusBarColor = resources.getColor(R.color.black)
        viewModel.onActivityCreated()
    }

    override fun onResumeFragments() {
        super.onResumeFragments()
        navigatorHolder.setNavigator(navigator)
    }

    override fun onPause() {
        navigatorHolder.removeNavigator()
        super.onPause()
    }

    private fun navigationBarColor() {
        window.navigationBarColor = resources.getColor(R.color.black)
    }
}