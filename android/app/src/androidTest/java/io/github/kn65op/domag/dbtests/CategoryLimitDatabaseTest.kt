package io.github.kn65op.domag.dbtests

import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.database.daos.CategoryDao
import io.github.kn65op.domag.database.daos.CategoryLimitDao
import io.github.kn65op.domag.database.entities.Category
import io.github.kn65op.domag.database.entities.CategoryLimit
import io.github.kn65op.domag.dbtests.common.DatabaseTest
import io.github.kn65op.domag.dbtests.common.assertNoData
import io.github.kn65op.domag.dbtests.common.getFromLiveData
import io.github.kn65op.domag.dbtests.data.*
import io.github.kn65op.domag.matchers.isEqualRegardlessId
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder
import org.junit.Before
import org.junit.Test

open class CategoryLimitDatabaseTest  : DatabaseTest() {
    private lateinit var categoryLimitDao: CategoryLimitDao

    @Before
    fun createDao() {
        categoryLimitDao = db.categoryLimitDao()
    }

    @Test
    fun findById() {
        val found = getFromLiveData(categoryLimitDao.getById(1))

        assertThat(found, equalTo(categoryLimitOne))
    }

    @Test
    fun findByCategoryId() {
        val found = getFromLiveData(categoryLimitDao.getByCategoryId(3))

        assertThat(found, equalTo(categoryLimitTwo))
    }

    @Test
    fun delete() = runBlocking {
        categoryLimitDao.delete(categoryLimitOne)

         assertNoData(categoryLimitDao.getById(1))
    }

    @Test
    fun update() = runBlocking {
        val updatedLimit = CategoryLimit(uid = 1, categoryId = 1, minimumDesiredAmount = FixedPointNumber(23.41))

        categoryLimitDao.update(updatedLimit)

        val found = getFromLiveData(categoryLimitDao.getById(1))
        assertThat(found, equalTo(updatedLimit))
    }
}
