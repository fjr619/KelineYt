package com.example.kelineyt.dialog

import androidx.fragment.app.Fragment
import com.example.kelineyt.R
import com.example.kelineyt.databinding.DialogResetPasswordBinding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

fun Fragment.setupBottomSheetDialog(
    onSendClick: (String) -> Unit
) {
    val dialog = BottomSheetDialog(requireContext(), R.style.DialogStyle)
    val binding = DialogResetPasswordBinding.inflate(layoutInflater, null, false)
    dialog.setContentView(binding.root)
    dialog.behavior.state = BottomSheetBehavior.STATE_EXPANDED
    dialog.show()

    binding.buttonSendResetPassword.setOnClickListener {
        val email = binding.edResetPassword.text.toString().trim()
        onSendClick(email)
        dialog.dismiss()
    }

    binding.buttonCancelResetPassword.setOnClickListener {
        dialog.dismiss()
    }
}