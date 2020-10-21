package io.github.kn65op.domag.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kn65op.domag.database.entities.CategoryLimit

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
}
