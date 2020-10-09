package io.github.kn65op.domag.database.operations

import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.database.database.AppDatabase
import io.github.kn65op.domag.database.entities.Consume
import io.github.kn65op.domag.database.entities.Item
import java.time.ZonedDateTime

suspend fun AppDatabase.consumeItem(itemId: Int, consumedAmount: FixedPointNumber) {
    val item = itemDao().findByIdImmediately(itemId)
    val consumedItem = item.consumeItem(consumedAmount)
    if (consumedItem == null) {
        itemDao().delete(item)
    } else {
        itemDao().update(consumedItem)
    }
    val consume =
        Consume(
            amount = consumedAmount,
            categoryId = item.categoryId,
            date = ZonedDateTime.now()
        )
    consumeDao().insert(consume)
}

private fun Item.consumeItem(consumedAmount: FixedPointNumber) = when (consumedAmount) {
    amount -> null
    else -> Item(uid, amount - consumedAmount, depotId, categoryId, description, bestBefore)
}
