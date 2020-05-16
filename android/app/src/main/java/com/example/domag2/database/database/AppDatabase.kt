package com.example.domag2.database.database

import androidx.room.*
import com.example.domag2.database.daos.CategoryDao
import com.example.domag2.database.daos.ItemDao
import com.example.domag2.database.daos.DepotDao
import com.example.domag2.database.entities.Category
import com.example.domag2.database.entities.Item
import com.example.domag2.database.entities.Depot
import io.github.kn65op.android.lib.type.FixedPointNumberConverter

@Database(entities = [Depot::class, Item::class, Category::class], version = 1)
@TypeConverters(FixedPointNumberConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun depotDao(): DepotDao
    abstract fun itemDao(): ItemDao
    abstract fun categoryDao(): CategoryDao
}
