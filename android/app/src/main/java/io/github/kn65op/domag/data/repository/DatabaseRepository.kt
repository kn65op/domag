package io.github.kn65op.domag.data.repository

import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.database.relations.CategoryWithContents
import io.github.kn65op.domag.data.model.Category
import io.github.kn65op.domag.data.model.DataId
import io.github.kn65op.domag.data.model.Depot
import io.github.kn65op.domag.data.model.Item
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val db: AppDatabase) : Repository {
    override fun getAllCategories(): Flow<List<Category>> =
        flow {
            val allCategoriesFlow = db.categoryDao().getAllWithContentsFlow()
            allCategoriesFlow.collect { dbCategories ->
                val modelCategories = dbCategories.map { toModelCategory(it) }
                emit(modelCategories)
            }
        }

    private fun toModelCategory(category: CategoryWithContents) = Category(
        uid = category.category.uid,
        name = category.category.name,
        unit = category.category.unit,
        minimumDesiredAmount = category.limits?.minimumDesiredAmount,
        parent = null,
        children = emptyList(),
        items = emptyList()
    )

    @JvmName("toModelCategoryNullable")
    private fun toModelCategory(category: CategoryWithContents?) = category?.let { toModelCategory(it) }

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
            val allCategoriesFlow = db.categoryDao().findWithContentsByIdFlow(id)
            allCategoriesFlow.collect { dbCategory ->
                val modelCategory = toModelCategory(dbCategory)
                emit(modelCategory)
            }
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
