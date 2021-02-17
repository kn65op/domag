package io.github.kn65op.domag.data.repository

import android.util.Log
import androidx.lifecycle.MutableLiveData
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.database.relations.CategoryWithContents
import io.github.kn65op.domag.data.model.Category
import io.github.kn65op.domag.data.model.DataId
import io.github.kn65op.domag.data.model.Depot
import io.github.kn65op.domag.data.model.Item
import kotlinx.coroutines.flow.*
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val db: AppDatabase) : Repository {
    override fun getAllCategories(): Flow<List<Category>> =
        flow {
            Log.i("KOTEK" , "1")
            val allCategoriesFlow = db.categoryDao().getAllWithContentsFlow()
            Log.i("KOTEK" , "10")
            allCategoriesFlow.collect { dbCategories ->
                Log.i("KOTEK" , "11")
                val modelCategories = dbCategories.map { toModelCategory(it) }
                Log.i("KOTEK" , "12")
                emit(modelCategories)
                Log.i("KOTEK" , "13")
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
