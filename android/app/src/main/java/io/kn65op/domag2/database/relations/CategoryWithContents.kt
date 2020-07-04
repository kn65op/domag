package io.kn65op.domag2.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import com.kn65op.domag2.database.entities.Category
import com.kn65op.domag2.database.entities.Item

data class CategoryWithContents(
    @Embedded
    val category: Category,

    @Relation(parentColumn = "rowid", entityColumn = "parentId")
    val categories: List<Category> = emptyList(),

    @Relation(parentColumn = "rowid", entityColumn = "categoryId")
    val items: List<Item> = emptyList()
)