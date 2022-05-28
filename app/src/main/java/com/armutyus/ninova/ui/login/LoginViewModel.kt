package com.armutyus.ninova.ui.login

import android.content.Intent
import android.widget.Toast
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.MainActivity
import com.armutyus.ninova.repository.AuthRepositoryInterface
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepositoryInterface
) : ViewModel() {

    private val currentFirebaseUser = MutableLiveData<FirebaseUser?>()
    val currentUser get() = currentFirebaseUser

    fun signInUser(email: String, password: String) = viewModelScope.launch {


    }

}