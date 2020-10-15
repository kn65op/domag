package io.github.kn65op.domag.ui.categories

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import io.github.kn65op.domag.R
import io.github.kn65op.domag.database.database.DatabaseFactoryImpl
import io.github.kn65op.domag.database.relations.CategoryWithContents
import io.github.kn65op.domag.ui.common.constructItemFullName
import io.github.kn65op.domag.ui.dialogs.ConsumeDialogController

class CategoryAdapter(
    private var category: LiveData<CategoryWithContents>,
    private val activity: FragmentActivity,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class CategoryViewHolder(view: View) : ViewHolder(view) {
        val nameViewHolder: TextView = view.findViewById(R.id.category_row_name)
    }

    class ItemViewHolder(view: View) : ViewHolder(view) {
        val amountViewHolder: TextView = view.findViewById(R.id.item_row_amount)
        val unitViewHolder: TextView = view.findViewById(R.id.item_row_unit)
        val nameViewHolder: TextView = view.findViewById(R.id.item_row_name)
        val consumeButton: ImageButton = view.findViewById(R.id.item_row_consume_button)
    }

    private val categoryOnPosition = 1
    private val itemOnPosition = 2
    private val dbFactory = DatabaseFactoryImpl()
    private val consumeDialogController = ConsumeDialogController(activity)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            categoryOnPosition -> CategoryViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.category_row, parent, false)
            )
            itemOnPosition -> ItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_row, parent, false)
            )
            else -> {
                Log.e(LOG_TAG, "Invalid view type $viewType")
                CategoryViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.depot_row, parent, false)
                )
            }
        }

    override fun getItemViewType(position: Int): Int =
        calculatePosition(position)

    private fun calculatePosition(position: Int): Int {
        Log.i(LOG_TAG, "Position type for: $position")
        return if (position >= category.value?.categories?.size ?: 0)
            itemOnPosition
        else categoryOnPosition
    }

    override fun getItemCount(): Int {
        Log.i(LOG_TAG, "${category.value}")
        return (category.value?.categories?.size ?: 0) + (category.value?.items?.size ?: 0)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (calculatePosition(position)) {
            categoryOnPosition -> {
                bindCategory(holder, position)
            }
            itemOnPosition -> {
                bindItem(position, holder)
            }
            else -> {
                Log.e(LOG_TAG, "Invalid position, no binding will be done")
            }
        }
    }

    private fun bindItem(
        position: Int,
        holder: ViewHolder
    ) {
        val categoryContent = category.value
        if (categoryContent != null) {
            val itemPosition = position - categoryContent.categories.size
            val db = dbFactory.factory.createDatabase(activity.applicationContext)
            val depot =
                db.depotDao().findById(categoryContent.items[itemPosition].depotId)
            Log.i(
                LOG_TAG,
                "Seach for category: ${categoryContent.items[itemPosition].categoryId}"
            )
            val itemViewHolder = holder as ItemViewHolder
            Log.i(LOG_TAG, "Show item: $itemPosition")
            depot.observe(lifecycleOwner, {
                Log.i(LOG_TAG, "Category for $itemPosition: ${it?.name}")
                it?.let { depot ->
                    val item = categoryContent.items[itemPosition]
                    val fullName = constructItemFullName(depot.name, item.description)

                    itemViewHolder.amountViewHolder.text =
                        categoryContent.items[itemPosition].amount.toString()
                    itemViewHolder.nameViewHolder.text = fullName
                    itemViewHolder.unitViewHolder.text = categoryContent.category.unit
                    itemViewHolder.consumeButton.setOnClickListener {
                        consumeDialogController.startConsumeDialog(
                            itemId = item.uid!!,
                            fullName = fullName,
                            category = categoryContent.category,
                            currentAmount = item.amount
                        )
                    }
                }
            })
        }
    }

    private fun bindCategory(
        holder: ViewHolder,
        position: Int
    ) {
        val depotViewHolder = holder as CategoryViewHolder
        val printedCategory = category.value?.categories?.get(position)
        depotViewHolder.nameViewHolder.text = printedCategory?.name
        holder.itemView.setOnClickListener {
            val id = printedCategory?.uid ?: 0
            Log.i(LOG_TAG, "Navigate to $id")
            val action = CategoriesFragmentDirections.actionNavCategoriesSelf(id)
            holder.itemView.findNavController().navigate(action)
        }
    }

    companion object {
        private const val LOG_TAG = "CategoryAdapter"
    }
}