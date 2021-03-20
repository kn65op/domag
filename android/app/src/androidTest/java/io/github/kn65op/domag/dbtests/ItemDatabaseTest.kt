package io.github.kn65op.domag.dbtests

import com.natpryce.hamkrest.absent
import com.natpryce.hamkrest.assertion.assertThat
import io.github.kn65op.domag.data.database.daos.ItemDao
import io.github.kn65op.domag.data.database.entities.withDescription
import io.github.kn65op.domag.data.database.relations.ItemWithExtra
import io.github.kn65op.domag.dbtests.common.DatabaseTest
import io.github.kn65op.domag.dbtests.common.assertItemInDb
import io.github.kn65op.domag.dbtests.common.assertNoItemInDb
import io.github.kn65op.domag.dbtests.common.getFromLiveData
import io.github.kn65op.domag.dbtests.data.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime

class ItemDatabaseTest : DatabaseTest() {
    private lateinit var itemDao: ItemDao

    private val someId = 1
    private val notExistingId = 181128

    @Before
    fun createDao() {
        itemDao = db.itemDao()
    }

    @Test
    fun getAllItemsLD() = runBlocking {
        assertThat(getFromLiveData(itemDao.getAll()).size, equalTo(allItemsCount))
    }

    @Test
    fun getAllItems() = runBlocking {
        assertThat(itemDao.getAllFlow().first().size, equalTo(allItemsCount))
    }

    @Test
    fun getItemByIdLD() = runBlocking {
        val item = getFromLiveData(itemDao.findById(1))
        assertThat(item, equalTo(item1))
    }

    @Test
    fun getItemById() = runBlocking {
        val item = itemDao.findByIdFlow(1).first()
        assertThat(item, equalTo(item1))
    }

    @Test
    fun getItemByIdImmediately() = runBlocking {
        val item = itemDao.findByIdImmediately(1)
        assertThat(item, equalTo(item1))
    }

    @Test
    fun getItemsByIds() = runBlocking {
        val items = getFromLiveData(itemDao.findByIds(arrayOf(2, 5).toIntArray()))
        assertThat(items, equalTo(listOf(item2, item5)))
    }

    @Test
    fun getItemsByIdsImmediately() = runBlocking {
        val items = itemDao.findByIdsImmediately(arrayOf(1, 3).toIntArray())
        assertThat(items, equalTo(listOf(item1, item3)))
    }

    @Test
    fun updateItem() = runBlocking {
        val itemToUpdate = getFromLiveData(itemDao.findById(3))
        val updatedItem = itemToUpdate.withDescription("NewName")

        itemDao.update(updatedItem)

        assertNoItemInDb(itemToUpdate, db)
        assertItemInDb(updatedItem, db)
    }

    @Test
    fun getItesmsWithBetsBeforeBeforeDateLD() = runBlocking {
        val items = getFromLiveData(
            itemDao.getWithBestBeforeBefore(
                ZonedDateTime.now().plusDays(7).minusHours(1)
            )
        )

        assertThat(items, equalTo(listOf(item2, item3, item5, item7)))
    }

    @Test
    fun getItesmsWithBetsBeforeBeforeDate() = runBlocking {
        val items =
            itemDao.getWithBestBeforeBeforeFlow(
                ZonedDateTime.now().plusDays(7).minusHours(1)
            ).first()

        assertThat(
            items,
            equalTo(listOf(itemWithExtra2, itemWithExtra3, itemWithExtra5, itemWithExtra7))
        )
    }

    @Test
    fun deleteItem() = runBlocking {
        val itemToDelete = getFromLiveData(itemDao.findById(1))

        itemDao.delete(itemToDelete)

        assertNoItemInDb(itemToDelete, db)
    }

    @Test
    fun getNotExistingItemWithExtra() = runBlocking {
        val item = itemDao.findWithExtraById(notExistingId).first()

        assertThat(item, absent())
    }

    @Test
    fun getItemWithExtra() = runBlocking {
        val item = itemDao.findWithExtraById(someId).first()
        val onlyItem = itemDao.findByIdFlow(someId).first()

        assertThat(
            item,
            equalTo(
                ItemWithExtra(
                    item = onlyItem!!,
                    category = mainCategory1.category.copy(uid = 1),
                    depot = mainDepot1.depot.copy(uid = 1)
                )
            )
        )
    }
}

