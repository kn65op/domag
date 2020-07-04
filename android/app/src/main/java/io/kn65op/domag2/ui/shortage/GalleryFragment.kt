package io.kn65op.domag2.ui.shortage

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.kn65op.domag2.R
import com.kn65op.domag2.ui.common.FragmentWithActionBar
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar

class GalleryFragment : FragmentWithActionBar() {

    private lateinit var galleryViewModel: GalleryViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        galleryViewModel =
            ViewModelProviders.of(this).get(GalleryViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        galleryViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        setHasOptionsMenu(true)
        Log.i("HEHESZKI", "set to true")
        val fab: FloatingActionButton = root.findViewById(R.id.floatingActionButton)
        fab.setOnClickListener { view ->
            Snackbar.make(view, "Inserted new", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        }
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