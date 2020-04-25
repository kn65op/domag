package com.example.domag2.ui.common

import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment

open class FragmentWithActionBar : Fragment() {
    fun actionBar() =
        (activity as AppCompatActivity).supportActionBar
}