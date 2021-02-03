package io.github.kn65op.domag.data.repository

import androidx.lifecycle.LiveData
import io.github.kn65op.domag.data.database.database.AppDatabase
import io.github.kn65op.domag.data.model.Category
import io.github.kn65op.domag.data.model.DataId
import io.github.kn65op.domag.data.model.Depot
import io.github.kn65op.domag.data.model.Item
import javax.inject.Inject

class DatabaseRepository @Inject constructor(private val db : AppDatabase) : Repository
{
    override fun getAllCategories(): LiveData<List<Category>> {
        TODO("Not yet implemented")
    }

    override fun getAllDepots(): LiveData<List<Depot>> {
        TODO("Not yet implemented")
    }

    override fun getAllItems(): LiveData<List<Item>> {
        TODO("Not yet implemented")
    }

    override fun getCategory(id: DataId): LiveData<Category> {
        TODO("Not yet implemented")
    }

    override fun getDepot(id: DataId): LiveData<Depot> {
        TODO("Not yet implemented")
    }

    override fun getItem(): LiveData<Item> {
        TODO("Not yet implemented")
    }
}
