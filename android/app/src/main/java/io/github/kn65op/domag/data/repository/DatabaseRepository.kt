package io.github.kn65op.domag.data.repository

import androidx.lifecycle.MutableLiveData
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.model.Category
import io.github.kn65op.domag.data.model.DataId
import io.github.kn65op.domag.data.model.Depot
import io.github.kn65op.domag.data.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val db: AppDatabase) : Repository {
    override fun getAllCategories(): Flow<List<Category>> =
        flow {
            emit(emptyList<Category>())
        }

    override fun getAllDepots(): Flow<List<Depot>> =
        flow {
            emit(emptyList<Depot>())
        }

    override fun getAllItems(): Flow<List<Item>> =
        flow {
            emit(emptyList<Item>())
        }

    override fun getCategory(id: DataId): Flow<Category?> =
        flow {
            emit(null)
        }

    override fun getDepot(id: DataId): Flow<Depot?> =
        flow {
            emit(null)
        }

    override fun getItem(id: DataId): Flow<Item?> =
        flow {
            emit(null)
        }
}
