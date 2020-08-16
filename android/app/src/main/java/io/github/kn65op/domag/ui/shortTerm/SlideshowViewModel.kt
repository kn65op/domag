package io.github.kn65op.domag.ui.shortTerm

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SlideshowViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Short term not supported yet"
    }
    val text: LiveData<String> = _text
}