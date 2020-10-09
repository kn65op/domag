package io.github.kn65op.domag.dbtests

import io.github.kn65op.domag.database.daos.ConsumeDao
import io.github.kn65op.domag.dbtests.common.DatabaseTest
import io.github.kn65op.domag.dbtests.common.getFromLiveData
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Before
import org.junit.Test

class ConsumeDatabaseTest : DatabaseTest() {
    private lateinit var consumeDao: ConsumeDao

    @Before
    fun createDao() {
        consumeDao = db.consumeDao()
    }

    @Test
    fun getAll()  = runBlocking{
        val allConsumesCount = 1
        assertThat(
            getFromLiveData(consumeDao.getAll()).size,
            CoreMatchers.equalTo(allConsumesCount)
        )
    }
}

