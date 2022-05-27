package com.armutyus.ninova.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.armutyus.ninova.repository.AuthRepositoryInterface
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository : AuthRepositoryInterface
) : ViewModel() {

    private val currentFirebaseUser = MutableLiveData<FirebaseUser?>()
    val currentUser get() = currentFirebaseUser

    //Pass firebase works to here

}