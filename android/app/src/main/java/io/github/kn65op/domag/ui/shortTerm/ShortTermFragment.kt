package io.github.kn65op.domag.ui.shortTerm

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.kn65op.domag.R
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.repository.Repository
import java.time.ZonedDateTime
import javax.inject.Inject

@AndroidEntryPoint
class ShortTermFragment : Fragment() {
    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var dataRepository: Repository
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_short_term, container, false)
        val items = dataRepository.getItemsWithBestBeforeBefore(ZonedDateTime.now().plusDays(7))
        viewAdapter = ShortTermItemAdapter(lifecycleScope, items, db, requireActivity())
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