package com.example.domag2.ui.items

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
import com.example.domag2.database.relations.DepotWithContents

class DepotAdapter(
    var depot: LiveData<DepotWithContents>,
    val context: Context,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<DepotAdapter.ViewHolder>() {
    open class ViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
    }

    class DepotViewHolder(private val view: View) : ViewHolder(view) {
        val nameViewHolder: TextView = view.findViewById(R.id.depot_row_name)
    }

    class ItemViewHolder(private val view: View) : ViewHolder(view) {
        val amountViewHolder: TextView = view.findViewById(R.id.item_row_amount)
        val unitViewHolder: TextView = view.findViewById(R.id.item_row_unit)
        val nameViewHolder: TextView = view.findViewById(R.id.item_row_name)
    }

    private val depotOnPosition = 1
    private val itemOnPosition = 2
    private val dbFactory = DatabaseFactoryImpl()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
        when (viewType) {
            depotOnPosition -> DepotViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.depot_row, parent, false)
            )
            itemOnPosition -> ItemViewHolder(
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_row, parent, false)
            )
            else -> {
                Log.e(LOG_TAG, "Invalid view type $viewType")
                DepotViewHolder(
                    LayoutInflater.from(parent.context).inflate(R.layout.depot_row, parent, false)
                )
            }
        }

    override fun getItemViewType(position: Int): Int =
        calculatePosition(position)

    private fun calculatePosition(position: Int): Int {
        Log.i(LOG_TAG, "Position type for: $position")
        return if (position >= depot.value?.depots?.size ?: 0)
            itemOnPosition
        else depotOnPosition
    }

    override fun getItemCount(): Int {
        Log.i(LOG_TAG, "${depot.value}")
        return (depot.value?.depots?.size ?: 0) + (depot.value?.items?.size ?: 0)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        when (calculatePosition(position)) {
            depotOnPosition -> {
                bindDepot(holder, position)
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
        val depotContent = depot.value
        if (depotContent != null) {
            val itemPosition = position - depotContent.depots.size
            val db = dbFactory.factory.createDatabase(context)
            val category =
                db.categoryDao().findById(depotContent.items[itemPosition].categoryId)
            Log.i(
                LOG_TAG,
                "Seach for category: ${depotContent.items[itemPosition].categoryId}"
            )
            val itemViewHolder = holder as ItemViewHolder
            Log.i(LOG_TAG, "Show item: $itemPosition")
            category.observe(lifecycleOwner, Observer {
                Log.i(LOG_TAG, "Category for $itemPosition: ${it?.name}")
                itemViewHolder.amountViewHolder.text =
                    depotContent.items[itemPosition].amount.toString()
                itemViewHolder.unitViewHolder.text = it?.unit
                itemViewHolder.nameViewHolder.text = it?.name
            })
        }
    }

    private fun bindDepot(
        holder: ViewHolder,
        position: Int
    ) {
        val depotViewHolder = holder as DepotViewHolder
        val printedDepot = depot.value?.depots?.get(position)
        depotViewHolder.nameViewHolder.text = printedDepot?.name
        holder.itemView.setOnClickListener {
            val id = printedDepot?.uid ?: 0
            val action = ItemsFragmentDirections.actionNavItemsSelf(id)
            holder.itemView.findNavController().navigate(action)
        }
    }

    companion object {
        private const val LOG_TAG = "DepotAdapter"
    }
}