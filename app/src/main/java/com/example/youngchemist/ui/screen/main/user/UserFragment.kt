package com.example.youngchemist.ui.screen.main.user

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.youngchemist.R
import com.example.youngchemist.databinding.FragmentUserBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener

@AndroidEntryPoint
class UserFragment : Fragment() {

    private val viewModel: UserFragmentViewModel by viewModels()
    private lateinit var binding: FragmentUserBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View =
        FragmentUserBinding.inflate(inflater,container,false).also { binding = it }.root

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.etName.setOnFocusChangeListener { view, b ->
            if (!b) {
                binding.etName.isEnabled = false
                binding.ivName.setImageResource(R.drawable.ic_baseline_edit_24)
            }
        }
        binding.etFamilyName.setOnFocusChangeListener { view, b ->
            if (!b) {
                binding.etFamilyName.isEnabled = false
                binding.ivFamilyName.setImageResource(R.drawable.ic_baseline_edit_24)
            }
        }
        binding.ivName.setOnClickListener {
            binding.etName.isEnabled = true
            binding.etName.setSelection(binding.etName.text.length)
            binding.etName.requestFocus()
            binding.etName.showKeyboard()
            binding.ivName.setImageResource(R.drawable.ic_icon_done)
        }
        binding.ivFamilyName.setOnClickListener {
            binding.etFamilyName.isEnabled = true
            binding.etFamilyName.setSelection(binding.etFamilyName.text.length)
            binding.etFamilyName.requestFocus()
            binding.etFamilyName.showKeyboard()
            binding.ivFamilyName.setImageResource(R.drawable.ic_icon_done)
        }

        val bottomSheet = binding.bmSheet.bottomSheetContainer
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.skipCollapsed = true
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.bmSheet.newPasswordContainer.alpha = 0.5f
        binding.bmSheet.tvChange.alpha = 0.5f
        binding.changePasswordContainer.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        }
        bottomSheetBehavior.addBottomSheetCallback(bottomSheetCallback)
    }

    private val bottomSheetCallback = object : BottomSheetBehavior.BottomSheetCallback() {

        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState.equals(BottomSheetBehavior.STATE_EXPANDED)) {
                Log.d("TAG","exp")
            }
        }

        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            viewModel.onBottomSheetStateChanged(slideOffset)
        }

    }

    private fun initializeBottomSheet() {
        val bottomSheet = binding.bmSheet.bottomSheetContainer
        val bottomSheetBehavior = BottomSheetBehavior.from(bottomSheet)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
    }

    private fun View.showKeyboard() {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
    }
}