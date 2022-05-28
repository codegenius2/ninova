package com.armutyus.ninova.ui.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.liveData
import com.armutyus.ninova.repository.AuthRepositoryInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: AuthRepositoryInterface
) : ViewModel() {

    fun signInUser(email: String, password: String) = liveData(Dispatchers.IO) {
        repository.signInWithEmailPassword(email, password).collect() { response ->
            emit(response)
        }
    }

}