package io.kn65op.domag2.ui.items

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class DepotViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Items not supported yet"
    }
    val text: LiveData<String> = _text
}