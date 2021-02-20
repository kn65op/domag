package io.github.kn65op.domag.data.transformations

import io.github.kn65op.domag.data.database.relations.CategoryWithContents
import io.github.kn65op.domag.data.model.Category

fun CategoryWithContents.toModelCategory() = Category(
    uid = category.uid,
    name = category.name,
    unit = category.unit,
    minimumDesiredAmount = limits?.minimumDesiredAmount,
    parent = null,
    children = emptyList(),
    items = emptyList()
)
