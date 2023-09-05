package com.example.kelineyt.fragments.loginregister

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.kelineyt.R
import com.example.kelineyt.data.User
import com.example.kelineyt.databinding.FragmentRegisterBinding
import com.example.kelineyt.util.RegisterFieldsState
import com.example.kelineyt.util.RegisterValidation
import com.example.kelineyt.util.Resource
import com.example.kelineyt.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RegisterFragment: Fragment() {

    private lateinit var binding: FragmentRegisterBinding
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentRegisterBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.tvDoYouHaveAccount.setOnClickListener {
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        binding.apply {
            buttonRegisterRegister.setOnClickListener {
                val user = User(
                    edFirstNameRegister.text.toString().trim(),
                    edLastNameRegister.text.toString().trim(),
                    edEmailRegister.text.toString().trim(),
                )

                val password = edPasswordRegister.text.toString()
                viewModel.createAccountWithEmailAndPassword(user, password)
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.register.collect {
                    when(it) {
                        is Resource.Loading -> {
                            binding.buttonRegisterRegister.startAnimation()
                            isEnabledForm(false)
                        }
                        is Resource.Success -> {
                            Log.d("test","success ${it.data?.email}")
                            binding.buttonRegisterRegister.revertAnimation()
                            isEnabledForm(true)
                        }
                        is Resource.Error -> {
                            Log.d("test","error ${it.message.toString()}")
                            binding.buttonRegisterRegister.revertAnimation()
                            isEnabledForm(true)
                        }
                        else -> Unit
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch(Dispatchers.Main) {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.validation.collect { validation ->
                    if (validation.email is RegisterValidation.Failed) {
                            binding.edEmailRegister.apply {
                                requestFocus()
                                error = validation.email.message
                            }
                    }

                    if (validation.password is RegisterValidation.Failed) {
                            binding.edPasswordRegister.apply {
                                requestFocus()
                                error = validation.password.message
                            }
                    }
                }
            }
        }
    }

    fun isEnabledForm(enable: Boolean) {
        binding.edFirstNameRegister.isEnabled = enable
        binding.edLastNameRegister.isEnabled = enable
        binding.edEmailRegister.isEnabled = enable
        binding.edPasswordRegister.isEnabled = enable
    }
}