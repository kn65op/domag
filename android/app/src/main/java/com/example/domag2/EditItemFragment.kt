package com.example.domag2

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.domag2.database.database.AppDatabase
import com.example.domag2.database.database.DatabaseFactoryImpl
import com.example.domag2.database.entities.Category
import com.example.domag2.database.entities.Depot
import com.example.domag2.database.entities.Item
import com.google.android.material.textfield.TextInputEditText
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import io.github.kn65op.android.lib.type.FixedPointNumber
import kotlinx.coroutines.launch

private const val DEPOT_ID_PARAMETER = "depotId"

class EditItemFragment : Fragment(), AdapterView.OnItemSelectedListener {
    private val dbFactory = DatabaseFactoryImpl()
    private var depotId = 0
    private lateinit var depotSpinner: SearchableSpinner
    private lateinit var categorySpinner: SearchableSpinner
    private lateinit var amountField: TextInputEditText
    private lateinit var descriptionField: TextInputEditText
    private var allCategories = emptyList<Category>()
    private var allDepots = emptyList<Depot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        depotId = arguments?.getInt(DEPOT_ID_PARAMETER) ?: 0
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_item, container, false)
        setHasOptionsMenu(true)

        depotSpinner = root.findViewById(R.id.edit_item_depot_spinner)
        categorySpinner = root.findViewById(R.id.edit_item_category_spinner)
        amountField = root.findViewById(R.id.edit_item_amount_value)
        descriptionField = root.findViewById(R.id.edit_item_description)

        depotSpinner.setTitle(context?.getString(R.string.edit_item_depot_spinner_title))
        depotSpinner.setPositiveButton(context?.getString(R.string.spinner_select_text))
        categorySpinner.setTitle(context?.getString(R.string.edit_item_category_spinner_title))
        categorySpinner.setPositiveButton(context?.getString(R.string.spinner_select_text))

        val db = dbFactory.factory.createDatabase(requireContext())
        collectAllCategories(db)
        collectAllDepots(db)

        return root
    }

    private fun collectAllCategories(db: AppDatabase?) {
        val rootObjects = db?.categoryDao()?.getAll()
        rootObjects?.observe(viewLifecycleOwner, Observer { categories ->
            if (categories != null) {
                allCategories = categories
                Log.i(LOG_TAG, "Observed all categories: ${allCategories.size}")
                val list =
                    ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
                list.addAll(categories.map { it.name })
                categorySpinner.adapter = list
                categorySpinner.onItemSelectedListener = this
            }
        })
    }

    private fun collectAllDepots(db: AppDatabase?) {
        val rootObjects = db?.depotDao()?.getAll()
        rootObjects?.observe(viewLifecycleOwner, Observer { depots ->
            if (depots != null) {
                allDepots = depots
                Log.i(LOG_TAG, "Observed all depots: ${allCategories.size}")
                val list =
                    ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
                list.addAll(depots.map { it.name })
                depotSpinner.adapter = list
                depotSpinner.onItemSelectedListener = this
            }
        })
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.i(LOG_TAG, "Some selected $p2")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.i(LOG_TAG, "Nothing selected")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_depot_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.edit_depot_menu_confirm -> {
            val db = dbFactory.factory.createDatabase(requireContext())
            val depotId = allDepots[depotSpinner.selectedItemPosition].uid
            val categoryId = allCategories[categorySpinner.selectedItemPosition].uid
            if (depotId != null && categoryId != null) {
                lifecycleScope.launch {
                    val item = Item(
                        depotId = depotId,
                        categoryId = categoryId,
                        description = descriptionField.text.toString(),
                        amount = FixedPointNumber(amountField.text.toString().toDouble())
                    )
                    val dao = db.itemDao()
                    dao.insert(item)
                }

            }
            activity?.onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    companion object {
        private val LOG_TAG = "EditItemFragment";
    }
}
