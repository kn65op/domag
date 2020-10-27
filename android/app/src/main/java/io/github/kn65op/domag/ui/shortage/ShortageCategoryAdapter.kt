package io.github.kn65op.domag.ui.shortage

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import io.github.kn65op.domag.R
import io.github.kn65op.domag.database.relations.CategoryWithContents
import io.github.kn65op.domag.database.utils.amount
import io.github.kn65op.domag.ui.utils.notifyIfNotComputing

class ShortageCategoryAdapter(
    private val activity: FragmentActivity,
    private val recycler: RecyclerView,
) : RecyclerView.Adapter<ShortageCategoryAdapter.CategoryViewHolder>() {
    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.shortage_category_row_name)
        val detailsView: TextView = view.findViewById(R.id.shortage_category_row_details)
    }

    private var categories: List<CategoryWithContents> = emptyList()

    fun dataChanged(newCategories: List<CategoryWithContents>) {
        categories = newCategories
        Log.d(LOG_TAG, "Categories in db changed in amount: ${categories.size}")
        activity.runOnUiThread {
            Log.i(LOG_TAG, "Notify recycler")
            recycler.notifyIfNotComputing()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        CategoryViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.shortage_category_row,
                parent,
                false
            )
        )

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        activity.runOnUiThread {
            holder.nameView.text = category.category.name
            holder.detailsView.text =
                "${category.items.amount()}/${category.limits?.minimumDesiredAmount.toString()}"
        }
    }

    override fun getItemCount(): Int {
        Log.d(LOG_TAG, "Categories count: ${categories.size}")
        return categories.size
    }

    companion object {
        private const val LOG_TAG = "ShortageFragmentAdapter"
    }
}