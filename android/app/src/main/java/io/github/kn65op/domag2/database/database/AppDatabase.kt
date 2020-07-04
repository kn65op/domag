package io.github.kn65op.domag2.database.database

import androidx.room.*
import io.github.kn65op.domag2.database.converters.ZoneDateTimeConverters
import io.github.kn65op.domag2.database.daos.CategoryDao
import io.github.kn65op.domag2.database.daos.ConsumeDao
import io.github.kn65op.domag2.database.daos.ItemDao
import io.github.kn65op.domag2.database.daos.DepotDao
import io.github.kn65op.domag2.database.entities.Category
import io.github.kn65op.domag2.database.entities.Consume
import io.github.kn65op.domag2.database.entities.Item
import io.github.kn65op.domag2.database.entities.Depot
import io.github.kn65op.android.lib.type.FixedPointNumberConverter

@Database(entities = [Depot::class, Item::class, Category::class, Consume::class], version = 2)
@TypeConverters(FixedPointNumberConverter::class, ZoneDateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun depotDao(): DepotDao
    abstract fun itemDao(): ItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun consumeDao(): ConsumeDao
}
