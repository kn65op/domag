package io.github.kn65op.domag.ui.categories

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.activity.addCallback
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.github.kn65op.domag.R
import io.github.kn65op.domag.database.database.AppDatabase
import io.github.kn65op.domag.database.database.DatabaseFactoryImpl
import io.github.kn65op.domag.database.entities.Category
import io.github.kn65op.domag.database.relations.CategoryWithContents
import io.github.kn65op.domag.ui.common.FragmentWithActionBar
import com.google.android.material.floatingactionbutton.FloatingActionButton

class CategoriesFragment : FragmentWithActionBar() {
    val storedCategoryId = "categoryId"

    private val dbFactory = DatabaseFactoryImpl()
    private lateinit var recyclerView: RecyclerView
    private lateinit var viewManager: RecyclerView.LayoutManager
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private var currentCategory = MutableLiveData<CategoryWithContents>()
    private var root = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val categoryId = arguments?.getInt(storedCategoryId)

        Log.i(LOG_TAG, "Received category: $categoryId")

        val db = context?.let { dbFactory.factory.createDatabase(it) }
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
        val root = inflater.inflate(R.layout.fragment_categories, container, false)

        val currentContext = context
        if (currentContext != null) {
            recyclerView = root.findViewById(R.id.categories_recycler_view)
            viewAdapter = CategoryAdapter(currentCategory, currentContext, viewLifecycleOwner)

            viewManager = LinearLayoutManager(currentContext)
            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = viewManager
                adapter = viewAdapter
            }
            currentCategory.observe(viewLifecycleOwner, Observer {
                val callback = requireActivity().onBackPressedDispatcher.addCallback(this) {
                    val action =
                        CategoriesFragmentDirections.actionNavCategoriesSelf(
                            categoryId = it.category.parentId ?: 0
                        )
                    Log.i(LOG_TAG, "${currentCategory.value?.category?.uid}")
                    root.findNavController().navigate(action)
                }
                callback.isEnabled = it.category.uid != null

                activity?.runOnUiThread {
                    Log.i(LOG_TAG, "Data has been changed")
                    if (!recyclerView.isComputingLayout) {
                        recyclerView.adapter?.notifyDataSetChanged()
                    }
                }
            })
        } else {
            Log.e(LOG_TAG, "Unable to fill recycler view, no context")
        }
        prepareFabs(root)
        setHasOptionsMenu(true)
        return root
    }

    private fun prepareFabs(root: View) {
        val addCategoryFab: FloatingActionButton =
            root.findViewById(R.id.categories_add_category_fab)
        addCategoryFab.setOnClickListener {
            Log.i(LOG_TAG, "Clicked add depot")
            Log.i(LOG_TAG, "Wiato for to go")
            val action =
                CategoriesFragmentDirections.actionNavCategoriesToEditCategory(
                    parentId = currentCategory.value?.category?.uid ?: 0
                )
            Log.i(LOG_TAG, "${currentCategory.value?.category?.uid}")
            root.findNavController().navigate(action)
        }
    }

    private fun getCategory(db: AppDatabase?, depotId: Int) {
        val objects = db?.categoryDao()?.findWithContentsById(depotId)
        objects?.observe(viewLifecycleOwner, Observer {
            Log.i(LOG_TAG, "Observed objects: ${it}")
            if (it != null) {
                currentCategory.value = it
                actionBar()?.title = it.category.name
            }
        })
    }

    private fun getRootCategories(db: AppDatabase?) {
        val rootObjects = db?.categoryDao()?.findRootDepots()
        rootObjects?.observe(viewLifecycleOwner, Observer {
            Log.i(LOG_TAG, "Observed root objects: ${it}")
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
        private val LOG_TAG = "CategoriesFragment";
    }
}
