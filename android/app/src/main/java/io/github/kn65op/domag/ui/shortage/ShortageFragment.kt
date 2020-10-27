package io.github.kn65op.domag.ui.shortage

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.kn65op.domag.R
import io.github.kn65op.domag.database.database.AppDatabase
import io.github.kn65op.domag.database.database.DatabaseFactoryImpl
import io.github.kn65op.domag.database.filters.filterUnderLimit
import io.github.kn65op.domag.ui.common.FragmentWithActionBar
import io.github.kn65op.domag.ui.utils.notifyIfNotComputing

class ShortageFragment : FragmentWithActionBar() {
    private lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var recyclerAdapter: ShortageCategoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_shortage, container, false)
        recyclerView = root.findViewById(R.id.fragment_shortage_recycler_view)
        recyclerAdapter = ShortageCategoryAdapter(requireActivity(), recyclerView)
        val viewManager = LinearLayoutManager(context)
        setHasOptionsMenu(true)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = recyclerAdapter
        }

        getDataFromDb()

        return root
    }

    private fun getDataFromDb() {
        Log.d(LOG_TAG, "Get all categories from db")
        val dbFactory = DatabaseFactoryImpl()
        db = dbFactory.factory.createDatabase(requireContext())
        db.categoryDao().getAllWithContents()
            .observe(viewLifecycleOwner, {
                Log.d(LOG_TAG, "Observed all categories: ${it.size}")
                recyclerAdapter.dataChanged(it.filterUnderLimit())
                recyclerView.notifyIfNotComputing()
            })
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