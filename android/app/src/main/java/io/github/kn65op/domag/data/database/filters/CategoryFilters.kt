package io.github.kn65op.domag.data.database.filters

import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.data.database.relations.CategoryWithContents

fun List<CategoryWithContents>.filterUnderLimit() =
    filter { category ->
        val limits = category.limits
        val allItemsAmount = category.items.map { it.amount }
            .reduceOrNull { acc, number -> acc + number }
        limits != null && limits.minimumDesiredAmount >= allItemsAmount ?: FixedPointNumber(0)
    }
