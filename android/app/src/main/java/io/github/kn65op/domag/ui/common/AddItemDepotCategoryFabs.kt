package io.github.kn65op.domag.ui.common

import android.util.Log
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import com.google.android.material.floatingactionbutton.FloatingActionButton
import io.github.kn65op.domag.R

fun prepareFabs(
    root: View,
    addDepotAction: View.OnClickListener,
    addItemAction: View.OnClickListener,
    addCategoryAction: View.OnClickListener,
    backgroundId : Int
) {
    val fabBackground: View = root.findViewById(backgroundId)
    val fabGeneral: FloatingActionButton = root.findViewById(R.id.items_general_fab)
    val addItemLayout: ConstraintLayout = root.findViewById(R.id.items_add_item_layout)
    val addDepotLayout: ConstraintLayout = root.findViewById(R.id.items_add_depot_layout)
    val addCategoryLayout: ConstraintLayout = root.findViewById(R.id.items_add_category_layout)
    val fabClose: FloatingActionButton = root.findViewById(R.id.items_close_fab)
    val hideFabMenu: (view: View) -> Unit = {
        Log.d(LOG_TAG, "hide fabs")
        addDepotLayout.visibility = View.GONE
        addItemLayout.visibility = View.GONE
        addCategoryLayout.visibility = View.GONE
        fabClose.visibility = View.GONE
        fabGeneral.visibility = View.VISIBLE
        fabBackground.visibility = View.GONE
    }
    fabGeneral.setOnClickListener {
        Log.d(LOG_TAG, "show fabs")
        addDepotLayout.visibility = View.VISIBLE
        addItemLayout.visibility = View.VISIBLE
        addCategoryLayout.visibility = View.VISIBLE
        fabClose.visibility = View.VISIBLE
        fabGeneral.visibility = View.GONE
        fabBackground.visibility = View.VISIBLE
    }
    fabClose.setOnClickListener(hideFabMenu)
    fabBackground.setOnClickListener(hideFabMenu)
    val addDepotFab: FloatingActionButton = root.findViewById(R.id.items_add_depot_fab)
    addDepotFab.setOnClickListener (addDepotAction)
    val addItemFab : FloatingActionButton = root.findViewById(R.id.items_add_item_fab)
    addItemFab.setOnClickListener(addItemAction)
    val addCategoryFab : FloatingActionButton = root.findViewById(R.id.items_add_category_fab)
    addCategoryFab.setOnClickListener(addCategoryAction)
}

private const val LOG_TAG = "AddItemDepotCategoryFabs"