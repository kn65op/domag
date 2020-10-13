package io.github.kn65op.domag.dbtests.operations

import io.github.kn65op.domag.database.daos.CategoryDao
import io.github.kn65op.domag.database.daos.ConsumeDao
import io.github.kn65op.domag.database.entities.Consume
import io.github.kn65op.domag.database.operations.deleteCategory
import io.github.kn65op.domag.database.relations.CategoryWithContents
import io.github.kn65op.domag.dbtests.common.DatabaseTest
import io.github.kn65op.domag.dbtests.common.assertNoItemInDb
import io.github.kn65op.domag.dbtests.common.getFromLiveData
import io.github.kn65op.domag.dbtests.data.*
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test
import java.time.ZonedDateTime

class DeleteCategoryOperationTest : DatabaseTest() {
    private lateinit var categoryDao: CategoryDao
    private lateinit var consumeDao: ConsumeDao

    @Before
    fun createDao() {
        categoryDao = db.categoryDao()
        consumeDao = db.consumeDao()
    }

    private fun findCategoryToRemove(): CategoryWithContents {
        val toRemove = getFromLiveData(categoryDao.findWithContentsByName(mainCategory1Name))
        MatcherAssert.assertThat(toRemove.size, CoreMatchers.equalTo(1))
        return toRemove[0];
    }

    @Test
    fun deleteCategoryShouldDeleteItemsInCategory() = runBlocking {
        val toRemove = findCategoryToRemove()

        db.deleteCategory(toRemove)

        assertNoItemInDb(item1, db)
        assertNoItemInDb(item2, db)
        assertNoItemInDb(item4, db)
        assertNoItemInDb(item5, db)
        assertNoItemInDb(item6, db)
        assertNoItemInDb(item7, db)
    }

    @Test
    fun deleteCategoryShouldRemoveConsumeEntries() = runBlocking {
        val toRemove = findCategoryToRemove()

        MatcherAssert.assertThat(
            getFromLiveData(consumeDao.findByIds(arrayOf(1).toIntArray())).size,
            CoreMatchers.equalTo(1)
        )

        db.deleteCategory(toRemove)

        MatcherAssert.assertThat(
            getFromLiveData(consumeDao.findByIds(arrayOf(1).toIntArray())).size,
            CoreMatchers.equalTo(0)
        )
    }

    @Test
    fun deleteShouldDeleteAlsoAllAllCategoriesInside() = runBlocking {
        val toRemove = findCategoryToRemove()
        val shouldBeRemoved = getFromLiveData(categoryDao.findByName(mainCategory1Name)).plus(
            getFromLiveData(categoryDao.findByName(category1InMainCategory1Name))
        ).plus(getFromLiveData(categoryDao.findByName(category2InMainCategory1Name)))
        MatcherAssert.assertThat(shouldBeRemoved.size, Matchers.greaterThan(1))

        db.deleteCategory(toRemove)

        val all = getFromLiveData(categoryDao.getAll())

        MatcherAssert.assertThat(all, CoreMatchers.not(CoreMatchers.hasItem(shouldBeRemoved[0])))
        MatcherAssert.assertThat(all, CoreMatchers.not(CoreMatchers.hasItem(shouldBeRemoved[1])))
        MatcherAssert.assertThat(all, CoreMatchers.not(CoreMatchers.hasItem(shouldBeRemoved[2])))
    }
}
