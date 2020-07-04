package io.github.kn65op.domag2.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import io.github.kn65op.domag2.database.entities.Item
import io.github.kn65op.domag2.database.entities.Depot

data class DepotWithContents(
    @Embedded
    val depot: Depot,

    @Relation(parentColumn = "rowid", entityColumn = "parentId")
    val depots: List<Depot> = emptyList(),

    @Relation(parentColumn = "rowid", entityColumn = "depotId")
    val items: List<Item> = emptyList()
)