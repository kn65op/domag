package io.github.kn65op.domag.ui.shortage

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class GalleryViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Items in shortage not supported yet"
    }
    val text: LiveData<String> = _text
}