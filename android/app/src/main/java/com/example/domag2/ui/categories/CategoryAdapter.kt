package com.example.domag2.ui.categories

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.domag2.R
import com.example.domag2.database.database.DatabaseFactoryImpl
import com.example.domag2.database.relations.CategoryWithContents

class CategoryAdapter(
    var category: LiveData<CategoryWithContents>,
    val context: Context,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {
    open class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    }

    class CategoryViewHolder(private val view: View) : ViewHolder(view) {
        val nameViewHolder: TextView = view.findViewById(R.id.category_row_name)
    }

    class ItemViewHolder(private val view: View) : ViewHolder(view) {
        val amountViewHolder: TextView = view.findViewById(R.id.item_row_amount)
        val unitViewHolder: TextView = view.findViewById(R.id.item_row_unit)
        val nameViewHolder: TextView = view.findViewById(R.id.item_row_name)
    }

    private val categoryOnPosition = 1
    private val itemOnPosition = 2
    private val dbFactory = DatabaseFactoryImpl()

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
        val depotContent = category.value
        if (depotContent != null) {
            val itemPosition = position - depotContent.categories.size
            val db = dbFactory.factory.createDatabase(context)
            val category =
                db.categoryDao().findById(depotContent.items[itemPosition].categoryId)
            Log.i(
                LOG_TAG,
                "Seach for category: ${depotContent.items[itemPosition].categoryId}"
            )
            val itemViewHolder = holder as ItemViewHolder
            Log.i(LOG_TAG, "Show item: $itemPosition")
            itemViewHolder.amountViewHolder.text =
                depotContent.items[itemPosition].cos
            category.observe(lifecycleOwner, Observer {
                Log.i(LOG_TAG, "Category for $itemPosition: ${it?.name}")
                itemViewHolder.nameViewHolder.text = it?.name
                itemViewHolder.unitViewHolder.text = it?.unit
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
            val action = CategoriesFragmentDirections.actionNavCategoriesSelf(id)
            holder.itemView.findNavController().navigate(action)
        }
    }

    companion object {
        private const val LOG_TAG = "CategoryAdapter"
    }
}