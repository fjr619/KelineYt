package com.example.kelineyt.fragments.loginregister

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.kelineyt.R
import com.example.kelineyt.data.User
import com.example.kelineyt.databinding.FragmentRegisterBinding
import com.example.kelineyt.util.Resource
import com.example.kelineyt.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

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

        lifecycleScope.launchWhenStarted {
            viewModel.register.collect {
                when(it) {
                    is Resource.Loading -> {
                        binding.buttonRegisterRegister.startAnimation()
                        isEnabledForm(false)
                    }
                    is Resource.Success -> {
                        Log.d("test","success ${it.data.toString()}")
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

    fun isEnabledForm(enable: Boolean) {
        binding.edFirstNameRegister.isEnabled = enable
        binding.edLastNameRegister.isEnabled = enable
        binding.edEmailRegister.isEnabled = enable
        binding.edPasswordRegister.isEnabled = enable
    }
}