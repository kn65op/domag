package io.github.kn65op.domag.database.filters

import io.github.kn65op.domag.database.relations.CategoryWithContents

fun List<CategoryWithContents>.filterUnderLimit() =
    filter { category ->
        val limits = category.limits
        limits != null && limits.minimumDesiredAmount >= (category.items.map { it.amount }
            .reduce { acc, number -> acc + number })
    }
