package io.github.kn65op.domag.database.filters

import io.github.kn65op.domag.database.relations.CategoryWithContents

fun List<CategoryWithContents>.filterUnderLimit() =
    filter { category ->
        val limits = category.limits
        val allItemsAmount = category.items.map { it.amount }
            .reduceOrNull { acc, number -> acc + number }
        limits != null && allItemsAmount != null && limits.minimumDesiredAmount >= allItemsAmount
    }
