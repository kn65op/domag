package io.github.kn65op.domag.ui.utils

import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.notifyIfNotComputing() {
    if (!isComputingLayout) {
        adapter?.notifyDataSetChanged()
    }
}
