package io.github.kn65op.domag.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import io.github.kn65op.domag.data.database.entities.Category
import io.github.kn65op.domag.data.database.entities.Item
import io.github.kn65op.domag.data.database.entities.Depot

data class ItemWithExtra(
    @Embedded
    val item: Item,

    @Relation(parentColumn = "categoryId", entityColumn = "rowid")
    val category: Category? = null,

    @Relation(parentColumn = "depotId", entityColumn = "rowid")
    val depot: Depot? = null,
)