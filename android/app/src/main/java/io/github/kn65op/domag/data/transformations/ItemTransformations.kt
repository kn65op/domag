package io.github.kn65op.domag.data.transformations

import io.github.kn65op.domag.data.model.Item
import io.github.kn65op.domag.data.model.RawItem
import io.github.kn65op.domag.data.database.entities.Item as DbItem
import io.github.kn65op.domag.data.database.relations.ItemWithExtra as DbItemWithExtra

fun DbItem.toRawItem() = RawItem(
    uid = uid,
    depotId = depotId,
    categoryId = categoryId,
    amount = amount,
    description = description,
    bestBefore = bestBefore,
)

fun DbItemWithExtra.toItem() = Item(
    uid = item.uid,
    amount = item.amount,
    depot = depot?.toModelRawDepot(),
    category = category?.toModelRawCategory(),
    description = item.description,
    bestBefore = item.bestBefore,
    consumes = emptyList(),
)
