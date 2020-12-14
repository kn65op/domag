package io.github.kn65op.domag.ui.items

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
import io.github.kn65op.domag.data.database.database.DatabaseFactoryImpl
import io.github.kn65op.domag.data.database.relations.DepotWithContents
import io.github.kn65op.domag.ui.common.constructItemFullName
import io.github.kn65op.domag.ui.dialogs.ConsumeDialogController

class DepotAdapter(
    private var depot: LiveData<DepotWithContents>,
    private val activity: FragmentActivity,
    private val lifecycleOwner: LifecycleOwner
) :
    RecyclerView.Adapter<DepotAdapter.ViewHolder>() {
    open class ViewHolder(view: View) : RecyclerView.ViewHolder(view)

    class DepotViewHolder(view: View) : ViewHolder(view) {
        val nameViewHolder: TextView = view.findViewById(R.id.depot_row_name)
    }

    class ItemViewHolder(view: View) : ViewHolder(view) {
        val amountViewHolder: TextView = view.findViewById(R.id.item_row_amount)
        val unitViewHolder: TextView = view.findViewById(R.id.item_row_unit)
        val nameViewHolder: TextView = view.findViewById(R.id.item_row_name)
        val consumeButton: ImageButton = view.findViewById(R.id.item_row_consume_button)
    }

    private val depotOnPosition = 1
    private val itemOnPosition = 2
    private val dbFactory = DatabaseFactoryImpl()
    private val consumeDialogController = ConsumeDialogController(activity)

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
            val item = depotContent.items[itemPosition]
            val db = dbFactory.factory.createDatabase(activity.applicationContext)
            val category =
                db.categoryDao().findById(item.categoryId)
            Log.i(
                LOG_TAG,
                "Search for category: ${item.categoryId}"
            )
            val itemViewHolder = holder as ItemViewHolder
            Log.i(LOG_TAG, "Show item: $itemPosition")
            category.observe(lifecycleOwner, {
                Log.i(LOG_TAG, "Category for $itemPosition: ${it?.name}")
                it?.let { category ->
                    val bindItem = depotContent.items[itemPosition]
                    val fullName = constructItemFullName(category.name, bindItem.description)
                    itemViewHolder.amountViewHolder.text =
                        bindItem.amount.toString()
                    itemViewHolder.unitViewHolder.text = category.unit
                    itemViewHolder.nameViewHolder.text = fullName
                    itemViewHolder.consumeButton.setOnClickListener {
                        consumeDialogController.startConsumeDialog(
                            item.uid!!,
                            fullName,
                            category,
                            bindItem.amount
                        )
                    }
                }
                item.uid?.let { uid ->
                    holder.itemView.setOnClickListener {
                        val action =
                            ItemsFragmentDirections.actionNavItemsToFragmentEditItem(uid)
                        holder.itemView.findNavController().navigate(action)
                    }
                }
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