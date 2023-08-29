package com.example.kelineyt.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.kelineyt.data.User
import com.example.kelineyt.util.Constant
import com.example.kelineyt.util.RegisterFieldsState
import com.example.kelineyt.util.RegisterValidation
import com.example.kelineyt.util.Resource
import com.example.kelineyt.util.validateEmail
import com.example.kelineyt.util.validatePassword
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {

    private val _register = MutableStateFlow<Resource<User>>(Resource.Unspecified())
    val register: Flow<Resource<User>> = _register

    private val _validation = Channel<RegisterFieldsState>()
    val validation = _validation.receiveAsFlow()

    fun createAccountWithEmailAndPassword(user: User, password: String) {
        val checkValidation = checkValidation(user, password)


        if (checkValidation.first) {
            _register.value = Resource.Loading()
            firebaseAuth.createUserWithEmailAndPassword(user.email, password)
                .addOnSuccessListener {
                    it.user?.let {
                        saveUserInfo(it.uid, user, it)
                    }
                }.addOnFailureListener {
                    _register.value = Resource.Error(it.message.toString())
                }
        } else {
            val registerFieldsState = checkValidation.second
            viewModelScope.launch {
                _validation.send(registerFieldsState)
            }
        }
    }

    private fun saveUserInfo(userUid: String, user: User, firebaseUser: FirebaseUser) {
        db.collection(Constant.USER_COLLECTION)
            .document(userUid)
            .set(user)
            .addOnSuccessListener {
                _register.value = Resource.Success(user)

            }.addOnFailureListener {
                firebaseUser.delete()
                _register.value = Resource.Error(it.message.toString())
            }
    }

    private fun checkValidation(user: User, password: String): Pair<Boolean, RegisterFieldsState> {
        val emailVaidation = validateEmail(user.email)
        val passwordValidation = validatePassword(password)
        return Pair(
            emailVaidation is RegisterValidation.Success && passwordValidation is RegisterValidation.Success,
            RegisterFieldsState(emailVaidation, passwordValidation)
        )
    }
}