package io.github.kn65op.domag.ui.items

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.kn65op.domag.R
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.model.Depot
import io.github.kn65op.domag.data.operations.getRootDepots
import io.github.kn65op.domag.data.repository.Repository
import io.github.kn65op.domag.ui.common.FragmentWithActionBar
import io.github.kn65op.domag.ui.common.prepareFabs
import io.github.kn65op.domag.ui.utils.notifyIfNotComputing
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ItemsFragment : FragmentWithActionBar() {
    private val storedDepotTag = "depotId"

    @Inject
    lateinit var db: AppDatabase

    @Inject
    lateinit var dataRepository: Repository

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var currentDepotFlow: Flow<Depot?>
    private var currentDepot: Depot? = null
    private var isRootDepot = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val depotId = arguments?.getInt(storedDepotTag)

        Log.i(LOG_TAG, "Received depot: $depotId")

        currentDepotFlow = if (depotId == null || depotId == 0) {
            Log.i("KOT", "10")
            getRootDepots(dataRepository)
        } else {
            isRootDepot = false
            getDepot(depotId)
        }
        Log.i("KOT", "11")

        return prepareView(inflater, container)
    }

    private fun prepareView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View {
        val root = inflater.inflate(R.layout.fragment_items, container, false)
        val currentContext = context
        if (currentContext != null) {
            recyclerView = root.findViewById(R.id.items_recycler_view)
            viewAdapter = DepotAdapter(
                currentDepotFlow,
                requireActivity(),
                lifecycleScope,
                viewLifecycleOwner,
                db
            )

            viewManager = LinearLayoutManager(currentContext)
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
        } else {
            Log.e(LOG_TAG, "Unable to fill recycler view, no context")
        }
        lifecycleScope.launch {
            currentDepotFlow.collect { observeDepotChanges(it, root) }
        }
        prepareFabMenu(root)
        setHasOptionsMenu(true)
        return root
    }

    private fun observeDepotChanges(
        depot: Depot?,
        root: View
    ) {
        currentDepot = depot
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            val action =
                ItemsFragmentDirections.actionNavItemsSelf(
                    depotId = currentDepot?.parentId ?: 0
                )
            Log.i(LOG_TAG, "${currentDepot?.uid}")
            root.findNavController().navigate(action)
        }
        callback.isEnabled = currentDepot?.uid != null

        activity?.runOnUiThread {
            Log.i(LOG_TAG, "Data has been changed")
            recyclerView.notifyIfNotComputing()
        }

        if (!isRootDepot) actionBar()?.title = depot?.name
    }

    private fun prepareFabMenu(root: View) {
        val addDepotAction = View.OnClickListener {
            Log.i(LOG_TAG, "Clicked add depot")
            val action =
                ItemsFragmentDirections.actionNavItemsToEditContainer(
                    parentId = currentDepot?.uid ?: 0
                )
            Log.i(LOG_TAG, "${currentDepot?.uid}")
            root.findNavController().navigate(action)
        }
        val addItemAction = View.OnClickListener {
            Log.i(LOG_TAG, "Clicked add item")
            val action = ItemsFragmentDirections.actionNavItemsToFragmentEditItem(
                depotId = currentDepot?.uid ?: 0
            )
            root.findNavController().navigate(action)
        }
        val addCategoryAction = View.OnClickListener {
            Log.i(LOG_TAG, "Clicked add category")
            val action = ItemsFragmentDirections.actionNavItemsToEditCategory()
            root.findNavController().navigate(action)
        }

        prepareFabs(
            root,
            addDepotAction,
            addItemAction,
            addCategoryAction,
            R.id.items_fab_background,
        )
    }

    private fun getDepot(depotId: Int) =
        dataRepository.getDepot(depotId)

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.items_menu, menu)
        menu.findItem(R.id.items_edit_depot_menu_item).isVisible = !isRootDepot
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.items_edit_depot_menu_item -> {
                Log.i(LOG_TAG, "Clicked add depot")
                currentDepot?.uid?.let {
                    val action = ItemsFragmentDirections.actionNavItemsToEditContainer(
                        depotId = it
                    )
                    Log.i(LOG_TAG, "Edit $it")
                    view?.findNavController()?.navigate(action)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    companion object {
        private const val LOG_TAG = "ItemsFragment"
    }
}

