package io.kn65op.domag2.database.database

import androidx.room.*
import com.kn65op.domag2.database.converters.ZoneDateTimeConverters
import com.kn65op.domag2.database.daos.CategoryDao
import com.kn65op.domag2.database.daos.ConsumeDao
import com.kn65op.domag2.database.daos.ItemDao
import com.kn65op.domag2.database.daos.DepotDao
import com.kn65op.domag2.database.entities.Category
import com.kn65op.domag2.database.entities.Consume
import com.kn65op.domag2.database.entities.Item
import com.kn65op.domag2.database.entities.Depot
import io.github.kn65op.android.lib.type.FixedPointNumberConverter

@Database(entities = [Depot::class, Item::class, Category::class, Consume::class], version = 2)
@TypeConverters(FixedPointNumberConverter::class, ZoneDateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun depotDao(): DepotDao
    abstract fun itemDao(): ItemDao
    abstract fun categoryDao(): CategoryDao
    abstract fun consumeDao(): ConsumeDao
}
