package io.github.kn65op.domag.ui.shortTerm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.kn65op.domag.R
import io.github.kn65op.domag.database.database.DatabaseFactoryImpl
import java.time.ZonedDateTime

class ShortTermFragment : Fragment() {
    private val dbFactory = DatabaseFactoryImpl()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_short_term, container, false)
        val db = context?.let { dbFactory.factory.createDatabase(it) }
        val data = db?.itemDao()?.getWithBestBeforeBefore(ZonedDateTime.now().plusDays(7))
        viewAdapter = ShortTermItemAdapter(viewLifecycleOwner, data, db, requireActivity())
        viewManager = LinearLayoutManager(context)
        recyclerView = root.findViewById(R.id.short_term_items_recycler_view)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

        return root
    }
}