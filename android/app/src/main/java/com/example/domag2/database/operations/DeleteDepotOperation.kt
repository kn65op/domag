package com.example.domag2.database.operations

import com.example.domag2.database.database.AppDatabase
import com.example.domag2.database.relations.DepotWithContents

suspend fun AppDatabase.deleteDepot(depot : DepotWithContents) {
    val depotDao = depotDao()
    val itemDao = itemDao()
    depot.depot.uid?.let { uid ->
        depot.depots.forEach {
            it.uid?.let { childId ->
                deleteDepot(depotDao.findWithContentsByIdImmediate(childId))
            }
        }
        depot.items.forEach {
            itemDao.delete(it)
        }
    }
    depotDao.deleteOnly(depot.depot)
}

