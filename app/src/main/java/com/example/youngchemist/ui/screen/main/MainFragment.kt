package com.example.youngchemist.ui.screen.main

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentMainBinding
import com.example.youngchemist.ui.screen.main.qr.qr_code.QrCodeFragment
import com.example.youngchemist.ui.screen.main.stat.StatisticsFragment
import com.example.youngchemist.ui.screen.main.subjects.SubjectsFragment
import com.example.youngchemist.ui.screen.main.user.BottomTabScreen
import com.example.youngchemist.ui.screen.main.user.UserFragment
import com.luseen.spacenavigation.SpaceItem
import com.luseen.spacenavigation.SpaceOnClickListener
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainFragment : Fragment(), BottomTabScreen {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainFragmentViewModel by viewModels()
    private var lastSelectedItem = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentMainBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        createBottomNavMenu()
        createBottomNavMenuItemSelectedListener()
        replaceFragment(getFragmentForTabId(id_subjects)!!)
        checkArguments()

        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }

    }

    private fun createBottomNavMenu() {
        binding.bnvMain.addSpaceItem(SpaceItem("Subject", R.drawable.ic_baseline_burger))
        binding.bnvMain.addSpaceItem(SpaceItem("Stat", R.drawable.ic_baseline_bar_chart))
        binding.bnvMain.addSpaceItem(SpaceItem("Saved", R.drawable.ic_baseline_favorite))
        binding.bnvMain.addSpaceItem(SpaceItem("Person", R.drawable.ic_baseline_person))
        binding.bnvMain.setSpaceBackgroundColor(resources.getColor(R.color.violet))
        binding.bnvMain.showIconOnly()
        binding.bnvMain.setCentreButtonIconColorFilterEnabled(true)
    }

    private fun checkArguments() {
        arguments?.let {
            binding.bnvMain.changeCurrentItem(it.getInt(LAST_ITEM))
            getFragmentForTabId(it.getInt(LAST_ITEM))?.run {
               replaceFragment(this)
           }
        }
    }

    private fun createBottomNavMenuItemSelectedListener() {
        binding.bnvMain.setSpaceOnClickListener(object : SpaceOnClickListener {
            override fun onCentreButtonClick() {
                viewModel.navigateToScanFragemnt(lastSelectedItem)
            }
            override fun onItemClick(itemIndex: Int, itemName: String?) {
                getFragmentForTabId(itemIndex)?.run {
                    replaceFragment(this)
                }
                lastSelectedItem = itemIndex
            }

            override fun onItemReselected(itemIndex: Int, itemName: String?) {}
        })
    }

    private fun getFragmentForTabId(tabId: Int, qrCodeRawValue: String? = null): Fragment? {
        return when (tabId) {
            id_subjects -> {
                SubjectsFragment()
            }
            id_statistics -> {
                StatisticsFragment()
            }
            id_qr_code -> {
                QrCodeFragment()
            }
            id_person -> {
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
        private const val LAST_ITEM = "last_item"
        private const val id_subjects = 0
        private const val id_statistics = 1
        private const val id_qr_code = 2
        private const val id_person = 3

        @JvmStatic
        fun newInstance(lastSelectedItemPosition: Int) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putInt(LAST_ITEM, lastSelectedItemPosition)
                }
            }
    }

    override fun navigateToSubjectsScreen() {
        //binding.bnvMain.selectedItemId = R.id.subjects
    }

    override fun navigateToStatisticsScreen() {
        //binding.bnvMain.selectedItemId = R.id.stat
    }

    override fun navigateToQrCodeScreen() {
        //binding.bnvMain.selectedItemId = R.id.qrCode
    }

    override fun navigateToUserScreen() {
        //binding.bnvMain.selectedItemId = R.id.person
    }
}