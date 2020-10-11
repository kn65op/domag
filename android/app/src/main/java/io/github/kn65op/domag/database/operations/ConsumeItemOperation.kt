package io.github.kn65op.domag.database.operations

import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.database.database.AppDatabase
import io.github.kn65op.domag.database.entities.Consume
import io.github.kn65op.domag.database.entities.Item
import java.time.ZonedDateTime

suspend fun AppDatabase.consumeItem(itemId: Int, consumedAmount: FixedPointNumber) {
    if (consumedAmount == FixedPointNumber(0))
    {
        return
    }
    val item = itemDao().findByIdImmediately(itemId)
    val consumedItem = item.consumeItem(consumedAmount)
    updateItem(consumedItem, item)
    insertConsume(consumedAmount, item)
}

private suspend fun AppDatabase.insertConsume(
    consumedAmount: FixedPointNumber,
    item: Item
) {
    val consume =
        Consume(
            amount = consumedAmount,
            categoryId = item.categoryId,
            date = ZonedDateTime.now()
        )
    consumeDao().insert(consume)
}

private suspend fun AppDatabase.updateItem(
    consumedItem: Item?,
    item: Item
) {
    if (consumedItem == null) {
        itemDao().delete(item)
    } else {
        itemDao().update(consumedItem)
    }
}

private fun Item.consumeItem(consumedAmount: FixedPointNumber) = when {
    consumedAmount == amount -> null
    consumedAmount > amount-> throw NotEnoughAmountToConsume(consumedAmount, amount)
    else -> Item(uid, amount - consumedAmount, depotId, categoryId, description, bestBefore)
}

data class NotEnoughAmountToConsume(
    val requestedAmount: FixedPointNumber,
    val amount: FixedPointNumber
) : Throwable() {

}
