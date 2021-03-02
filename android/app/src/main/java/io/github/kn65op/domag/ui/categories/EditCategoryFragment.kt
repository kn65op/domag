package io.github.kn65op.domag.ui.categories

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import dagger.hilt.android.AndroidEntryPoint
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.R
import io.github.kn65op.domag.data.database.daos.CategoryLimitDao
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.database.entities.Category
import io.github.kn65op.domag.data.database.entities.CategoryLimit
import io.github.kn65op.domag.data.database.entities.withLimit
import io.github.kn65op.domag.data.database.operations.deleteCategory
import io.github.kn65op.domag.data.database.relations.CategoryWithContents
import io.github.kn65op.domag.databinding.FragmentEditCategoryBinding
import io.github.kn65op.domag.ui.common.FragmentWithActionBar
import io.github.kn65op.domag.ui.utils.replaceText
import io.github.kn65op.domag.utils.getAllButNotItAndDescendants
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val CURRENT_CATEGORY_NAME_PARAMETER = "currentName"
private const val CURRENT_CATEGORY_UNIT_PARAMETER = "currentUnit"
private const val PARENT_ID_PARAMETER = "currentParent"
private const val PARENT_ID_ACTION_PARAMETER = "parentId"
private const val CURRENT_CATEGORY_ID_PARAMETER = "categoryId"

