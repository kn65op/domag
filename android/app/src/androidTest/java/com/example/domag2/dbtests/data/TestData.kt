package com.example.domag2.dbtests.data

import android.content.Context
import androidx.room.Room
import com.example.domag2.database.database.AppDatabase
import com.example.domag2.database.entities.Category
import com.example.domag2.database.entities.Item
import com.example.domag2.database.entities.Depot
import com.example.domag2.database.relations.CategoryWithContents
import com.example.domag2.database.relations.DepotWithContents
import io.github.kn65op.android.lib.type.FixedPointNumber
import kotlinx.coroutines.runBlocking

val mainDepot1Name = "name3"
val mainDepot2Name = "name2"
val depot1InMainDepot1Name = "depotName"
val depot2InMainDepot1Name = "depot anoname 2"

val mainDepot1 = DepotWithContents(depot = Depot(name = mainDepot1Name))
val mainDepot2 = DepotWithContents(depot = Depot(name = mainDepot2Name))
val depot1InMainDepot1 = DepotWithContents(Depot(name = depot1InMainDepot1Name, parentId = 1))
val depot2InMainDepot1 = DepotWithContents(Depot(name = depot2InMainDepot1Name, parentId = 1))


const val mainCategory1Name = "category name"
const val mainCategory2Name = "category name2"
const val category1InMainCategory1Name = "categoryName"
const val category2InMainCategory1Name = "category anoname 2"

const val mainCategory1Unit = "l"
const val mainCategory2Unit = "zzz"
const val category1InMainCategory1Unit = "szt"
const val category2InMainCategory1Unit = "kg"

val mainCategory1 =
    CategoryWithContents(category = Category(name = mainCategory1Name, unit = mainCategory1Unit))
val mainCategory2 =
    CategoryWithContents(category = Category(name = mainCategory2Name, unit = mainCategory2Unit))
val category1InMainCategory1 = CategoryWithContents(
    Category(
        name = category1InMainCategory1Name,
        parentId = 1,
        unit = category1InMainCategory1Unit
    )
)
val category2InMainCategory1 =
    CategoryWithContents(
        Category(
            name = category2InMainCategory1Name,
            parentId = 1,
            unit = category2InMainCategory1Unit
        )
    )

val itemAmount1 = FixedPointNumber(1.0)
val itemAmount2 = FixedPointNumber(1123.3)
val itemAmount3 = FixedPointNumber(2.0)
val itemAmount4 = FixedPointNumber(11.11)
val itemAmount5 = FixedPointNumber(8.8)
val itemAmount6 = FixedPointNumber(0.8)
val itemAmount7 = FixedPointNumber(1.8)

const val item1Description = "Good"

val item1 = Item(uid = 1, depotId = 1, categoryId = 1, amount = itemAmount1, description = item1Description)
val item2 = Item(uid = 2, depotId = 3, categoryId = 3, amount = itemAmount2)
val item3 = Item(uid = 3, depotId = 2, categoryId = 2, amount = itemAmount3)
val item4 = Item(uid = 4, depotId = 1, categoryId = 3, amount = itemAmount4)
val item5 = Item(uid = 5, depotId = 3, categoryId = 3, amount = itemAmount5)
val item6 = Item(uid = 6, depotId = 2, categoryId = 4, amount = itemAmount6)
val item7 = Item(uid = 7, depotId = 4, categoryId = 4, amount = itemAmount7)

val itemsFrom2 = listOf(item2, item3, item4, item5, item6, item7)
val allItemsCount = itemsFrom2.size + 1

fun fillData(db: AppDatabase) = runBlocking {
    val depotDao = db.depotDao()

    depotDao.insert(listOf(mainDepot1.depot, mainDepot2.depot))
    depotDao.insert(depot1InMainDepot1.depot)
    depotDao.insert(depot2InMainDepot1.depot)

    val categoryDao = db.categoryDao()

    categoryDao.insert(
        listOf(
            mainCategory1.category,
            mainCategory2.category,
            category1InMainCategory1.category
        )
    )
    categoryDao.insert(category2InMainCategory1.category)

    val itemDao = db.itemDao()
    itemDao.insert(item1)
    itemDao.insert(itemsFrom2)
}

fun createDb(context: Context) = Room.inMemoryDatabaseBuilder(
    context, AppDatabase::class.java
).build()

