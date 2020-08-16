package io.github.kn65op.domag.ui.depot

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import io.github.kn65op.domag.R
import io.github.kn65op.domag.database.database.AppDatabase
import io.github.kn65op.domag.database.database.DatabaseFactoryImpl
import io.github.kn65op.domag.database.entities.Depot
import io.github.kn65op.domag.database.operations.deleteDepot
import io.github.kn65op.domag.database.relations.DepotWithContents
import io.github.kn65op.domag.ui.common.FragmentWithActionBar
import io.github.kn65op.domag.ui.items.DepotAdapter
import io.github.kn65op.domag.ui.utils.replaceText
import kotlinx.android.synthetic.main.fragment_edit_depot.*
import kotlinx.coroutines.launch

private const val CURRENT_DEPOT_NAME_PARAMETER = "currentName"
private const val PARENT_ID_PARAMETER = "currentParent"
private const val PARENT_ID_ACTION_PARAMETER = "parentId"
private const val CURRENT_DEPOT_ID_PARAMETER = "depotId"

class EditDepotFragment : FragmentWithActionBar(), AdapterView.OnItemSelectedListener {
    private val dbFactory = DatabaseFactoryImpl()
    private var currentName: String? = null
    private var currentParent: Int? = null
    private var depotId: Int? = null
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: SearchableSpinner
    private var currentDepot = MutableLiveData<DepotWithContents>()
    private var allDepots = emptyList<Depot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentName = savedInstanceState?.getString(CURRENT_DEPOT_NAME_PARAMETER)
        currentParent = savedInstanceState?.getInt(PARENT_ID_PARAMETER)
        if (currentParent == null)
            currentParent = arguments?.getInt(PARENT_ID_ACTION_PARAMETER)
        depotId = arguments?.getInt(CURRENT_DEPOT_ID_PARAMETER)
        Log.i(LOG_TAG, "Passed depotId: $depotId")
        val savedId = savedInstanceState?.getInt(CURRENT_DEPOT_ID_PARAMETER)
        savedId?.let {
            if (it != 0) {
                depotId = it
            }
        }
        Log.i(LOG_TAG, "Using depotId: $depotId")
        Log.i(LOG_TAG, "Editing depot: $depotId: $currentName with parent: $currentParent")
    }

    private fun readDepot() {
        depotId?.let { searchDepotId ->
            if (searchDepotId != 0) {
                context?.let { context ->
                    val db = dbFactory.factory.createDatabase(context)
                    db.depotDao().findWithContentsById(searchDepotId)
                        .observe(viewLifecycleOwner, Observer {
                            if (it != null) {
                                currentDepot.value = it
                                currentParent = it.depot.parentId
                                Log.i(
                                    LOG_TAG,
                                    "Editing depot: ${it.depot.uid}: ${it.depot.name} with parent: ${it.depot.parentId}"
                                )
                            }
                        })
                }
            }
        }
    }

    private fun getAllDepots(db: AppDatabase?) {
        val rootObjects = db?.depotDao()?.getAll()
        rootObjects?.observe(viewLifecycleOwner, Observer { depots ->
            if (depots != null) {
                allDepots = depots
                Log.i(LOG_TAG, "Observed all objects: ${allDepots.size}")
                val parents = ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item)
                parents.add(context?.getString(R.string.edit_depot_parent_select_no_parent))
                parents.addAll(depots.filter { it.uid != depotId }.map { it.name })
                spinner.adapter = parents
                Log.i(LOG_TAG, "AllDepots: ${allDepots.size}")
                val parentDepotPosition =
                    allDepots.indexOfFirst {
                        Log.i(LOG_TAG, "${it.uid} ? ${currentParent})")
                        it.uid == currentParent
                    } + PARENT_SHIFT
                Log.i(LOG_TAG, "Index $parentDepotPosition")
                spinner.setSelection(parentDepotPosition)
                spinner.onItemSelectedListener = this
            }
        })
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_depot, container, false)

        readDepot()

        spinner = root.findViewById(R.id.edit_depot_fragment_parent_spinner)
        recyclerView = root.findViewById(R.id.edit_depot_content_view)

        spinner.setTitle(context?.getString(R.string.edit_depot_parent_text))
        spinner.setPositiveButton(context?.getString(R.string.spinner_select_text))

        viewManager = LinearLayoutManager(context)
        viewAdapter = DepotAdapter(currentDepot, context!!, this)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        currentDepot.observe(this, Observer {
            activity?.runOnUiThread {
                if (!recyclerView.isComputingLayout) {
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                currentName = it.depot.name
                currentName?.let {
                    edit_depot_depot_name.replaceText(it)
                }
            }
        })

        val db = context?.let { dbFactory.factory.createDatabase(it) }
        getAllDepots(db)

        setHasOptionsMenu(true)
        actionBar()?.setHomeAsUpIndicator(R.drawable.ic_cancel_small)

        return root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_depot_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.edit_depot_menu_confirm -> {
            val name = edit_depot_depot_name.text.toString()
            if (name.isNotEmpty()) {
                val db = context?.let { it1 -> dbFactory.factory.createDatabase(it1) }
                val dao = db?.depotDao()
                if (currentDepot.value == null) {
                    lifecycleScope.launch {
                        Log.i(LOG_TAG, "Insert $name with parent: $currentParent")
                        dao?.insert(Depot(name = name, parentId = currentParent))
                        Log.i(LOG_TAG, "Inserted $name")
                    }
                } else {
                    lifecycleScope.launch {
                        Log.i(LOG_TAG, "Edit $name")
                        currentDepot.value?.depot?.let {
                            val depot = Depot(uid = it.uid, name = name, parentId = currentParent)
                            dao?.update(depot)
                        }
                        Log.i(LOG_TAG, "Edited $name")
                    }
                }
            }
            activity?.onBackPressed()
            true
        }
        R.id.edit_depot_menu_remove_depot_item -> {
            val db = context?.let { it1 -> dbFactory.factory.createDatabase(it1) }
            currentDepot.value?.let {
                val parent = it.depot.parentId
                lifecycleScope.launch {
                    db?.deleteDepot(it)
                }
                val action =
                    EditDepotFragmentDirections.actionEditContainerToNavItems(
                        parent ?: 0
                    )
                Log.i(LOG_TAG, "$view will navigate to $parent")
                view?.findNavController()?.navigate(action)
            }
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(CURRENT_DEPOT_NAME_PARAMETER, currentName)
        currentParent?.let { outState.putInt(PARENT_ID_PARAMETER, it) }
        depotId?.let { outState.putInt(CURRENT_DEPOT_ID_PARAMETER, it) }
        super.onSaveInstanceState(outState)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        if (p2 == 0)
            currentParent = null
        else
            currentParent = allDepots[p2 - PARENT_SHIFT].uid
        Log.i(LOG_TAG, "Set parent id $currentParent")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        currentParent = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            EditDepotFragment().apply {
                arguments = Bundle().apply {
                    putString(CURRENT_DEPOT_NAME_PARAMETER, param1)
                    putString(PARENT_ID_PARAMETER, param2)
                }
            }

        private val LOG_TAG = "EditDepotFragment";
        private const val PARENT_SHIFT = 1
    }
}
