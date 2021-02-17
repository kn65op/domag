package io.github.kn65op.domag.data.model

import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.utils.types.HasUid

data class Category(
    override val uid: Int? = null,

    val name: String,
    val unit: String,
    val minimumDesiredAmount: FixedPointNumber?,
    val parent: Category?,
    val children: List<Category>,
    val items: List<Item>,
) : HasUid
