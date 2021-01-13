package io.github.kn65op.domag.ui.items

import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ImageButton
import android.widget.TextView
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.toptoche.searchablespinnerlibrary.SearchableSpinner
import dagger.hilt.android.AndroidEntryPoint
import io.github.kn65op.android.lib.gui.dialogs.LocalDatePickerDialog
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.R
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.entities.Category
import io.github.kn65op.domag.data.entities.Depot
import io.github.kn65op.domag.data.entities.Item
import io.github.kn65op.domag.ui.common.FragmentWithActionBar
import io.github.kn65op.domag.ui.common.constructItemFullName
import io.github.kn65op.domag.ui.common.createDialog
import io.github.kn65op.domag.ui.utils.replaceText
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

private const val ITEM_ID_PARAMETER = "itemId"
private const val DEPOT_ID_PARAMETER = "depotId"
private const val CATEGORY_ID_PARAMETER = "categoryId"

@AndroidEntryPoint
class EditItemFragment : FragmentWithActionBar(), AdapterView.OnItemSelectedListener,
    LocalDatePickerDialog.DatePickerListener {
    @Inject
    lateinit var db: AppDatabase
    private var initialDepoId: Int? = null
    private var initialCategoryId: Int? = null
    private var itemId: Int? = null
    private var currentItem: Item? = null
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("ccc dd-MMMM-yyyy")
    private var bestBefore: ZonedDateTime? = null

    private lateinit var depotSpinner: SearchableSpinner
    private lateinit var categorySpinner: SearchableSpinner
    private lateinit var amountField: TextInputEditText
    private lateinit var descriptionField: TextInputEditText
    private lateinit var bestBeforeField: TextView
    private lateinit var bestBeforeLayout: TextInputLayout
    private lateinit var bestBeforeRemoveButton: ImageButton
    private var allCategories = emptyList<Category>()
    private var allDepots = emptyList<Depot>()

    override fun onCreate(savedInstanceState: Bundle?) {
        itemId = arguments?.getInt(ITEM_ID_PARAMETER)
        if (itemId == 0) {
            itemId = null
            initialDepoId = arguments?.getInt(DEPOT_ID_PARAMETER)
            initialCategoryId = arguments?.getInt(CATEGORY_ID_PARAMETER)
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
            db.itemDao().findById(it)
                .observe(viewLifecycleOwner, { item ->
                    item?.let {
                        currentItem = item
                        initialDepoId = item.depotId
                        initialCategoryId = item.categoryId
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
        bestBeforeLayout = root.findViewById(R.id.edit_item_best_before_layout)
        bestBeforeRemoveButton = root.findViewById(R.id.edit_item_remove_best_before_button)
        bestBeforeField.text

        descriptionField.doOnTextChanged { _, _, _, _ ->
            val categoryPosition = categorySpinner.selectedItemPosition
            if (categoryPosition != -1) {
                actionBar()?.title = constructItemFullName()
            }
        }

        depotSpinner.setTitle(context?.getString(R.string.edit_item_depot_spinner_title))
        depotSpinner.setPositiveButton(context?.getString(R.string.spinner_select_text))
        categorySpinner.setTitle(context?.getString(R.string.edit_item_category_spinner_title))
        categorySpinner.setPositiveButton(context?.getString(R.string.spinner_select_text))
        bestBeforeField.setOnClickListener { startDatePicker() }
        bestBeforeField.setOnFocusChangeListener { _, gained -> if (gained) startDatePicker() }
        bestBeforeRemoveButton.setOnClickListener { clearDate() }
        if (itemId == null) {
            amountField.replaceText(1.toString())
        }

        collectAllCategories(db)
        collectAllDepots(db)

        return root
    }

    private fun constructItemFullName() =
        constructItemFullName(
            allCategories[categorySpinner.selectedItemPosition].name,
            descriptionField.text.toString()
        )

    private fun startDatePicker() {
        val dialog = LocalDatePickerDialog(this)
        dialog.show(requireActivity().supportFragmentManager, "Date picker")
    }

    private fun collectAllCategories(db: AppDatabase?) {
        val rootObjects = db?.categoryDao()?.getAll()
        rootObjects?.observe(viewLifecycleOwner, { categories ->
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
        rootObjects?.observe(viewLifecycleOwner, { depots ->
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
            categorySpinner.setSelection(allCategories.indexOfFirst { it.uid == initialCategoryId })
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
        if (itemId == null) {
            val item = menu.getItem(0)
            item.isEnabled = false
            item.isVisible = false
        }
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.edit_item_menu_confirm -> {
            when {
                depotSpinner.selectedItemPosition == -1 -> {
                    createDialog(requireActivity(), R.string.edit_item_no_depot_dialog_message)
                }
                categorySpinner.selectedItemPosition == -1 -> {
                    createDialog(requireActivity(), R.string.edit_item_no_category_dialog_message)
                }
                else -> {
                    createItem(db)
                }
            }
            true
        }
        R.id.edit_item_menu_remove_item_item -> {
            val dao = db.itemDao()
            currentItem?.let {
                lifecycleScope.launch { dao.delete(it) }
            }
            activity?.onBackPressed()
            true

        }
        R.id.edit_item_menu_consume_item -> {
            Log.w(LOG_TAG, "No Consume - remove it")
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

    private fun clearDate() {
        bestBeforeField.text = ""
        bestBefore = null
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
        private const val LOG_TAG = "EditItemFragment"
    }
}
