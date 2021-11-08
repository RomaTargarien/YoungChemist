package com.example.youngchemist.ui.screen.main

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentMainBinding
import com.example.youngchemist.ui.screen.Screens
import com.example.youngchemist.ui.screen.main.qr.QrCodeScannerFragment
import com.example.youngchemist.ui.screen.main.stat.StatisticsFragment
import com.example.youngchemist.ui.screen.main.subjects.SubjectsFragment
import com.example.youngchemist.ui.screen.main.user.UserFragment
import com.github.terrakok.cicerone.Router
import com.github.terrakok.cicerone.Screen
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentMainBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        replaceFragment(getFragmentForTabId(R.id.subjects)!!)
        Log.d("TAG",binding.bnvMain.isSelected.toString())
        binding.bnvMain.setOnItemSelectedListener { item ->
            getFragmentForTabId(item.itemId)?.run {
                replaceFragment(this); true
            } ?: false
        }
    }

    private fun getFragmentForTabId(tabId: Int): Fragment? {
        return when (tabId) {
            R.id.subjects -> {
                SubjectsFragment()
            }
            R.id.stat -> {
                StatisticsFragment()
            }
            R.id.qrCode -> {
                QrCodeScannerFragment()
            }
            R.id.person -> {
                UserFragment()
            }
            else -> null
        }
    }

    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction().setCustomAnimations(
            R.anim.nav_default_enter_anim,
            R.anim.nav_default_exit_anim,
            R.anim.nav_default_pop_enter_anim,
            R.anim.nav_default_pop_exit_anim
        ).replace(R.id.tab_content, fragment).commit()
    }

}