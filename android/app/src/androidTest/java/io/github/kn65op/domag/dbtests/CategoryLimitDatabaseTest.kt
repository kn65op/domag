package io.github.kn65op.domag.dbtests

import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.data.database.daos.CategoryLimitDao
import io.github.kn65op.domag.data.database.entities.CategoryLimit
import io.github.kn65op.domag.dbtests.common.DatabaseTest
import io.github.kn65op.domag.dbtests.common.assertNoData
import io.github.kn65op.domag.dbtests.common.getFromLiveData
import io.github.kn65op.domag.dbtests.data.*
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

open class CategoryLimitDatabaseTest : DatabaseTest() {
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
    fun findByCategoryId() = runBlocking {
        val found = categoryLimitDao.getByCategoryIdImmediately(3)

        assertThat(found, equalTo(categoryLimitTwo))
    }

    @Test
    fun findByNotExistingCategoryId() = runBlocking {
        val found = categoryLimitDao.getByCategoryIdImmediately(2)

        assertThat(found, equalTo(null))
    }

    @Test
    fun delete() = runBlocking {
        categoryLimitDao.delete(categoryLimitOne)

        assertNoData(categoryLimitDao.getById(1))
    }

    @Test
    fun update() = runBlocking {
        val updatedLimit =
            CategoryLimit(uid = 1, categoryId = 1, minimumDesiredAmount = FixedPointNumber(23.41))

        categoryLimitDao.update(updatedLimit)

        val found = getFromLiveData(categoryLimitDao.getById(1))
        assertThat(found, equalTo(updatedLimit))
    }
}
