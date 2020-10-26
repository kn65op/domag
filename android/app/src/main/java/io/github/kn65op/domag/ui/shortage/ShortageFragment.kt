package io.github.kn65op.domag.ui.shortage

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.RecyclerView
import io.github.kn65op.domag.R
import io.github.kn65op.domag.database.database.DatabaseFactoryImpl
import io.github.kn65op.domag.ui.common.FragmentWithActionBar

class ShortageFragment : FragmentWithActionBar() {
    private val dbFactory = DatabaseFactoryImpl()
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_shortage, container, false)
        recyclerView = root.findViewById(R.id.fragment_shortage_recycler_view)
        setHasOptionsMenu(true)

        getDataFromDb()

        return root
    }

    private fun getDataFromDb() {
        Log.d(LOG_TAG, "Not getting data from db yet")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        Log.i("HEHESZKI", "Menu")
        inflater.inflate(R.menu.shortage, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    companion object {
        private const val LOG_TAG = "ShortageFragment"
    }
}