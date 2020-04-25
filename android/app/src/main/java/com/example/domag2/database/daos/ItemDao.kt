package com.example.domag2.database.daos

import androidx.room.*
import com.example.domag2.database.entities.Item

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAll(): List<Item>

    @Query("SELECT * FROM item WHERE uid = :itemIds")
    fun findById(itemIds: Int): Item

    @Query("SELECT * FROM item WHERE rowid IN (:itemIds)")
    fun findByIds(itemIds: Array<Int>): List<Item>

    @Insert
    fun insert(items: List<Item>)

    @Insert
    fun insert(item: Item)

    @Update
    fun update(item: Item)

    @Delete
    fun delete(item: Item)
}
