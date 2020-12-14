package io.github.kn65op.domag.database.entities

import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Relation

@Entity
data class ItemWithInfo(
    @Embedded
    val item: Item,

    @Relation(parentColumn = "categoryId", entityColumn = "rowid")
    val category: Category? = null,

    @Relation(parentColumn = "depotId", entityColumn = "rowid")
    val depot: Depot? = null,
)
