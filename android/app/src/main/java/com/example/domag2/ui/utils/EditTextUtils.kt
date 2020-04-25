package com.example.domag2.ui.utils

import android.widget.EditText

fun EditText.replaceText(text: String) {
    this.text.replace(0, this.text.length, text)
}

