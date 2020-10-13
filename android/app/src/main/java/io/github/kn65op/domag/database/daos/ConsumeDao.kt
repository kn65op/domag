package io.github.kn65op.domag.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kn65op.domag.database.entities.Consume

@Dao
interface ConsumeDao {
    @Query("SELECT * FROM consume")
    fun getAll(): LiveData<List<Consume>>

    @Query("SELECT * FROM consume WHERE uid = :consumeIds")
    fun findByIdImmediately(consumeIds: Int): Consume

    @Query("SELECT * FROM consume WHERE uid = :consumeIds")
    fun findById(consumeIds: Int): LiveData<Consume>

    @Query("SELECT * FROM consume WHERE uid IN (:consumeIds)")
    fun findByIds(consumeIds: IntArray): LiveData<List<Consume>>

    @Query("SELECT * FROM consume WHERE uid IN (:consumeIds)")
    fun findByIdsImmediately(consumeIds: IntArray): List<Consume>

    @Query("SELECT * FROM consume WHERE categoryId = :categoryId")
    fun findByCategoryImediately(categoryId: Int): List<Consume>

    @Insert
    suspend fun insert(consumes: List<Consume>)

    @Insert
    suspend fun insert(consume: Consume)

    @Update
    suspend fun update(consume: Consume)

    @Delete
    suspend fun delete(consume: Consume)
}
