package io.github.kn65op.domag.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.model.Category
import io.github.kn65op.domag.data.model.DataId
import io.github.kn65op.domag.data.model.Depot
import io.github.kn65op.domag.data.model.Item
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val db: AppDatabase) : Repository {
    override fun getAllCategories(): LiveData<List<Category>> {
        val categories = MutableLiveData<List<Category>>()
        categories.value = emptyList()
        return categories
    }

    override fun getAllDepots(): LiveData<List<Depot>> {
        val depots = MutableLiveData<List<Depot>>()
        depots.value = emptyList()
        return depots
    }

    override fun getAllItems(): LiveData<List<Item>> {
        val items = MutableLiveData<List<Item>>()
        items.value = emptyList()
        return items
    }

    override fun getCategory(id: DataId): LiveData<Category?> {
        val category = MutableLiveData<Category?>()
        category.value = null
        return category
    }

    override fun getDepot(id: DataId): LiveData<Depot?> {
        val depot = MutableLiveData<Depot?>()
        depot.value = null
        return depot
    }

    override fun getItem(id: DataId): LiveData<Item?> {
        val item = MutableLiveData<Item?>()
        item.value = null
        return item
    }
}
