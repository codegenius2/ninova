package com.armutyus.ninova.ui.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ShelvesViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is shelves Fragment"
    }
    val text: LiveData<String> = _text
}