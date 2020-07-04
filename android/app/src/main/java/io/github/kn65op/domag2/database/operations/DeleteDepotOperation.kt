package io.github.kn65op.domag2.database.operations

import io.github.kn65op.domag2.database.database.AppDatabase
import io.github.kn65op.domag2.database.relations.DepotWithContents

suspend fun AppDatabase.deleteDepot(depot: DepotWithContents) {
    val depotDao = depotDao()
    val itemDao = itemDao()
    depot.depots.forEach {
        it.uid?.let { childId ->
            deleteDepot(depotDao.findWithContentsByIdImmediate(childId))
        }
    }
    depot.items.forEach {
        itemDao.delete(it)
    }
    depotDao.delete(depot.depot)
}

