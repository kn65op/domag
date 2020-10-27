package io.github.kn65op.domag.ui.utils

import android.util.Log
import androidx.recyclerview.widget.RecyclerView

fun RecyclerView.notifyIfNotComputing() {
    Log.d(LOG_TAG, "Will check")
    if (!isComputingLayout) {
        adapter?.notifyDataSetChanged()
    } else {
        Log.i(LOG_TAG, "View is computing layout")
    }
}

private const val LOG_TAG = "RecyclerViewNotifier"
