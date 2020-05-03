package com.example.domag2.dbtests.operations

import com.example.domag2.database.daos.DepotDao
import com.example.domag2.database.operations.deleteDepot
import com.example.domag2.dbtests.common.DatabaseTest
import com.example.domag2.dbtests.common.assertNoItemInDb
import com.example.domag2.dbtests.common.getFromLiveData
import com.example.domag2.dbtests.data.*
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert
import org.junit.Before
import org.junit.Test

class DeleteDepotOperationTest : DatabaseTest() {
    private lateinit var depotDao: DepotDao

    @Before
    fun createDao() {
        depotDao = db.depotDao()
    }

    @Test
    fun deleteDepotShouldDeleteItemsInDepot() = runBlocking {
        val toRemove = getFromLiveData(depotDao.findWithContentsByName(mainDepot1.depot.name))
        MatcherAssert.assertThat(toRemove.size, CoreMatchers.equalTo(1))

        db.deleteDepot(toRemove[0])

        assertNoItemInDb(item1, db)
        assertNoItemInDb(item2, db)
        assertNoItemInDb(item4, db)
        assertNoItemInDb(item5, db)
        assertNoItemInDb(item7, db)
    }
}
