package io.github.kn65op.domag.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kn65op.domag.data.entities.CategoryLimit

@Dao
interface CategoryLimitDao {
    @Query("SELECT * FROM categorylimit WHERE uid = :limitId")
    fun getById(limitId : Int): LiveData<CategoryLimit>

    @Insert
    suspend fun insert(category: CategoryLimit)

    @Update
    suspend fun update(category: CategoryLimit)

    @Delete
    suspend fun delete(category: CategoryLimit)

    @Query("SELECT * FROM categorylimit WHERE categoryId = :categoryId")
    suspend fun getByCategoryIdImmediately(categoryId: Int): CategoryLimit?
}
