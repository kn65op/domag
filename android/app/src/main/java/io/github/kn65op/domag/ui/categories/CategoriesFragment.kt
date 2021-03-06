package io.github.kn65op.domag.ui.categories

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.lifecycle.MutableLiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import io.github.kn65op.domag.R
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.database.entities.Category
import io.github.kn65op.domag.data.database.relations.CategoryWithContents
import io.github.kn65op.domag.databinding.FragmentCategoriesBinding
import io.github.kn65op.domag.ui.common.FragmentWithActionBar
import io.github.kn65op.domag.ui.common.prepareFabs
import javax.inject.Inject

@AndroidEntryPoint
class CategoriesFragment : FragmentWithActionBar() {
    private val storedCategoryId = "categoryId"

    @Inject
    lateinit var db: AppDatabase
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var categoriesFragmentBinding: FragmentCategoriesBinding
    private var currentCategory = MutableLiveData<CategoryWithContents>()
    private var root = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val categoryId = arguments?.getInt(storedCategoryId)

        Log.i(LOG_TAG, "Received category: $categoryId")

        if (categoryId == null || categoryId == 0) {
            getRootCategories(db)
        } else {
            getCategory(db, categoryId)
            root = false
        }

        return prepareView(inflater, container)
    }

    private fun prepareView(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): View {
        categoriesFragmentBinding = FragmentCategoriesBinding.inflate(inflater, container, false)
        val root = categoriesFragmentBinding.root

        val currentContext = requireContext()
        recyclerView = root.findViewById(R.id.categories_recycler_view)
        viewAdapter = CategoryAdapter(currentCategory, requireActivity(), viewLifecycleOwner, db)

        viewManager = LinearLayoutManager(currentContext)
        recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }
        currentCategory.observe(viewLifecycleOwner, {
            startFillingCategory(it, root)
        })
        prepareFabMenu(root)
        setHasOptionsMenu(true)
        return root
    }

    private fun startFillingCategory(
        it: CategoryWithContents,
        root: View
    ) {
        setBackButton(it, root)
        prepareShowingLimit(it)

        activity?.runOnUiThread {
            Log.i(LOG_TAG, "Data has been changed")
            if (!recyclerView.isComputingLayout) {
                recyclerView.adapter?.notifyDataSetChanged()
            }
        }
    }

    private fun prepareShowingLimit(it: CategoryWithContents) {
        if (it.limits != null) {
            categoriesFragmentBinding.fragmentCategoriesLimitInfo.text =
                requireActivity().getString(
                    R.string.categories_minimum_amount_text,
                    "${it.limits.minimumDesiredAmount} ${it.category.unit}"
                )
            categoriesFragmentBinding.fragmentCategoriesLimitInfo.visibility = View.VISIBLE
        } else {
            categoriesFragmentBinding.fragmentCategoriesLimitInfo.visibility = View.GONE
        }
    }

    private fun setBackButton(
        it: CategoryWithContents,
        root: View
    ) {
        val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            val action =
                CategoriesFragmentDirections.actionNavCategoriesSelf(
                    categoryId = it.category.parentId ?: 0
                )
            Log.i(LOG_TAG, "${currentCategory.value?.category?.uid}")
            root.findNavController().navigate(action)
        }
        callback.isEnabled = it.category.uid != null
    }

    private fun prepareFabMenu(root: View) {
        val addDepotAction = View.OnClickListener {
            Log.i(LOG_TAG, "Clicked add depot")
            val action = CategoriesFragmentDirections.actionNavCategoriesToEditContainer()
            root.findNavController().navigate(action)
        }
        val addItemAction = View.OnClickListener {
            Log.i(LOG_TAG, "Clicked add item")
            val action = CategoriesFragmentDirections.actionNavCategoriesToFragmentEditItem(
                categoryId = currentCategory.value?.category?.uid ?: 0
            )
            root.findNavController().navigate(action)
        }
        val addCategoryAction = View.OnClickListener {
            Log.i(LOG_TAG, "Clicked add category")
            val action =
                CategoriesFragmentDirections.actionNavCategoriesToEditCategory(
                    parentId = currentCategory.value?.category?.uid ?: 0
                )
            Log.i(LOG_TAG, "${currentCategory.value?.category?.uid}")
            root.findNavController().navigate(action)
        }
        prepareFabs(
            root,
            addDepotAction = addDepotAction,
            addCategoryAction = addCategoryAction,
            addItemAction = addItemAction,
            backgroundId = R.id.categories_fab_background,
        )
    }

    private fun getCategory(db: AppDatabase?, depotId: Int) {
        val objects = db?.categoryDao()?.findWithContentsById(depotId)
        objects?.observe(viewLifecycleOwner, {
            Log.i(LOG_TAG, "Observed objects: $it")
            if (it != null) {
                currentCategory.value = it
                actionBar()?.title = it.category.name
            }
        })
    }

    private fun getRootCategories(db: AppDatabase?) {
        val rootObjects = db?.categoryDao()?.findRootDepots()
        rootObjects?.observe(viewLifecycleOwner, {
            Log.i(LOG_TAG, "Observed root objects: $it")
            if (it != null) {
                currentCategory.value = CategoryWithContents(
                    category = Category(name = "Categories - title", unit = ""),
                    categories = it
                )
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.categories_menu, menu)
        menu.findItem(R.id.categories_menu_edit_category_menu_item).isVisible = !root
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean =
        when (item.itemId) {
            R.id.categories_menu_edit_category_menu_item -> {
                Log.i(LOG_TAG, "Clicked add depot")
                currentCategory.value?.category?.uid?.let {
                    val action = CategoriesFragmentDirections.actionNavCategoriesToEditCategory(
                        categoryId = it
                    )
                    Log.i(LOG_TAG, "Edit $it")
                    view?.findNavController()?.navigate(action)
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }

    companion object {
        private const val LOG_TAG = "CategoriesFragment"
    }
}
