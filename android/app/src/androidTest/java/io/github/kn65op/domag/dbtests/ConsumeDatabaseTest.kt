package io.github.kn65op.domag.dbtests

import com.natpryce.hamkrest.assertion.assertThat
import io.github.kn65op.domag.database.daos.ConsumeDao
import io.github.kn65op.domag.dbtests.common.DatabaseTest
import io.github.kn65op.domag.dbtests.common.getFromLiveData
import io.github.kn65op.domag.dbtests.data.allItemsCount
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
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
        MatcherAssert.assertThat(
            getFromLiveData(consumeDao.getAll()).size,
            CoreMatchers.equalTo(allConsumesCount)
        )
    }
}

