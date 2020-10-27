package io.github.kn65op.domag.ui.shortTerm

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import io.github.kn65op.domag.R
import io.github.kn65op.domag.database.database.AppDatabase
import io.github.kn65op.domag.database.entities.Item
import io.github.kn65op.domag.ui.dialogs.ConsumeDialogController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ShortTermItemAdapter(
    lifecycleOwner: LifecycleOwner,
    val items: LiveData<List<Item>>?,
    val db: AppDatabase?,
    val activity: FragmentActivity
) :
    RecyclerView.Adapter<ShortTermItemAdapter.ItemViewHolder>() {
    class ItemViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.short_term_item_row_name)
        val amount: TextView = view.findViewById(R.id.short_term_item_row_amount)
        val unit: TextView = view.findViewById(R.id.short_term_item_row_unit)
        val date: TextView = view.findViewById(R.id.short_term_item_row_date)
        val consumeButton: ImageButton = view.findViewById(R.id.short_term_consume_button)
    }

    private val consumeDialogController = ConsumeDialogController(activity)

    init {
        items?.observe(lifecycleOwner,
            Observer { notifyDataSetChanged() })
    }

    override fun getItemCount(): Int {
        val size = items?.value?.size ?: 0
        Log.i(LOG_TAG, "Found $size items")
        return size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemViewHolder {
        return ItemViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.short_term_item_row,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ItemViewHolder, position: Int) {
        items?.value?.get(position)?.let { item ->
            Log.i(LOG_TAG, "Bind $position")
            GlobalScope.launch {
                Log.i(LOG_TAG, "Start routine")
                val categoryDao = db?.categoryDao()
                val category = categoryDao?.findWithContentsByIdImmediately(item.categoryId)
                category?.let {
                    val depotDao = db?.depotDao()
                    val name = if (item.description.isNullOrEmpty())
                        "${category.category.name} in ${
                            depotDao?.getDepotName(
                                item.depotId
                            )
                        }"
                    else
                        "${item.description} (${category.category.name}) ${
                            activity.applicationContext.getString(
                                R.string.inside
                            )
                        } ${
                            depotDao?.getDepotName(
                                item.depotId
                            )
                        }"
                    val unit = categoryDao.getCategoryUnit(item.categoryId)
                    activity.runOnUiThread {
                        holder.amount.text = item.amount.toString()
                        holder.name.text = name
                        holder.unit.text = unit
                        holder.date.text = item.bestBefore?.format(timeFormatter)
                        item.uid?.let {
                            holder.consumeButton.setOnClickListener {
                                consumeDialogController.startConsumeDialog(
                                    itemId = item.uid,
                                    fullName = name,
                                    category = category.category,
                                    currentAmount = item.amount
                                )
                            }
                        }
                    }
                }
            }
            holder.itemView.setOnClickListener {
                Log.i(LOG_TAG, "Navigate to item ${item.uid}")
                val id = item.uid ?: 0
                val action = ShortTermFragmentDirections.actionNavShortTermToFragmentEditItem(id)
                holder.itemView.findNavController().navigate(action)
            }
        }
    }

    companion object {
        private val timeFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
        private const val LOG_TAG = "ShortTermItemAdapter"
    }
}