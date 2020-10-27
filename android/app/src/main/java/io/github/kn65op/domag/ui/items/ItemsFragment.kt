package io.github.kn65op.domag.ui.items

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.kn65op.domag.R
import io.github.kn65op.domag.database.database.AppDatabase
import io.github.kn65op.domag.database.database.DatabaseFactoryImpl
import io.github.kn65op.domag.database.entities.Depot
import io.github.kn65op.domag.database.relations.DepotWithContents
import io.github.kn65op.domag.ui.common.FragmentWithActionBar
import io.github.kn65op.domag.ui.common.prepareFabs
import io.github.kn65op.domag.ui.utils.notifyIfNotComputing

class ItemsFragment : FragmentWithActionBar() {
    private val storedDepotTag = "depotId"

    private val dbFactory = DatabaseFactoryImpl()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private var currentDepot = MutableLiveData<DepotWithContents>()
    private var root = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val depotId = arguments?.getInt(storedDepotTag)

        Log.i(LOG_TAG, "Received depot: $depotId")

        val db = context?.let { dbFactory.factory.createDatabase(it) }
        if (depotId == null || depotId == 0) {
            getRootDepots(db)
        } else {
            getDepot(db, depotId)
            root = false
        }

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
            viewAdapter = DepotAdapter(currentDepot, requireActivity(), viewLifecycleOwner)

            viewManager = LinearLayoutManager(currentContext)
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
            currentDepot.observe(viewLifecycleOwner, {
                val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
                    val action =
                        ItemsFragmentDirections.actionNavItemsSelf(
                            depotId = it.depot.parentId ?: 0
                        )
                    Log.i(LOG_TAG, "${currentDepot.value?.depot?.uid}")
                    root.findNavController().navigate(action)
                }
                callback.isEnabled = it.depot.uid != null

                activity?.runOnUiThread {
                    Log.i(LOG_TAG, "Data has been changed")
                    recyclerView.notifyIfNotComputing()
                }
            })
        } else {
            Log.e(LOG_TAG, "Unable to fill recycler view, no context")
        }
        prepareFabMenu(root)
        setHasOptionsMenu(true)
        return root
    }

    private fun prepareFabMenu(root: View) {
        val addDepotAction = View.OnClickListener {
            Log.i(LOG_TAG, "Clicked add depot")
            val action =
                ItemsFragmentDirections.actionNavItemsToEditContainer(
                    parentId = currentDepot.value?.depot?.uid ?: 0
                )
            Log.i(LOG_TAG, "${currentDepot.value?.depot?.uid}")
            root.findNavController().navigate(action)
        }
        val addItemAction = View.OnClickListener {
            Log.i(LOG_TAG, "Clicked add item")
            val action = ItemsFragmentDirections.actionNavItemsToFragmentEditItem(
                depotId = currentDepot.value?.depot?.uid ?: 0
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

    private fun getDepot(db: AppDatabase?, depotId: Int) {
        val objects = db?.depotDao()?.findWithContentsById(depotId)
        objects?.observe(viewLifecycleOwner, {
            Log.i(LOG_TAG, "Observed objects: $it")
            if (it != null) {
                currentDepot.value = it
                actionBar()?.title = it.depot.name
            }
        })
    }

    private fun getRootDepots(db: AppDatabase?) {
        val rootObjects = db?.depotDao()?.findRootDepots()
        rootObjects?.observe(viewLifecycleOwner, {
            Log.i(LOG_TAG, "Observed root objects: $it")
            if (it != null) {
                currentDepot.value = DepotWithContents(
                    depot = Depot(name = "Items - title"),
                    depots = it
                )
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.items_menu, menu)
        menu.findItem(R.id.items_edit_depot_menu_item).isVisible = !root
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.items_edit_depot_menu_item -> {
                Log.i(LOG_TAG, "Clicked add depot")
                currentDepot.value?.depot?.uid?.let {
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

