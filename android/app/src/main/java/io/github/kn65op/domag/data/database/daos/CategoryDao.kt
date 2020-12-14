package io.github.kn65op.domag.data.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import io.github.kn65op.domag.data.entities.Category
import io.github.kn65op.domag.data.database.relations.CategoryWithContents

@Dao
interface CategoryDao {
    @Transaction
    @Query("SELECT rowid, * FROM category")
    fun getAll(): LiveData<List<Category>>

    @Transaction
    @Query("SELECT rowid, * FROM category")
    fun getAllWithContents(): LiveData<List<CategoryWithContents>>

    @Transaction
    @Query("SELECT rowid, * FROM category WHERE rowid = :categoryIds")
    fun findWithContentsById(categoryIds: Int): LiveData<CategoryWithContents>

    @Transaction
    @Query("SELECT rowid, * FROM category WHERE rowid = :categoryIds")
    suspend fun findWithContentsByIdImmediately(categoryIds: Int): CategoryWithContents

    @Transaction
    @Query("SELECT rowid, * FROM category WHERE rowid IN (:categoryIds)")
    fun findWithContentsById(categoryIds: IntArray): LiveData<List<CategoryWithContents>>

    @Query("SELECT rowid, * FROM category WHERE rowid = :categoryIds")
    fun findById(categoryIds: Int): LiveData<Category>

    @Query("SELECT rowid, * FROM category WHERE rowid IN (:categoryIds)")
    fun findById(categoryIds: IntArray): LiveData<List<Category>>

    @Transaction
    @Query("SELECT rowid, * FROM category WHERE name MATCH :name")
    fun findWithContentsByName(name: String): LiveData<List<CategoryWithContents>>

    @Transaction
    @Query("SELECT rowid, * FROM category WHERE name MATCH :name")
    fun findByName(name: String): LiveData<List<Category>>

    @Query("SELECT rowid, * FROM category WHERE parentId IS NULL")
    fun findRootDepots(): LiveData<List<Category>>

    @Query("SELECT name FROM category WHERE rowId = :id")
    fun getCategoryName(id: Int): String

    @Query("SELECT unit FROM category WHERE rowId = :id")
    fun getCategoryUnit(id: Int): String

    @Insert
    suspend fun insert(categorys: List<Category>)

    @Insert
    suspend fun insert(category: Category) : Long

    @Update
    suspend fun update(category: Category)

    @Delete
    suspend fun delete(category: Category)
}
