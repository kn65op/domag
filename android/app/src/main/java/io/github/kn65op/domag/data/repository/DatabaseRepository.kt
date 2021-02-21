package io.github.kn65op.domag.data.repository

import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.model.*
import io.github.kn65op.domag.data.transformations.toModelCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val db: AppDatabase) : Repository {
    override fun getAllCategories(): Flow<List<Category>> =
        flow {
            val allCategoriesFlow = db.categoryDao().getAllWithContentsFlow()
            allCategoriesFlow.collect { dbCategories ->
                val modelCategories = dbCategories.map { it.toModelCategory() }
                emit(modelCategories)
            }
        }

    override fun getAllDepots(): Flow<List<Depot>> =
        flow {
            emit(emptyList<Depot>())
        }

    override fun getAllItems(): Flow<List<RawItem>> =
        flow {
            emit(emptyList<RawItem>())
        }

    override fun getCategory(id: DataId): Flow<Category?> =
        flow {
            val allCategoriesFlow = db.categoryDao().findWithContentsByIdFlow(id)
            allCategoriesFlow.collect { dbCategory ->
                val modelCategory = dbCategory?.toModelCategory()
                emit(modelCategory)
            }
        }

    override fun getDepot(id: DataId): Flow<Depot?> =
        flow {
            emit(null)
        }

    override fun getItem(id: DataId): Flow<RawItem?> =
        flow {
            emit(null)
        }
}
