package io.github.kn65op.domag2.ui.shoppingList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ToolsViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "Shopping list not supported yet"
    }
    val text: LiveData<String> = _text
}