package com.example.youngchemist.ui.screen.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentMainBinding
import com.example.youngchemist.ui.screen.main.qr.QrCodeFragment
import com.example.youngchemist.ui.screen.main.stat.StatisticsFragment
import com.example.youngchemist.ui.screen.main.subjects.SubjectsFragment
import com.example.youngchemist.ui.screen.main.user.BottomTabScreen
import com.example.youngchemist.ui.screen.main.user.UserFragment
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainFragment : Fragment(),BottomTabScreen {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainFragmentViewModel by viewModels()



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
        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }
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
                QrCodeFragment.newInstance(qrCodeRawValue)
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
        fun newInstance(qrCodeRawValue: String?) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putString(QR_CODE_RAW_VALUE, qrCodeRawValue)
                }
            }
    }

    override fun navigateToSubjectsScreen() {
        binding.bnvMain.selectedItemId = R.id.subjects
    }

    override fun navigateToStatisticsScreen() {
        binding.bnvMain.selectedItemId = R.id.stat
    }

    override fun navigateToQrCodeScreen() {
        binding.bnvMain.selectedItemId = R.id.qrCode
    }

    override fun navigateToUserScreen() {
        binding.bnvMain.selectedItemId = R.id.person
    }
}