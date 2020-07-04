package io.kn65op.domag2.dbtests.operations

import com.kn65op.domag2.database.daos.CategoryDao
import com.kn65op.domag2.database.operations.deleteCategory
import io.kn65op.domag2.dbtests.common.DatabaseTest
import com.kn65op.domag2.dbtests.common.assertNoItemInDb
import com.kn65op.domag2.dbtests.common.getFromLiveData
import com.kn65op.domag2.dbtests.data.*
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.junit.Before
import org.junit.Test

class DeleteCategoryOperationTest : _root_ide_package_.io.kn65op.domag2.dbtests.common.DatabaseTest() {
    private lateinit var categoryDao:CategoryDao

    @Before
    fun createDao() {
        categoryDao = db.categoryDao()
    }

    @Test
    fun deleteDepotShouldDeleteItemsInDepot() = runBlocking {
        val toRemove = getFromLiveData(categoryDao.findWithContentsByName(mainCategory1Name))
        MatcherAssert.assertThat(toRemove.size, CoreMatchers.equalTo(1))

        db.deleteCategory(toRemove[0])

        assertNoItemInDb(item1, db)
        assertNoItemInDb(item2, db)
        assertNoItemInDb(item4, db)
        assertNoItemInDb(item5, db)
        assertNoItemInDb(item6, db)
        assertNoItemInDb(item7, db)
    }

    @Test
    fun deleteShouldDeleteAlsoAllAllDepotsInside() = runBlocking {
        val toRemove = getFromLiveData(categoryDao.findWithContentsByName(mainCategory1Name))
        MatcherAssert.assertThat(toRemove.size, CoreMatchers.equalTo(1))
        val shouldBeRemoved = getFromLiveData(categoryDao.findByName(mainCategory1Name)).plus(
            getFromLiveData(categoryDao.findByName(category1InMainCategory1Name))
        ).plus(getFromLiveData(categoryDao.findByName(category2InMainCategory1Name)))
        MatcherAssert.assertThat(shouldBeRemoved.size, Matchers.greaterThan(1))

        db.deleteCategory(toRemove[0])

        val all = getFromLiveData(categoryDao.getAll())

        MatcherAssert.assertThat(all, CoreMatchers.not(CoreMatchers.hasItem(shouldBeRemoved[0])))
        MatcherAssert.assertThat(all, CoreMatchers.not(CoreMatchers.hasItem(shouldBeRemoved[1])))
        MatcherAssert.assertThat(all, CoreMatchers.not(CoreMatchers.hasItem(shouldBeRemoved[2])))
    }
}
