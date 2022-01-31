package com.example.youngchemist.ui.screen.main

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.transition.TransitionManager
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentMainBinding
import com.example.youngchemist.service.AchievementService
import com.example.youngchemist.ui.screen.main.achievements.AchievementsFragment
import com.example.youngchemist.ui.screen.main.saved_models.SavedModelsFragment
import com.example.youngchemist.ui.screen.main.subjects.SubjectsFragment
import com.example.youngchemist.ui.screen.main.user.UserFragment
import dagger.hilt.android.AndroidEntryPoint
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener


@AndroidEntryPoint
class MainFragment : Fragment() {

    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainFragmentViewModel by viewModels()
    private var lastSelectedItem: Int? = null
    private var mService: AchievementService? = null
    private var mBound: Boolean = false

    private val connection = object : ServiceConnection {

        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as AchievementService.LocalBinder
            mService = binder.getService()
            showUnViewedAchievementNumber()
            mBound = true
        }

        override fun onServiceDisconnected(arg0: ComponentName) {
            mBound = false
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentMainBinding.inflate(inflater, container, false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.viewModel = viewModel
        binding.bottomNavMenu.background = null
        binding.bottomNavMenu.menu.getItem(2).isEnabled = false
        binding.bottomNavMenu.setOnItemSelectedListener { menuItem ->
            getFragmentForTabId(menuItem.itemId)?.run {
                lastSelectedItem = menuItem.itemId
                if (menuItem.itemId == R.id.user) {
                    TransitionManager.beginDelayedTransition(binding.toolbarContainer)
                    binding.ivExit.isVisible = true
                } else {
                    TransitionManager.beginDelayedTransition(binding.toolbarContainer)
                    binding.ivExit.isVisible = false
                }
                replaceFragment(this); true
            }
            true
        }
        binding.bottomNavMenu.selectedItemId =
            (arguments?.getSerializable(LAST_ITEM) as Int?).also { arguments?.clear() }
                ?: R.id.subjects
        binding.fabQrCode.setOnClickListener {
            viewModel.navigateToScanFragemnt(lastSelectedItem ?: R.id.subjects)
        }
        if (mService == null) {
            bindToService()
        } else {
            showUnViewedAchievementNumber()
        }
        bindToService()
        KeyboardVisibilityEvent.setEventListener(
            requireActivity(),
            object : KeyboardVisibilityEventListener {
                override fun onVisibilityChanged(isOpen: Boolean) {
                    binding.bottomAppBar.isVisible = !isOpen
                }
            })

        viewModel.bottomSheetState.observe(viewLifecycleOwner,{
            val slideOffSet = (1 - (0.5+it/2)).toFloat()
            binding.bottomNavMenu.visibility = if (it.equals(1f)) View.GONE else View.VISIBLE
            binding.fabQrCode.visibility = if (it.equals(1f)) View.GONE else View.VISIBLE
            binding.bottomAppBar.animate().alpha(slideOffSet).setDuration(0).start()
            binding.fabQrCode.animate().alpha(slideOffSet).setDuration(0).start()
        })

        requireActivity().onBackPressedDispatcher.addCallback {
            viewModel.exit()
        }
    }

    private fun showUnViewedAchievementNumber() {
        mService?.doneAchievements?.observe(viewLifecycleOwner, {
            it.count {
                !it.wasViewed
            }.also {
                when (it) {
                    0 -> binding.bottomNavMenu.getBadge(R.id.achievements)
                        ?.apply { isVisible = false; clearNumber() }
                    else -> binding.bottomNavMenu.getOrCreateBadge(R.id.achievements)
                        .apply { isVisible = true; number = it }
                }
            }
        })
    }

    private fun bindToService() {
        Intent(requireContext(), AchievementService::class.java).also { intent ->
            activity?.bindService(intent, connection, Context.BIND_AUTO_CREATE)
        }
    }

    private fun getFragmentForTabId(tabId: Int, qrCodeRawValue: String? = null): Fragment? {
        return when (tabId) {
            R.id.subjects -> {
                SubjectsFragment()
            }
            R.id.achievements -> {
                AchievementsFragment()
            }
            R.id.savedModels -> {
                SavedModelsFragment()
            }
            R.id.user -> {
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d("TAG","fragment main destroyed")
        activity?.unbindService(connection)
    }

    companion object {
        private const val LAST_ITEM = "last_item"

        @JvmStatic
        fun newInstance(lastSelectedItemPosition: Int?) =
            MainFragment().apply {
                arguments = Bundle().apply {
                    putSerializable(LAST_ITEM, lastSelectedItemPosition)
                }
            }
    }
}