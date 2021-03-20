package io.github.kn65op.domag.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kn65op.domag.data.database.entities.Item
import io.github.kn65op.domag.data.database.relations.ItemWithExtra
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime

@Dao
interface ItemDao {
    @Query("SELECT * FROM item")
    fun getAll(): LiveData<List<Item>>

    @Query("SELECT * FROM item")
    fun getAllFlow(): Flow<List<Item>>

    @Query("SELECT * FROM item WHERE uid = :itemIds")
    fun findByIdImmediately(itemIds: Int): Item

    @Query("SELECT * FROM item WHERE uid = :itemIds")
    fun findById(itemIds: Int): LiveData<Item>

    @Query("SELECT * FROM item WHERE uid = :itemId")
    fun findByIdFlow(itemId: Int): Flow<Item?>

    @Query("SELECT * FROM item WHERE uid = :itemId")
    fun findWithExtraById(itemId: Int): Flow<ItemWithExtra?>

    @Query("SELECT * FROM item WHERE rowid IN (:itemIds)")
    fun findByIds(itemIds: IntArray): LiveData<List<Item>>

    @Query("SELECT * FROM item WHERE rowid IN (:itemIds)")
    fun findByIdsImmediately(itemIds: IntArray): List<Item>

    @Query("SELECT * FROM item WHERE bestBefore < :date ORDER BY bestBefore")
    fun getWithBestBeforeBefore(date: ZonedDateTime?): LiveData<List<Item>>

    @Query("SELECT * FROM item WHERE bestBefore < :date ORDER BY bestBefore")
    fun getWithBestBeforeBeforeFlow(date: ZonedDateTime): Flow<List<ItemWithExtra>>

    @Insert
    suspend fun insert(items: List<Item>)

    @Insert
    suspend fun insert(item: Item)

    @Update
    suspend fun update(item: Item)

    @Delete
    suspend fun delete(item: Item)
}
