package io.github.kn65op.domag.dbtests.operations

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.database.daos.ItemDao
import io.github.kn65op.domag.database.entities.Consume
import io.github.kn65op.domag.database.entities.Item
import io.github.kn65op.domag.database.operations.consumeItem
import io.github.kn65op.domag.dbtests.common.DatabaseTest
import io.github.kn65op.domag.dbtests.common.assertNoItemInDb
import io.github.kn65op.domag.dbtests.common.expectNoDataInLiveData
import io.github.kn65op.domag.dbtests.common.getFromLiveData
import io.github.kn65op.domag.dbtests.data.item1
import io.github.kn65op.domag.dbtests.data.itemAmount1
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class ConsumeItemOperationTest : DatabaseTest() {
    private lateinit var itemDao: ItemDao
    private val itemId = 1
    private val normalConsumeAmount = FixedPointNumber(0.5)

    @Before
    fun createDao() {
        itemDao = db.itemDao()
    }

    private fun assertConsumeInDb(
        id: Int,
        item: Item,
        amount: FixedPointNumber,
    ) {
        val currentConsume = getFromLiveData(db.consumeDao().findById(id))
        assertThat(currentConsume.categoryId, equalTo(item.categoryId))
        assertThat(currentConsume.amount, equalTo(amount))
    }

    @Test
    fun givenNotAllAmountConsumeItemShouldDecreaseItemAmount() = runBlocking {
        db.consumeItem(itemId, normalConsumeAmount)

        val item = getFromLiveData(itemDao.findById(itemId))

        assertThat(item.amount, CoreMatchers.equalTo(FixedPointNumber(0.5)))
    }

    @Test
    fun givenNotAllAmountConsumeItemShouldCreateConsume() = runBlocking {
        val previousSize = getFromLiveData(db.consumeDao().getAll()).size

        db.consumeItem(itemId, normalConsumeAmount)

        val item = getFromLiveData(itemDao.findById(itemId))

        val consumes = getFromLiveData(db.consumeDao().getAll())
        val newSize = previousSize + 1
        assertThat(consumes.size, equalTo(newSize))

        assertConsumeInDb(newSize, item, normalConsumeAmount)
    }

    @Test
    fun givenAllItemAmountShouldRemoveItem() = runBlocking {
        val previousSize = getFromLiveData(db.consumeDao().getAll()).size

        db.consumeItem(itemId, itemAmount1)

        assertConsumeInDb(previousSize + 1, item1, itemAmount1)
    }

    @Test
    fun givenMoreThenAvailableShouldThrow() = runBlocking {
        val previousSize = getFromLiveData(db.consumeDao().getAll()).size

        db.consumeItem(itemId, normalConsumeAmount)

        val item = getFromLiveData(itemDao.findById(itemId))

        val consumes = getFromLiveData(db.consumeDao().getAll())
        val newSize = previousSize + 1
        assertThat(consumes.size, equalTo(newSize))

        assertConsumeInDb(newSize, item, normalConsumeAmount)
    }
}
