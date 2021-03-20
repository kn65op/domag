package io.github.kn65op.domag.ui.shortTerm

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import io.github.kn65op.domag.R
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.model.Item
import io.github.kn65op.domag.data.model.RawItem
import io.github.kn65op.domag.ui.dialogs.ConsumeDialogController
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ShortTermItemAdapter(
    lifecycleScope: LifecycleCoroutineScope,
    itemsFlow: Flow<List<Item>>,
    db: AppDatabase,
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

    private val consumeDialogController = ConsumeDialogController(activity, db)
    private var items: List<Item> = emptyList()

    init {
        lifecycleScope.launch { test(itemsFlow) }
    }


    private suspend fun test(itemsFlow: Flow<List<Item>>) {
        itemsFlow.collect {
            items = it
            notifyDataSetChanged()
        }
    }


    override fun getItemCount(): Int {
        val size = items.size
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
        items.elementAtOrNull(position)?.let { item ->
            Log.i(LOG_TAG, "Bind $position")
            GlobalScope.launch {
                Log.i(LOG_TAG, "Start routine")
                val category = item.category
                category?.let {
                    val name = if (item.description.isNullOrEmpty())
                        "${category.name} in ${item.depot?.name}"
                    else
                        "${item.description} (${category.name}) ${
                            activity.applicationContext.getString(
                                R.string.inside
                            )
                        } ${item.depot?.name}"
                    val unit = item.category.unit
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
                                    category = category,
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