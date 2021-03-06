package io.github.kn65op.domag.data.repository

import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.model.*
import io.github.kn65op.domag.data.transformations.toModelCategory
import io.github.kn65op.domag.data.transformations.toModelDepot
import io.github.kn65op.domag.data.transformations.toModelRawDepot
import io.github.kn65op.domag.data.transformations.toRawItem
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val db: AppDatabase) : Repository {
    override fun getAllCategories(): Flow<List<Category>> =
        flow {
            val allCategoriesFlow = db.categoryDao().getAllWithContentsFlow()
            allCategoriesFlow.collect { dbCategories ->
                emit(dbCategories.map { it.toModelCategory() })
            }
        }

    override fun getAllDepots(): Flow<List<Depot>> =
        flow {
            val allDepotsFlow = db.depotDao().getAllWithContentsFlow()
            allDepotsFlow.collect { dbDepots ->
                emit(dbDepots.map { it.toModelDepot() })
            }
        }

    override fun getAllItems(): Flow<List<RawItem>> =
        flow {
            val allItemsFlow = db.itemDao().getAllFlow()
            allItemsFlow.collect { item ->
                emit(item.map { it.toRawItem() })
            }
        }

    override fun getCategory(id: DataId): Flow<Category?> =
        flow {
            val categoryFlow = db.categoryDao().findWithContentsByIdFlow(id)
            categoryFlow.collect { dbCategory ->
                emit(dbCategory?.toModelCategory())
            }
        }

    override fun getDepot(id: DataId): Flow<Depot?> =
        flow {
            val depotFlow = db.depotDao().findByIdFlow(id)
            depotFlow.collect { dbCategory ->
                emit(dbCategory?.toModelDepot())
            }
        }

    override fun getItem(id: DataId): Flow<RawItem?> =
        flow {
            val itemFlow = db.itemDao().findByIdFlow(id)
            itemFlow.collect { dbItem ->
                emit(dbItem?.toRawItem())
            }
        }
}
