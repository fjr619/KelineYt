package com.example.kelineyt.viewmodel

import android.content.SharedPreferences
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelineyt.util.Constant.INTRODUCTION_KEY
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class IntroductionViewModel @Inject constructor(
    private val sharedPreferences: SharedPreferences,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _navigate = MutableStateFlow(0)
    val navigate = _navigate.asStateFlow()

    companion object {
        const val SHOPPING_ACTIVITY = 1
        const val ACCOUNT_OPTIONS_FRAGMENT = 2
    }

    init {
        val isButtonClicked = sharedPreferences.getBoolean(INTRODUCTION_KEY, false)
        val user = firebaseAuth.currentUser

        viewModelScope.launch {
            if (user != null) {
                //navigate shopping activity
                _navigate.emit(SHOPPING_ACTIVITY)
            } else if (isButtonClicked) {
                //account options fragment
                _navigate.emit(ACCOUNT_OPTIONS_FRAGMENT)
            } else {
                Unit
            }
        }
    }

    fun startButtonClick() {
        sharedPreferences.edit().putBoolean(INTRODUCTION_KEY, true).apply()
    }
}