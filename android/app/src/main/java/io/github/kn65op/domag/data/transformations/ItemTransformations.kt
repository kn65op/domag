package io.github.kn65op.domag.data.transformations

import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.data.model.RawItem
import io.github.kn65op.domag.data.database.entities.Item as DbItem

fun DbItem.toRawItem() = RawItem(
    uid = uid,
    depotId = depotId,
    categoryId = categoryId,
    amount = amount,
    description = description,
    bestBefore = bestBefore,
)
