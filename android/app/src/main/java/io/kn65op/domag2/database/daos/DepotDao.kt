package io.kn65op.domag2.database.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.kn65op.domag2.database.entities.Depot
import com.kn65op.domag2.database.relations.DepotWithContents

@Dao
interface DepotDao {
    @Transaction
    @Query("SELECT rowid, * FROM depot")
    fun getAll(): LiveData<List<Depot>>

    @Transaction
    @Query("SELECT rowid, * FROM depot")
    fun getAllWithContents(): LiveData<List<DepotWithContents>>

    @Transaction
    @Query("SELECT rowid, * FROM depot WHERE rowid = :depotId")
    suspend fun findWithContentsByIdImmediate(depotId: Int): DepotWithContents

    @Transaction
    @Query("SELECT rowid, * FROM depot WHERE rowid = :depotId")
    fun findWithContentsById(depotId: Int): LiveData<DepotWithContents>

    @Transaction
    @Query("SELECT rowid, * FROM depot WHERE rowid IN (:depotIds)")
    fun findWithContentsById(depotIds: Array<Int>): LiveData<List<DepotWithContents>>

    @Query("SELECT rowid, * FROM depot WHERE rowid = :depotIds")
    fun findById(depotIds: Int): LiveData<Depot>

    @Query("SELECT rowid, * FROM depot WHERE rowid IN (:depotIds)")
    fun findById(depotIds: Array<Int>): LiveData<List<Depot>>

    @Transaction
    @Query("SELECT rowid, * FROM depot WHERE name MATCH :name")
    fun findWithContentsByName(name: String): LiveData<List<DepotWithContents>>

    @Transaction
    @Query("SELECT rowid, * FROM depot WHERE name MATCH :name")
    fun findByName(name: String): LiveData<List<Depot>>

    @Query("SELECT rowid, * FROM depot WHERE parentId IS NULL")
    fun findRootDepots(): LiveData<List<Depot>>

    @Insert
    suspend fun insert(depots: List<Depot>)

    @Insert
    suspend fun insert(depot: Depot)

    @Update
    suspend fun update(depot: Depot)

    @Delete
    suspend fun delete(depot: Depot)
}
