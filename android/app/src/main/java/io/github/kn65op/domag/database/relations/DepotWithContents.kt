package io.github.kn65op.domag.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import io.github.kn65op.domag.database.entities.Item
import io.github.kn65op.domag.database.entities.Depot

data class DepotWithContents(
    @Embedded
    val depot: Depot,

    @Relation(parentColumn = "rowid", entityColumn = "parentId")
    val depots: List<Depot> = emptyList(),

    @Relation(parentColumn = "rowid", entityColumn = "depotId")
    val items: List<Item> = emptyList()
)