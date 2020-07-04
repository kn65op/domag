package io.github.kn65op.domag2.dbtests.common

import io.github.kn65op.domag2.database.database.AppDatabase
import io.github.kn65op.domag2.database.entities.Item
import io.github.kn65op.domag2.dbtests.common.getFromLiveData
import org.hamcrest.CoreMatchers
import org.hamcrest.MatcherAssert

fun assertNoItemInDb(itemToDelete: Item, db : AppDatabase) {
    val itemDao = db.itemDao()
    MatcherAssert.assertThat(
        getFromLiveData(itemDao.getAll()),
        CoreMatchers.not(CoreMatchers.hasItem(itemToDelete))
    )
}

fun assertItemInDb(itemToDelete: Item, db : AppDatabase) {
    val itemDao = db.itemDao()
    MatcherAssert.assertThat(getFromLiveData(itemDao.getAll()), CoreMatchers.hasItem(itemToDelete))
}
