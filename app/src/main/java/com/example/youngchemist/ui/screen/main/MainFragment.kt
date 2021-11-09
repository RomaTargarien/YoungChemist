package com.example.youngchemist.ui.screen.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentMainBinding
import com.example.youngchemist.ui.screen.main.qr.QrCodeScannerFragment
import com.example.youngchemist.ui.screen.main.stat.StatisticsFragment
import com.example.youngchemist.ui.screen.main.subjects.SubjectsFragment
import com.example.youngchemist.ui.screen.main.user.UserFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentMainBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val qrCodeRawValue = arguments?.getString(QR_CODE_RAW_VALUE)
        if (qrCodeRawValue != null) {
            replaceFragment(getFragmentForTabId(R.id.qrCode, qrCodeRawValue)!!)
            binding.bnvMain.selectedItemId = R.id.qrCode
        } else {
            replaceFragment(getFragmentForTabId(R.id.subjects)!!)
        }
        binding.bnvMain.setOnItemSelectedListener { item ->
            getFragmentForTabId(item.itemId)?.run {
                replaceFragment(this); true
            } ?: false
        }
    }

    private fun getFragmentForTabId(tabId: Int, qrCodeRawValue: String? = null): Fragment? {
        return when (tabId) {
            R.id.subjects -> {
                SubjectsFragment()
            }
            R.id.stat -> {
                StatisticsFragment()
            }
            R.id.qrCode -> {
                QrCodeScannerFragment.newInstance(qrCodeRawValue)
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

    companion object {
        private const val QR_CODE_RAW_VALUE = "qr_raw_value"

        @JvmStatic
        fun newInstance(param1: String?) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(QR_CODE_RAW_VALUE, param1)
                }
            }
    }
}