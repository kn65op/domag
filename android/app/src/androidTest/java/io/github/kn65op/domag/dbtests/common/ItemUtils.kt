package io.github.kn65op.domag.dbtests.common

import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.database.entities.Item
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
