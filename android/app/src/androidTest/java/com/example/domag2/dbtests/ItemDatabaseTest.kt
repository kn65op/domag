package com.example.domag2.dbtests

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.domag2.database.daos.ItemDao
import com.example.domag2.database.database.AppDatabase
import com.example.domag2.database.entities.withName
import com.example.domag2.dbtests.common.assertItemInDb
import com.example.domag2.dbtests.common.assertNoItemInDb
import com.example.domag2.dbtests.common.getFromLiveData
import com.example.domag2.dbtests.data.*
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.equalTo
import org.hamcrest.MatcherAssert.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class ItemDatabaseTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()
    private lateinit var itemDao: ItemDao
    private lateinit var db: AppDatabase

    @Before
    fun createDb() {
        db = createDb(ApplicationProvider.getApplicationContext())
        itemDao = db.itemDao()
    }

    @Before
    fun fillDatabase() {
        fillData(db)
    }

    @After
    fun closeDb() {
        db.clearAllTables()
        db.close()
    }

    @Test
    fun getAllItems() = runBlocking {
        assertThat(getFromLiveData(itemDao.getAll()).size, equalTo(allItemsCount))
    }

    @Test
    fun getItemById() = runBlocking {
        val item = getFromLiveData(itemDao.findById(1))
        assertThat(item, equalTo(item1))
    }

    @Test
    fun getItemByIdImmediately() = runBlocking {
        val item = itemDao.findByIdImmediately(1)
        assertThat(item, equalTo(item1))
    }

    @Test
    fun getItemsByIds() = runBlocking {
        val items = getFromLiveData(itemDao.findByIds(arrayOf(2, 5)))
        assertThat(items, equalTo(listOf(item2, item5)))
    }

    @Test
    fun getItemsByIdsImmediately() = runBlocking {
        val items = itemDao.findByIdsImmediately(arrayOf(1, 3))
        assertThat(items, equalTo(listOf(item1, item3)))
    }

    @Test
    fun updateItem() = runBlocking {
        val itemToUpdate = getFromLiveData(itemDao.findById(3))
        val updatedItem = itemToUpdate.withName("NewName")

        itemDao.update(updatedItem)

        assertNoItemInDb(itemToUpdate, db)
        assertItemInDb(updatedItem, db)
    }

    @Test
    fun deleteItem() = runBlocking {
        val itemToDelete = getFromLiveData(itemDao.findById(1))

        itemDao.delete(itemToDelete)

        assertNoItemInDb(itemToDelete, db)
    }
}

