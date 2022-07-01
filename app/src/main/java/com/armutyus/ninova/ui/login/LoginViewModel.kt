package com.armutyus.ninova.ui.login

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import androidx.lifecycle.viewModelScope
import com.armutyus.ninova.repository.AuthRepositoryInterface
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseUser
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepositoryInterface
) : ViewModel() {

    private val currentFirebaseUser = MutableLiveData<FirebaseUser?>()
    val currentUser get() = currentFirebaseUser

    fun signInUser(email: String, password: String) = liveData(Dispatchers.IO) {
        repository.signInWithEmailPassword(email, password).collect { response ->
            emit(response)
        }
    }

    fun signInAnonymously() = liveData(Dispatchers.IO) {
        repository.signInAnonymous().collect { response ->
            emit(response)
        }
    }

    fun signUpUser(email: String, password: String) = liveData(Dispatchers.IO) {
        repository.signUpWithEmailPassword(email, password).collect { response ->
            emit(response)
        }
    }

    fun registerAnonymousUser(credential: AuthCredential) = liveData(Dispatchers.IO) {
        repository.anonymousToPermanent(credential).collect { response ->
            emit(response)
        }
    }

    fun createUser() = liveData(Dispatchers.IO) {
        repository.createUserInFirestore().collect { response ->
            emit(response)
        }
    }

    fun reAuthUser(credential: AuthCredential) = liveData(Dispatchers.IO) {
        repository.reAuthUser(credential).collect { response ->
            emit(response)
        }
    }

    fun changeUserEmail(email: String) = liveData(Dispatchers.IO) {
        repository.changeUserEmail(email).collect { response ->
            emit(response)
        }
    }

    fun changeUserPassword(password: String) = liveData(Dispatchers.IO) {
        repository.changeUserPassword(password).collect { response ->
            emit(response)
        }
    }

    fun sendPasswordEmail(email: String) = liveData(Dispatchers.IO) {
        repository.sendResetPassword(email).collect { response ->
            emit(response)
        }
    }

    fun signOut() = liveData(Dispatchers.IO) {
        repository.signOut().collect { response ->
            emit(response)
        }
    }

    fun getCurrentUser() = viewModelScope.launch {
        val getUser = repository.getCurrentUser()
        currentUser.postValue(getUser)
    }

}