package com.example.domag2

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import com.example.domag2.database.database.AppDatabase
import com.example.domag2.database.database.DatabaseFactoryImpl
import com.example.domag2.database.entities.Category
import com.example.domag2.database.entities.Depot
import com.example.domag2.database.entities.Item
import com.example.domag2.ui.common.FragmentWithActionBar
import com.example.domag2.ui.common.constructItemFullName
import com.example.domag2.ui.common.createDialog
import com.example.domag2.ui.utils.replaceText
import com.google.android.material.textfield.TextInputEditText
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import io.github.kn65op.android.lib.gui.dialogs.LocalDatePickerDialog
import io.github.kn65op.android.lib.type.FixedPointNumber
import kotlinx.coroutines.launch
import java.time.*
import java.time.format.DateTimeFormatter

private const val ITEM_ID_PARAMETER = "itemId"
private const val DEPOT_ID_PARAMETER = "depotId"

class EditItemFragment : FragmentWithActionBar(), AdapterView.OnItemSelectedListener,
    LocalDatePickerDialog.DatePickerListener {
    private val dbFactory = DatabaseFactoryImpl()
    private var initialDepoId: Int? = null
    private var itemCategoryId: Int? = null
    private var itemId: Int? = null
    private var currentItem: Item? = null
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("ccc dd-MMMM-yyyy")
    private var bestBefore: ZonedDateTime = ZonedDateTime.now()

    //private var fromDepotDepotId : Int?  = null
    private lateinit var depotSpinner: SearchableSpinner
    private lateinit var categorySpinner: SearchableSpinner
    private lateinit var amountField: TextInputEditText
    private lateinit var descriptionField: TextInputEditText
    private lateinit var bestBeforeField: TextView
    private var allCategories = emptyList<Category>()
    private var allDepots = emptyList<Depot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        itemId = arguments?.getInt(ITEM_ID_PARAMETER)
        if (itemId == 0) {
            itemId = null
            initialDepoId = arguments?.getInt(DEPOT_ID_PARAMETER)
        }
        Log.i(LOG_TAG, "Item is $itemId")
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_edit_item, container, false)
        setHasOptionsMenu(true)

        itemId?.let()
        {
            dbFactory.factory.createDatabase(requireContext()).itemDao().findById(it)
                .observe(viewLifecycleOwner, Observer { item ->
                    item?.let {
                        currentItem = item
                        initialDepoId = item.depotId
                        itemCategoryId = item.categoryId
                        item.description?.let { it1 -> descriptionField.replaceText(it1) }
                        amountField.replaceText(item.amount.toString())
                        selectProperSpinnerEntry()
                        item.bestBefore?.let { bestBefore -> setDate(bestBefore) }
                    }
                })
        }

        depotSpinner = root.findViewById(R.id.edit_item_depot_spinner)
        categorySpinner = root.findViewById(R.id.edit_item_category_spinner)
        amountField = root.findViewById(R.id.edit_item_amount_value)
        descriptionField = root.findViewById(R.id.edit_item_description)
        bestBeforeField = root.findViewById(R.id.edit_item_best_before_field)

        descriptionField.doOnTextChanged { _, _, _, _ ->
            val categoryPosition = categorySpinner.selectedItemPosition
            if (categoryPosition != -1) {
                actionBar()?.title = constructItemFullName(
                    allCategories[categorySpinner.selectedItemPosition].name,
                    descriptionField.text.toString()
                )
            }
        }

        depotSpinner.setTitle(context?.getString(R.string.edit_item_depot_spinner_title))
        depotSpinner.setPositiveButton(context?.getString(R.string.spinner_select_text))
        categorySpinner.setTitle(context?.getString(R.string.edit_item_category_spinner_title))
        categorySpinner.setPositiveButton(context?.getString(R.string.spinner_select_text))
        bestBeforeField.setOnClickListener { _ ->
            val dialog = LocalDatePickerDialog(this)
            dialog.show(requireActivity().supportFragmentManager, "Date picker")

        }

        val db = dbFactory.factory.createDatabase(requireContext())
        collectAllCategories(db)
        collectAllDepots(db)

        return root
    }

    private fun collectAllCategories(db: AppDatabase?) {
        val rootObjects = db?.categoryDao()?.getAll()
        rootObjects?.observe(viewLifecycleOwner, Observer { categories ->
            if (categories != null) {
                prepareAllCategoriesSpinner(categories)
                selectProperSpinnerEntry()
            }
        })
    }

    private fun prepareAllCategoriesSpinner(categories: List<Category>) {
        allCategories = categories
        Log.i(LOG_TAG, "Observed all categories: ${allCategories.size}")
        val list =
            ArrayAdapter<String>(requireContext(), android.R.layout.simple_spinner_item)
        list.addAll(categories.map { it.name })
        categorySpinner.adapter = list
        categorySpinner.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?, p1: View?, selected: Int, p3: Long
                ) {
                    actionBar()?.title = constructItemFullName(
                        allCategories[selected].name,
                        descriptionField.text.toString()
                    )
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {
                    Log.i(LOG_TAG, "Nothing selected")
                }
            }
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
                selectProperSpinnerEntry()
            }
        })
    }

    private fun selectProperSpinnerEntry() {
        if (allDepots.isNotEmpty() && allCategories.isNotEmpty()) {
            Log.i(LOG_TAG, "Depot: $initialDepoId")
            depotSpinner.setSelection(allDepots.indexOfFirst { it.uid == initialDepoId })
            currentItem?.let { item ->
                categorySpinner.setSelection(allCategories.indexOfFirst { it.uid == item.categoryId })
            }
        }
    }

    override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
        Log.i(LOG_TAG, "Some selected $p2")
    }

    override fun onNothingSelected(p0: AdapterView<*>?) {
        Log.i(LOG_TAG, "Nothing selected")
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.edit_item_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.edit_item_menu_confirm -> {
            val db = dbFactory.factory.createDatabase(requireContext())
            if (depotSpinner.selectedItemPosition == -1) {
                createDialog(requireActivity(), R.string.edit_item_no_depot_dialog_message)
            } else if (categorySpinner.selectedItemPosition == -1) {
                createDialog(requireActivity(), R.string.edit_item_no_category_dialog_message)
            } else {
                createItem(db)
            }
            true
        }
        R.id.edit_item_menu_remove_item_item -> {
            val dao = dbFactory.factory.createDatabase(requireContext()).itemDao()
            currentItem?.let {
                lifecycleScope.launch { dao.delete(it) }
            }
            activity?.onBackPressed()
            true

        }
        else -> super.onOptionsItemSelected(item)
    }

    private fun writeItemToDb(item: Item, db: AppDatabase) {
        lifecycleScope.launch {
            val dao = db.itemDao()
            if (item.uid == null) {
                dao.insert(item)
            } else {
                dao.update(item)
            }
        }
    }

    private fun createItem(db: AppDatabase) {
        val depotId = allDepots[depotSpinner.selectedItemPosition].uid
        val categoryId = allCategories[categorySpinner.selectedItemPosition].uid
        try {
            val amount = FixedPointNumber(amountField.text.toString().toDouble())
            if (depotId != null && categoryId != null) {
                lifecycleScope.launch {
                    val item = Item(
                        uid = itemId,
                        depotId = depotId,
                        categoryId = categoryId,
                        description = descriptionField.text.toString(),
                        amount = amount,
                        bestBefore = bestBefore
                    )
                    writeItemToDb(item, db)
                }
            }
            activity?.onBackPressed()
        } catch (ex: NumberFormatException) {
            Log.e(LOG_TAG, "Amount can't be converted: $ex")
            createDialog(requireActivity(), R.string.edit_item_no_amount_dialog_message)
        }
    }

    override fun onDateSet(dialog: DialogFragment, localDate: LocalDate) {
        Log.i(LOG_TAG, "Was set $localDate")
        setDate(localDate)
    }

    private fun setDate(localDate: ZonedDateTime?) {
        bestBeforeField.text = localDate?.format(timeFormatter)
        localDate?.let { bestBefore = it }
    }

    private fun setDate(localDate: LocalDate?) {
        bestBeforeField.text = localDate?.format(timeFormatter)
        localDate?.let {
            bestBefore = ZonedDateTime.of(
                localDate, LocalTime.now(),
                ZoneId.systemDefault()
            )
        }
    }

    companion object {
        private val LOG_TAG = "EditItemFragment";
    }
}