@AndroidEntryPoint
class EditCategoryFragment : FragmentWithActionBar(), AdapterView.OnItemSelectedListener {
    @Inject
    lateinit var db: AppDatabase
    private var currentName: String? = null
    private var currentUnit: String? = null
    private var currentParent: Int? = null
    private var categoryId: Int? = null
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var recyclerView: RecyclerView
    private lateinit var spinner: SearchableSpinner
    private lateinit var editCategoryBinding: FragmentEditCategoryBinding
    private var currentCategory = MutableLiveData<CategoryWithContents>()
    private var possibleParentCategories = emptyList<Category>()
    private var categories: List<Category>? = null

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
        Log.i(LOG_TAG, "Editing category: $categoryId: $currentName with parent: $currentParent")
        actionBar()?.title =
            if (currentName.isNullOrEmpty()) context?.getString(R.string.edit_category_new_category) else currentName
    }

    private fun readCategory() {
        categoryId?.let { searchCategoryId ->
            if (searchCategoryId != 0) {
                db.categoryDao().findWithContentsById(searchCategoryId)
                    .observe(viewLifecycleOwner, {
                        if (it != null) {
                            currentCategory.value = it
                            currentParent = it.category.parentId
                            currentUnit = it.category.unit
                            Log.i(
                                LOG_TAG,
                                "Editing category: ${it.category.uid}: ${it.category.name} with parent: ${it.category.parentId}"
                            )
                            actionBar()?.title = currentName
                            prepareCategorySelectorIfReady(currentCategory, categories)
                        }
                    })
            }
        }
    }

    private fun getAllCategories(db: AppDatabase?) {
        val rootObjects = db?.categoryDao()?.getAll()
        rootObjects?.observe(viewLifecycleOwner, {
            Log.i(LOG_TAG, "Observed all objects: ${it.size}")
            categories = it
            prepareCategorySelectorIfReady(currentCategory, categories)
        })
    }

    private fun prepareCategorySelectorIfReady(
        categoryLiveData: MutableLiveData<CategoryWithContents>,
        categories: List<Category>?
    ) {
        if (categories != null)
            prepareCategorySelector(categoryLiveData.value?.category, categories)
        else
            Log.i(LOG_TAG, "Categories are not ready to be prepared")
    }

    private fun prepareCategorySelector(category: Category?, categories: List<Category>) {
        possibleParentCategories =
            if (category == null) categories else getAllButNotItAndDescendants(category, categories)
        val possibleParents =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        possibleParents.add(context?.getString(R.string.edit_depot_parent_select_no_parent))
        possibleParents.addAll(possibleParentCategories.map { it.name })
        spinner.adapter = possibleParents
        Log.i(LOG_TAG, "Possible parent categories: ${possibleParentCategories.size}")
        val parentDepotPosition =
            possibleParentCategories.indexOfFirst {
                Log.i(LOG_TAG, "${it.uid} ? ${currentParent})")
                it.uid == currentParent
            } + PARENT_SHIFT
        Log.i(LOG_TAG, "Index $parentDepotPosition")
        spinner.setSelection(parentDepotPosition)
        spinner.onItemSelectedListener = this
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        editCategoryBinding = FragmentEditCategoryBinding.inflate(inflater, container, false)
        val root = editCategoryBinding.root

        readCategory()

        spinner = root.findViewById(R.id.edit_category_fragment_parent_spinner)
        recyclerView = root.findViewById(R.id.edit_category_content_view)

        spinner.setTitle(context?.getString(R.string.edit_category_parent_text))
        spinner.setPositiveButton(context?.getString(R.string.spinner_select_text))

        viewManager = LinearLayoutManager(context)
        viewAdapter = CategoryAdapter(currentCategory, requireActivity(), this, db)

        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        currentCategory.observe(viewLifecycleOwner, { category ->
            activity?.runOnUiThread {
                if (!recyclerView.isComputingLayout) {
                    recyclerView.adapter?.notifyDataSetChanged()
                }
                currentName = category.category.name
                currentName?.let { editCategoryBinding.editCategoryCategoryName.replaceText(it) }
                currentUnit = category.category.unit
                currentUnit?.let { editCategoryBinding.editCategoryUnitField.replaceText(it) }
                category.limits?.let {
                    editCategoryBinding.editCategoryMinimumAmountField.replaceText(
                        it.minimumDesiredAmount.toString()
                    )
                }
            }
        })

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
            db.let { saveCategory(it) }
            true
        }
        R.id.edit_depot_menu_remove_depot_item -> {
            currentCategory.value?.let {
                val parent = it.category.parentId
                lifecycleScope.launch {
                    db.deleteCategory(it)
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

    private fun saveCategory(db: AppDatabase) {
        val name = readName()
        val categoryDao = db.categoryDao()
        val category = currentCategory.value
        lifecycleScope.launch {
            if (category == null) {
                Log.i(LOG_TAG, "Insert $name with parent: $currentParent")
                val categoryId = categoryDao.insert(
                    Category(
                        name = name,
                        unit = editCategoryBinding.editCategoryUnitField.text.toString(),
                        parentId = currentParent
                    )
                )
                Log.i(LOG_TAG, "Inserted $name")
                saveLimit(db, categoryId.toInt())
            } else {
                val categoryId = category.category.uid
                categoryId?.let { id ->
                    Log.i(LOG_TAG, "Update $name")
                    val depot = Category(
                        uid = id,
                        unit = editCategoryBinding.editCategoryUnitField.text.toString(),
                        name = name,
                        parentId = currentParent
                    )
                    Log.i(LOG_TAG, "Update $depot")
                    categoryDao.update(depot)
                    Log.i(LOG_TAG, "Update $depot")
                    Log.i(LOG_TAG, "Edited $name")
                    saveLimit(db, id)
                }
            }
        }.invokeOnCompletion {
            activity?.onBackPressed()
        }
    }

    private suspend fun saveLimit(db: AppDatabase, categoryId: Int) {
        val minimumAmountText = editCategoryBinding.editCategoryMinimumAmountField.text.toString()
        val categoryLimitDao = db.categoryLimitDao()
        val categoryLimit = categoryLimitDao.getByCategoryIdImmediately(categoryId)
        if (minimumAmountText.isEmpty()) {
            removeLimit(categoryId, categoryLimit, categoryLimitDao)
            return
        }
        saveLimit(minimumAmountText, categoryLimit, categoryId, categoryLimitDao)
    }

    private suspend fun removeLimit(
        categoryId: Int,
        categoryLimit: CategoryLimit?,
        categoryLimitDao: CategoryLimitDao
    ) {
        Log.i(LOG_TAG, "There is no minimum amount specified for $categoryId")
        if (categoryLimit != null) {
            Log.i(LOG_TAG, "Remove category limit ${categoryLimit.uid}")
            categoryLimitDao.delete(categoryLimit)
        }
    }

    private suspend fun saveLimit(
        minimumAmountText: String,
        categoryLimit: CategoryLimit?,
        categoryId: Int,
        categoryLimitDao: CategoryLimitDao
    ) {
        val minimumAmount =
            FixedPointNumber(minimumAmountText.toDouble())
        if (categoryLimit == null) {
            Log.i(LOG_TAG, "insert new limit for $categoryId: $minimumAmount")
            categoryLimitDao.insert(
                CategoryLimit(
                    categoryId = categoryId, minimumDesiredAmount = minimumAmount
                )
            )
        } else {
            Log.i(LOG_TAG, "update limit for $categoryId: $minimumAmount")
            val newLimit = categoryLimit.withLimit(minimumAmount)
            categoryLimitDao.update(newLimit)
        }
    }

    private fun readName(): String {
        val originalName = editCategoryBinding.editCategoryCategoryName.text.toString()
        return if (originalName.isEmpty())
            "<UNNAMED>"
        else
            originalName
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString(CURRENT_CATEGORY_NAME_PARAMETER, currentName)
        outState.putString(CURRENT_CATEGORY_UNIT_PARAMETER, currentUnit)
        currentParent?.let { outState.putInt(PARENT_ID_PARAMETER, it) }
        categoryId?.let { outState.putInt(CURRENT_CATEGORY_ID_PARAMETER, it) }
        super.onSaveInstanceState(outState)
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, position: Int, p3: Long) {
        currentParent = if (position == 0)
            null
        else
            possibleParentCategories[position - PARENT_SHIFT].uid
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

        private const val LOG_TAG = "EditCategoryFragment"
        private const val PARENT_SHIFT = 1
    }
}
