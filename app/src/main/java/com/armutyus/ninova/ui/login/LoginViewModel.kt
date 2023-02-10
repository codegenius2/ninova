package com.armutyus.ninova.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.constants.Response
import com.armutyus.ninova.repository.FirebaseRepositoryInterface
import com.google.firebase.auth.AuthCredential
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: FirebaseRepositoryInterface
) : ViewModel() {

    fun signInUser(email: String, password: String, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.signInWithEmailPassword(email, password)
            onComplete(response)
        }

    fun signInAnonymously(onComplete: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        val response = repository.signInAnonymous()
        onComplete(response)
    }

    fun signUpUser(email: String, password: String, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.signUpWithEmailPassword(email, password)
            onComplete(response)
        }

    fun registerAnonymousUser(credential: AuthCredential, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.anonymousToPermanent(credential)
            onComplete(response)
        }

    fun createUser(onComplete: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        val response = repository.createUserInFirestore()
        onComplete(response)
    }

    fun reAuthUserAndChangeEmail(
        credential: AuthCredential,
        email: String,
        onComplete: (Response<Boolean>) -> Unit
    ) =
        viewModelScope.launch {
            val reAuthResponse = repository.reAuthUser(credential)
            if (reAuthResponse is Response.Failure) {
                onComplete(reAuthResponse)
                return@launch
            }

            val emailResponse = repository.changeUserEmail(email)
            if (emailResponse is Response.Failure) {
                onComplete(emailResponse)
                return@launch
            }
            onComplete(Response.Success(true))
        }

    fun reAuthUserAndChangePassword(
        credential: AuthCredential,
        password: String,
        onComplete: (Response<Boolean>) -> Unit
    ) =
        viewModelScope.launch {
            val reAuthResponse = repository.reAuthUser(credential)
            if (reAuthResponse is Response.Failure) {
                onComplete(reAuthResponse)
                return@launch
            }

            val emailResponse = repository.changeUserPassword(password)
            if (emailResponse is Response.Failure) {
                onComplete(emailResponse)
                return@launch
            }
            onComplete(Response.Success(true))
        }

    fun sendPasswordEmail(email: String, onComplete: (Response<Boolean>) -> Unit) =
        viewModelScope.launch {
            val response = repository.sendResetPassword(email)
            onComplete(response)
        }

    fun signOut(onComplete: (Response<Boolean>) -> Unit) = viewModelScope.launch {
        val response = repository.signOut()
        onComplete(response)
    }

}