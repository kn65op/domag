package com.example.domag2.dbtests

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.domag2.database.daos.CategoryDao
import com.example.domag2.dbtests.common.DatabaseTest
import com.example.domag2.dbtests.common.getFromLiveData
import com.example.domag2.dbtests.data.*
import com.example.domag2.matchers.isEqualRegardlessId
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
open class CategoryDatabaseTest  : DatabaseTest() {
    private lateinit var categoryDao: CategoryDao
    val matchCategoryStartingWithName =
        arrayContainingInAnyOrder(
            isEqualRegardlessId(mainCategory1.category),
            isEqualRegardlessId(mainCategory2.category)
        )

    @Before
    fun createDao() {
        categoryDao = db.categoryDao()
    }

    @Test
    fun findCategorysByExactName() {
        val byName = getFromLiveData(categoryDao.findByName(category2InMainCategory1Name))
        assertThat(
            byName.toTypedArray(),
            arrayContainingInAnyOrder(isEqualRegardlessId(category2InMainCategory1.category))
        )
    }

    @Test
    fun findCategorysStartsWith() {
        val byName = getFromLiveData(categoryDao.findByName("name*"))
        assertThat(byName.size, equalTo(2))
        assertThat(
            byName.toTypedArray(),
            matchCategoryStartingWithName
        )
    }

    @Test
    fun getAll() {
        val all = getFromLiveData(categoryDao.getAll())
        assertThat(
            all.toTypedArray(),
            arrayContainingInAnyOrder(
                isEqualRegardlessId(mainCategory1.category),
                isEqualRegardlessId(mainCategory2.category),
                isEqualRegardlessId(category1InMainCategory1.category),
                isEqualRegardlessId(category2InMainCategory1.category)
            )
        )
    }

    @Test
    fun findById() {
        val toFind = getFromLiveData(categoryDao.findByName(mainCategory1.category.name))
        assertThat(toFind.size, equalTo(1))

        val found = getFromLiveData(categoryDao.findWithContentsById(toFind[0].uid as Int))

        assertThat(toFind[0], equalTo(found.category))
    }

    @Test
    fun findByIds() {
        val toFind = getFromLiveData(categoryDao.findByName("name*"))

        assertThat(toFind.size, greaterThan(1))

        val found =
            getFromLiveData(categoryDao.findWithContentsById(toFind.map { it.uid as Int }
                .toTypedArray()))

        assertThat(
            found.map { it.category }.toTypedArray(),
            matchCategoryStartingWithName
        )
    }

    @Test
    fun delete() = runBlocking {
        val toRemove = getFromLiveData(categoryDao.findByName(mainCategory1.category.name))
        assertThat(toRemove.size, equalTo(1))

        categoryDao.delete(toRemove[0])

        val all = getFromLiveData(categoryDao.getAll())

        assertThat(
            all,
            not(hasItem(toRemove[0]))
        )
    }

    @Test
    fun deleteShouldDeleteAlsoAllCategoriesInside() = runBlocking {
        val toRemove = getFromLiveData(categoryDao.findByName(mainCategory1.category.name)).plus(
            getFromLiveData(categoryDao.findByName(category2InMainCategory1Name))).plus(
            getFromLiveData(categoryDao.findByName(category1InMainCategory1Name)))
        assertThat(toRemove.size, equalTo(3))

        categoryDao.deleteWithChildren(toRemove[0])

        val all = getFromLiveData(categoryDao.getAll())

        assertThat(all, not(hasItem(toRemove[0])))
        assertThat(all, not(hasItem(toRemove[1])))
        assertThat(all, not(hasItem(toRemove[2])))
    }

    @Test
    fun deleteShouldDeleteAlsoItemsUnderCategories() = runBlocking {
        val toRemove = getFromLiveData(categoryDao.findByName(mainCategory1Name))
        assertThat(toRemove.size, equalTo(1))

        categoryDao.delete(toRemove[0])

        /*assertNoItemInDb(item1, db)
        assertNoItemInDb(item2, db)
        assertNoItemInDb(item4, db)
        assertNoItemInDb(item5, db)
        assertNoItemInDb(item6, db)
        assertNoItemInDb(item7, db)*/
    }

    @Test
    fun categoryWithItems() = runBlocking {
        val categoryWithItems = getFromLiveData(categoryDao.findWithContentsById(3))

        assertThat(
            categoryWithItems.items.toTypedArray(),
            arrayContainingInAnyOrder(item2, item4, item5)
        )
    }

    @Test
    fun categoryWithChildren() {
        val categoryWithChildren = getFromLiveData(categoryDao.findWithContentsById(1))

        assertThat(
            categoryWithChildren.categories.toTypedArray(), arrayContainingInAnyOrder(
                isEqualRegardlessId(category1InMainCategory1.category), isEqualRegardlessId(
                    category2InMainCategory1.category
                )
            )
        )
    }
}
