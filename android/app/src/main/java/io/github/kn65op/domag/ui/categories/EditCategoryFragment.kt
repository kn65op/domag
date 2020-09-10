package io.github.kn65op.domag.ui.categories

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
import io.github.kn65op.domag.database.entities.Category
import io.github.kn65op.domag.database.operations.deleteCategory
import io.github.kn65op.domag.database.relations.CategoryWithContents
import io.github.kn65op.domag.ui.common.FragmentWithActionBar
import io.github.kn65op.domag.ui.utils.replaceText
import kotlinx.android.synthetic.main.fragment_edit_category.*
import kotlinx.coroutines.launch

private const val CURRENT_CATEGORY_NAME_PARAMETER = "currentName"
private const val CURRENT_CATEGORY_UNIT_PARAMETER = "currentUnit"
private const val PARENT_ID_PARAMETER = "currentParent"
private const val PARENT_ID_ACTION_PARAMETER = "parentId"
private const val CURRENT_CATEGORY_ID_PARAMETER = "categoryId"

class EditCategoryFragment : FragmentWithActionBar(), AdapterView.OnItemSelectedListener {
    private val dbFactory = DatabaseFactoryImpl()
    private var currentName: String? = null
    private var currentUnit: String? = null
    private var currentParent: Int? = null
    private var categoryId: Int? = null
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: SearchableSpinner
    private var currentCategory = MutableLiveData<CategoryWithContents>()
    private var possibleParentCategories = emptyList<Category>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        currentName = savedInstanceState?.getString(CURRENT_CATEGORY_NAME_PARAMETER)
        currentUnit = savedInstanceState?.getString(CURRENT_CATEGORY_UNIT_PARAMETER)
        currentParent = savedInstanceState?.getInt(PARENT_ID_PARAMETER)
        if (currentParent == null)
            currentParent = arguments?.getInt(PARENT_ID_ACTION_PARAMETER)
        categoryId = arguments?.getInt(CURRENT_CATEGORY_ID_PARAMETER)
        Log.i(LOG_TAG, "Passed categoryId: $categoryId")
        val savedId = savedInstanceState?.getInt(CURRENT_CATEGORY_ID_PARAMETER)
        savedId?.let {
            if (it != 0) {
                categoryId = it
            }
        }
        Log.i(LOG_TAG, "Using depotId: $categoryId")
        Log.i(LOG_TAG, "Editing category: $categoryId: $currentName with parent: $currentParent")
        actionBar()?.title = if (currentName.isNullOrEmpty()) context?.getString(R.string.edit_category_new_category) else currentName
    }

    private fun readDepot() {
        categoryId?.let { searchCategoryId ->
            if (searchCategoryId != 0) {
                context?.let { context ->
                    val db = dbFactory.factory.createDatabase(context)
                    db.categoryDao().findWithContentsById(searchCategoryId)
                        .observe(viewLifecycleOwner, Observer {
                            if (it != null) {
                                currentCategory.value = it
                                currentParent = it.category.parentId
                                currentUnit = it.category.unit
                                Log.i(
                                    LOG_TAG,
                                    "Editing category: ${it.category.uid}: ${it.category.name} with parent: ${it.category.parentId}"
                                )
                                actionBar()?.title = currentName
                            }
                        })
                }
            }
        }
    }

    private fun getAllCategories(db: AppDatabase?) {
        val rootObjects = db?.categoryDao()?.getAll()
        rootObjects?.observe(viewLifecycleOwner, Observer { categories ->
            if (categories != null) {
                Log.i(LOG_TAG, "Observed all objects: ${categories.size}")
                possibleParentCategories = categories.filter { it.uid != categoryId }
                val possibleParents = ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
                possibleParents.add(context?.getString(R.string.edit_depot_parent_select_no_parent))
                possibleParents.addAll(possibleParentCategories.map { it.name })
                spinner.adapter = possibleParents
                Log.i(LOG_TAG, "AllDepots: ${possibleParentCategories.size}")
                val parentDepotPosition =
                    possibleParentCategories.indexOfFirst {
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
        val root = inflater.inflate(R.layout.fragment_edit_category, container, false)

        readDepot()

        spinner = root.findViewById(R.id.edit_category_fragment_parent_spinner)
        recyclerView = root.findViewById(R.id.edit_category_content_view)

        spinner.setTitle(context?.getString(R.string.edit_category_parent_text))
        spinner.setPositiveButton(context?.getString(R.string.spinner_select_text))

        viewManager = LinearLayoutManager(context)
        viewAdapter = CategoryAdapter(currentCategory, requireContext(), this)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        currentCategory.observe(viewLifecycleOwner, Observer {
            activity?.runOnUiThread {
                if (!recyclerView.isComputingLayout) {
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                currentName = it.category.name
                currentName?.let { edit_category_category_name.replaceText(it) }
                currentUnit = it.category.unit
                currentUnit?.let { edit_category_unit_field.replaceText(it) }
            }
        })

        val db = context?.let { dbFactory.factory.createDatabase(it) }
        getAllCategories(db)

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
            val name = edit_category_category_name.text.toString()
            if (name.isNotEmpty()) {
                val db = context?.let { it1 -> dbFactory.factory.createDatabase(it1) }
                val dao = db?.categoryDao()
                if (currentCategory.value == null) {
                    lifecycleScope.launch {
                        Log.i(LOG_TAG, "Insert $name with parent: $currentParent")
                        dao?.insert(
                            Category(
                                name = name,
                                unit = edit_category_unit_field.text.toString(),
                                parentId = currentParent
                            )
                        )
                        Log.i(LOG_TAG, "Inserted $name")
                    }
                } else {
                    lifecycleScope.launch {
                        Log.i(LOG_TAG, "Edit $name")
                        currentCategory.value?.category?.let {
                            val depot = Category(
                                uid = it.uid,
                                unit = edit_category_unit_field.text.toString(),
                                name = name,
                                parentId = currentParent
                            )
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
            currentCategory.value?.let {
                val parent = it.category.parentId
                lifecycleScope.launch {
                    db?.deleteCategory(it)
                }
                val action =
                    EditCategoryFragmentDirections.actionEditCategoryToNavCategories(
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
        outState.putString(CURRENT_CATEGORY_NAME_PARAMETER, currentName)
        outState.putString(CURRENT_CATEGORY_UNIT_PARAMETER, currentUnit)
        currentParent?.let { outState.putInt(PARENT_ID_PARAMETER, it) }
        categoryId?.let { outState.putInt(CURRENT_CATEGORY_ID_PARAMETER, it) }
        super.onSaveInstanceState(outState)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        if (position == 0)
            currentParent = null
        else
            currentParent = possibleParentCategories[position - PARENT_SHIFT].uid
        Log.i(LOG_TAG, "Set parent id $currentParent")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        currentParent = null
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String, param3: String) =
            EditCategoryFragment().apply {
                arguments = Bundle().apply {
                    putString(CURRENT_CATEGORY_NAME_PARAMETER, param1)
                    putString(PARENT_ID_PARAMETER, param2)
                    putString(CURRENT_CATEGORY_UNIT_PARAMETER, param3)
                }
            }

        private val LOG_TAG = "EditCategoryFragment";
        private const val PARENT_SHIFT = 1
    }
}
