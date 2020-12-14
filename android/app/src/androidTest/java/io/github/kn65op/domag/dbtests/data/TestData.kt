package io.github.kn65op.domag.dbtests.data

import android.content.Context
import androidx.room.Room
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.database.relations.CategoryWithContents
import io.github.kn65op.domag.data.database.relations.DepotWithContents
import io.github.kn65op.domag.uitests.common.descriptionCategoryDelimiter
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.data.entities.*
import kotlinx.coroutines.runBlocking
import java.time.ZonedDateTime

const val mainDepot1Name = "name3"
const val mainDepot2Name = "name2"
const val depot1InMainDepot1Name = "depotName"
const val depot2InMainDepot1Name = "depot anoname 2"

val mainDepot1 = DepotWithContents(depot = Depot(name = mainDepot1Name))
val mainDepot2 = DepotWithContents(depot = Depot(name = mainDepot2Name))
val depot1InMainDepot1 = DepotWithContents(Depot(name = depot1InMainDepot1Name, parentId = 1))
val depot2InMainDepot1 = DepotWithContents(Depot(name = depot2InMainDepot1Name, parentId = 1))


const val mainCategory1Name = "category name"
const val mainCategory2Name = "category name2"
const val mainCategory3Name = "Sustenance 3"
const val category1InMainCategory1Name = "categoryName"
const val category2InMainCategory1Name = "category anoname 2"

const val mainCategory1Unit = "l"
const val mainCategory2Unit = "zzz"
const val category1InMainCategory1Unit = "pc"
const val category2InMainCategory1Unit = "kg"
const val someCategoryUnit = "g"

val mainCategory1LimitAmount = FixedPointNumber(10)
val category2InMainCategory1LimitAmount = FixedPointNumber(100)
val someCategoryLimit = FixedPointNumber(50)

val categoryLimitOne =
    CategoryLimit(uid = 1, categoryId = 1, minimumDesiredAmount = mainCategory1LimitAmount)
val mainCategory1Limit = categoryLimitOne
val categoryLimitTwo = CategoryLimit(
    uid = 2,
    categoryId = 3,
    minimumDesiredAmount = category2InMainCategory1LimitAmount
)
val category2inMainCategory1Limit = categoryLimitTwo
val categoryLimitThree =
    CategoryLimit(uid = 3, categoryId = 5, minimumDesiredAmount = someCategoryLimit)
val mainCategory3Limit = categoryLimitThree

val mainCategory1 =
    CategoryWithContents(
        category = Category(name = mainCategory1Name, unit = mainCategory1Unit),
        limits = mainCategory1Limit
    )
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
        ),
        limits = category2inMainCategory1Limit
    )
val mainCategory3 =
    CategoryWithContents(
        category = Category(name = mainCategory3Name, unit = someCategoryUnit),
        limits = mainCategory3Limit
    )

val itemAmount1 = FixedPointNumber(1.0)
val itemAmount2 = FixedPointNumber(1123.3)
val itemAmount3 = FixedPointNumber(2.0)
val itemAmount4 = FixedPointNumber(11.11)
val itemAmount5 = FixedPointNumber(8.8)
val itemAmount6 = FixedPointNumber(0.8)
val itemAmount7 = FixedPointNumber(1.8)

const val item1Description = "Good"
const val item1WholeNameWithCategory =
    "$item1Description$descriptionCategoryDelimiter$mainCategory1Name"
const val item2Description = "BAD"
const val item3Description = "Natoher descirption"
const val item3WholeNameWithCategory =
    "$item3Description$descriptionCategoryDelimiter$mainCategory2Name"

val bestBeforeItem1 = ZonedDateTime.now().plusDays(7)!!
val bestBeforeItem2 = ZonedDateTime.now().minusDays(7)!!
val bestBeforeItem3 = ZonedDateTime.now()!!
val bestBeforeItem5 = ZonedDateTime.now().plusDays(1)!!
val bestBeforeItem6 = ZonedDateTime.now().plusYears(7)!!
val bestBeforeItem7 = ZonedDateTime.now().plusDays(6)!!

val item1 = Item(
    uid = 1,
    depotId = 1,
    categoryId = 1,
    amount = itemAmount1,
    description = item1Description,
    bestBefore = bestBeforeItem1
)
val item2 = Item(
    uid = 2,
    depotId = 3,
    categoryId = 3,
    amount = itemAmount2,
    description = item2Description,
    bestBefore = bestBeforeItem2
)
val item3 = Item(
    uid = 3,
    depotId = 2,
    categoryId = 2,
    amount = itemAmount3,
    description = item3Description,
    bestBefore = bestBeforeItem3
)
val item4 = Item(uid = 4, depotId = 1, categoryId = 3, amount = itemAmount4)
val item5 =
    Item(uid = 5, depotId = 3, categoryId = 3, amount = itemAmount5, bestBefore = bestBeforeItem5)
val item6 =
    Item(uid = 6, depotId = 2, categoryId = 4, amount = itemAmount6, bestBefore = bestBeforeItem6)
val item7 =
    Item(uid = 7, depotId = 4, categoryId = 4, amount = itemAmount7, bestBefore = bestBeforeItem7)

val mainCategory1ItemsAmountCount = itemAmount1
val category1InMainCategoryItemsAmountCount = itemAmount2 + itemAmount4 + itemAmount5
val mainCategory3ItemsAmountCount = FixedPointNumber(0)

val itemsFrom2 = listOf(item2, item3, item4, item5, item6, item7)
val allItemsCount = itemsFrom2.size + 1

val consume1Amount = FixedPointNumber(1.01)
val consume1 = Consume(uid = 1, amount = consume1Amount, date = ZonedDateTime.now(), categoryId = 1)

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
            category1InMainCategory1.category,
        )
    )
    categoryDao.insert(category2InMainCategory1.category)
    categoryDao.insert(mainCategory3.category)

    val categoryLimitDao = db.categoryLimitDao()
    categoryLimitDao.insert(categoryLimitOne)
    categoryLimitDao.insert(categoryLimitTwo)
    categoryLimitDao.insert(categoryLimitThree)

    val itemDao = db.itemDao()
    itemDao.insert(item1)
    itemDao.insert(itemsFrom2)

    val consumeDao = db.consumeDao()
    consumeDao.insert(consume1)
}

fun createDb(context: Context) = Room.inMemoryDatabaseBuilder(
    context, AppDatabase::class.java
).build()

