package io.github.kn65op.domag.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kn65op.domag.database.entities.Item
import java.time.OffsetDateTime
import java.time.ZonedDateTime

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAll(): LiveData<List<Item>>

    @Query("SELECT * FROM item WHERE uid = :itemIds")
    fun findByIdImmediately(itemIds: Int): Item

    @Query("SELECT * FROM item WHERE uid = :itemIds")
    fun findById(itemIds: Int): LiveData<Item>

    @Query("SELECT * FROM item WHERE rowid IN (:itemIds)")
    fun findByIds(itemIds: IntArray): LiveData<List<Item>>

    @Query("SELECT * FROM item WHERE rowid IN (:itemIds)")
    fun findByIdsImmediately(itemIds: IntArray): List<Item>

    @Query("SELECT * FROM item WHERE bestBefore < :date")
    fun getWithBestBeforeBefore(date: ZonedDateTime?): LiveData<List<Item>>

    @Insert
    suspend fun insert(items: List<Item>)

    @Insert
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)
}
