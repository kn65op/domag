package io.github.kn65op.domag.data.database.relations

import androidx.room.Embedded
import androidx.room.Relation
import io.github.kn65op.domag.data.entities.Category
import io.github.kn65op.domag.data.entities.CategoryLimit
import io.github.kn65op.domag.data.entities.Item

data class CategoryWithContents(
    @Embedded
    val category: Category,

    @Relation(parentColumn = "rowid", entityColumn = "parentId")
    val categories: List<Category> = emptyList(),

    @Relation(parentColumn = "rowid", entityColumn = "categoryId")
    val items: List<Item> = emptyList(),

    @Relation(parentColumn = "rowid", entityColumn = "categoryId")
    val limits: CategoryLimit? = null,
)