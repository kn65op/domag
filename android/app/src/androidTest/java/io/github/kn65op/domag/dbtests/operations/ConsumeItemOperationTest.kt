package io.github.kn65op.domag.dbtests.operations

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.database.daos.ItemDao
import io.github.kn65op.domag.database.entities.Consume
import io.github.kn65op.domag.database.operations.consumeItem
import io.github.kn65op.domag.dbtests.common.DatabaseTest
import io.github.kn65op.domag.dbtests.common.getFromLiveData
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

        val currentConsume = getFromLiveData(db.consumeDao().findById(newSize))
        assertThat(currentConsume.categoryId, equalTo(item.categoryId))
        assertThat(currentConsume.amount, equalTo(normalConsumeAmount))
    }
}
