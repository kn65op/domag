package io.github.kn65op.domag.dbtests

import io.github.kn65op.domag.data.database.daos.CategoryDao
import io.github.kn65op.domag.data.database.entities.Category
import io.github.kn65op.domag.dbtests.common.DatabaseTest
import io.github.kn65op.domag.dbtests.common.getFromLiveData
import io.github.kn65op.domag.dbtests.data.*
import io.github.kn65op.domag.matchers.isEqualRegardlessId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder
import org.junit.Before
import org.junit.Test

open class CategoryDatabaseTest : DatabaseTest() {
    private lateinit var categoryDao: CategoryDao
    private val matchCategoryStartingWithName: Matcher<Array<Category>> =
        arrayContainingInAnyOrder(
            isEqualRegardlessId(mainCategory1.category),
            isEqualRegardlessId(mainCategory2.category)
        )

    @Before
    fun createDao() {
        categoryDao = db.categoryDao()
    }

    @Test
    fun findCategorysByExactNameLD() {
        val byName = getFromLiveData(categoryDao.findByName(category2InMainCategory1Name))
        assertThat(
            byName.toTypedArray(),
            arrayContainingInAnyOrder(isEqualRegardlessId(category2InMainCategory1.category))
        )
    }

    @Test
    fun findCategorysByExactName() = runBlocking {
        val byName = categoryDao.findByNameFlow(category2InMainCategory1Name).first()
        assertThat(
            byName.toTypedArray(),
            arrayContainingInAnyOrder(isEqualRegardlessId(category2InMainCategory1.category))
        )
    }

    @Test
    fun findCategorysStartsWithLD() {
        val byName = getFromLiveData(categoryDao.findByName("name*"))
        assertThat(byName.size, equalTo(2))
        assertThat(
            byName.toTypedArray(),
            matchCategoryStartingWithName
        )
    }

    @Test
    fun findCategorysStartsWith() = runBlocking {
        val byName = categoryDao.findByNameFlow("name*").first()
        assertThat(byName.size, equalTo(2))
        assertThat(
            byName.toTypedArray(),
            matchCategoryStartingWithName
        )
    }

    @Test
    fun getAllLD() {
        val all = getFromLiveData(categoryDao.getAll())
        assertThat(
            all.toTypedArray(),
            arrayContainingInAnyOrder(
                isEqualRegardlessId(mainCategory1.category),
                isEqualRegardlessId(mainCategory2.category),
                isEqualRegardlessId(category1InMainCategory1.category),
                isEqualRegardlessId(category2InMainCategory1.category),
                isEqualRegardlessId(mainCategory3.category)
            )
        )
    }

    @Test
    fun getAll() = runBlocking {
        val all = categoryDao.getAllFlow().first()
        assertThat(
            all.toTypedArray(),
            arrayContainingInAnyOrder(
                isEqualRegardlessId(mainCategory1.category),
                isEqualRegardlessId(mainCategory2.category),
                isEqualRegardlessId(category1InMainCategory1.category),
                isEqualRegardlessId(category2InMainCategory1.category),
                isEqualRegardlessId(mainCategory3.category)
            )
        )
    }

    @Test
    fun findByIdLD() {
        val toFind = getFromLiveData(categoryDao.findByName(mainCategory1.category.name))
        assertThat(toFind.size, equalTo(1))

        val found = getFromLiveData(categoryDao.findWithContentsById(toFind[0].uid as Int))

        assertThat(toFind[0], equalTo(found.category))
    }

    @Test
    fun findById() = runBlocking {
        val toFind = categoryDao.findByNameFlow(mainCategory1.category.name).first()
        assertThat(toFind.size, equalTo(1))

        val found = getFromLiveData(categoryDao.findWithContentsById(toFind[0].uid as Int))

        assertThat(toFind[0], equalTo(found.category))
    }

    @Test
    fun findByIdsLD() {
        val toFind = getFromLiveData(categoryDao.findByName("name*"))

        assertThat(toFind.size, greaterThan(1))

        val found =
            getFromLiveData(categoryDao.findWithContentsById(toFind.map { it.uid as Int }
                .toIntArray()))

        assertThat(
            found.map { it.category }.toTypedArray(),
            matchCategoryStartingWithName
        )
    }

    @Test
    fun findByIds() = runBlocking {
        val toFind = categoryDao.findByNameFlow("name*").first()

        assertThat(toFind.size, greaterThan(1))

        val found =
            getFromLiveData(categoryDao.findWithContentsById(toFind.map { it.uid as Int }
                .toIntArray()))

        assertThat(
            found.map { it.category }.toTypedArray(),
            matchCategoryStartingWithName
        )
    }

    @Test
    fun delete() = runBlocking {
        val toRemove = categoryDao.findByNameFlow(mainCategory1.category.name).first()
        assertThat(toRemove.size, equalTo(1))

        categoryDao.delete(toRemove[0])

        val all = getFromLiveData(categoryDao.getAll())

        assertThat(
            all,
            not(hasItem(toRemove[0]))
        )
    }

    @Test
    fun getName() = runBlocking {
        assertThat(categoryDao.getCategoryName(1), equalTo(mainCategory1Name))
    }

    @Test
    fun categoryWithItemsLD() = runBlocking {
        val categoryWithItems = getFromLiveData(categoryDao.findWithContentsById(3))

        assertThat(
            categoryWithItems.items.toTypedArray(),
            arrayContainingInAnyOrder(item2, item4, item5)
        )
    }

    @Test
    fun categoryWithItems() = runBlocking {
        val categoryWithItems = categoryDao.findWithContentsByIdFlow(3).first()

        assertThat(
            categoryWithItems?.items?.toTypedArray(),
            arrayContainingInAnyOrder(item2, item4, item5)
        )
    }

    @Test
    fun categoryWithChildrenLD() {
        val categoryWithChildren = getFromLiveData(categoryDao.findWithContentsById(1))

        assertThat(
            categoryWithChildren.categories.toTypedArray(), arrayContainingInAnyOrder(
                isEqualRegardlessId(category1InMainCategory1.category), isEqualRegardlessId(
                    category2InMainCategory1.category
                )
            )
        )
    }

    @Test
    fun categoryWithChildren() = runBlocking {
        val categoryWithChildren = categoryDao.findWithContentsByIdFlow(1).first()

        assertThat(
            categoryWithChildren?.categories?.toTypedArray(), arrayContainingInAnyOrder(
                isEqualRegardlessId(category1InMainCategory1.category), isEqualRegardlessId(
                    category2InMainCategory1.category
                )
            )
        )
    }

    @Test
    fun getUnit() = runBlocking {
        assertThat(categoryDao.getCategoryUnit(1), equalTo(mainCategory1Unit))
    }
}
