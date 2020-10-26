package io.github.kn65op.domag.ui.shortage

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import io.github.kn65op.domag.R
import io.github.kn65op.domag.ui.common.FragmentWithActionBar

class ShortageFragment : FragmentWithActionBar() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_shrotage, container, false)
        setHasOptionsMenu(true)
        Log.i("HEHESZKI", "set to true")
        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.i("HEHESZKI", "Menu")
        inflater.inflate(R.menu.shortage, menu)
        activity?.title = "GOOD TITLE"
        activity?.setShowWhenLocked(true)
        actionBar()?.title = "WORKS?"
        activity?.actionBar?.title = "ASDSAD"
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onDestroy() {
        super.onDestroy()
        activity?.setShowWhenLocked(false)
    }
}