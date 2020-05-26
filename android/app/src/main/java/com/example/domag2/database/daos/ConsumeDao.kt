package com.example.domag2.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.domag2.database.entities.Consume

@Dao
interface ConsumeDao {
    @Query("SELECT * FROM consume")
    fun getAll(): LiveData<List<Consume>>

    @Query("SELECT * FROM consume WHERE uid = :consumeIds")
    fun findByIdImmediately(consumeIds: Int): Consume

    @Query("SELECT * FROM consume WHERE uid = :consumeIds")
    fun findById(consumeIds: Int): LiveData<Consume>

    @Query("SELECT * FROM consume WHERE rowid IN (:consumeIds)")
    fun findByIds(consumeIds: Array<Int>): LiveData<List<Consume>>

    @Query("SELECT * FROM consume WHERE rowid IN (:consumeIds)")
    fun findByIdsImmediately(consumeIds: Array<Int>): List<Consume>

    @Insert
    suspend fun insert(consumes: List<Consume>)

    @Insert
    suspend fun insert(consume: Consume)

    @Update
    suspend fun update(consume: Consume)

    @Delete
    suspend fun delete(consume: Consume)
}
