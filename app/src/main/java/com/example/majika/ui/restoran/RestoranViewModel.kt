package com.example.majika.ui.restoran

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class RestoranViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is restoran Fragment"
    }
    val text: LiveData<String> = _text
}