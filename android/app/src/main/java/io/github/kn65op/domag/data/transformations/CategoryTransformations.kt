package io.github.kn65op.domag.data.transformations

import io.github.kn65op.domag.data.database.relations.CategoryWithContents
import io.github.kn65op.domag.data.database.entities.Category as DbCategory
import io.github.kn65op.domag.data.model.Category
import io.github.kn65op.domag.data.model.RawCategory

fun CategoryWithContents.toModelCategory() = Category(
    uid = category.uid,
    name = category.name,
    unit = category.unit,
    minimumDesiredAmount = limits?.minimumDesiredAmount,
    parent = null,
    children = categories.map { it.toModelRawCategory() },
    items = emptyList()
)

fun DbCategory.toModelRawCategory() = RawCategory(
    uid = uid,
    name = name,
    unit = unit
)
