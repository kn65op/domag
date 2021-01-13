package io.github.kn65op.domag.data.database.database

import androidx.room.*
import io.github.kn65op.domag.data.database.converters.ZoneDateTimeConverters
import io.github.kn65op.android.lib.type.FixedPointNumberConverter
import io.github.kn65op.domag.data.database.daos.*
import io.github.kn65op.domag.data.entities.*

@Database(
    entities = [Depot::class, Item::class, Category::class, Consume::class, CategoryLimit::class],
    version = 4
)
@TypeConverters(FixedPointNumberConverter::class, ZoneDateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun depotDao(): DepotDao
    abstract fun itemDao(): ItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun consumeDao(): ConsumeDao
    abstract fun categoryLimitDao(): CategoryLimitDao
}
