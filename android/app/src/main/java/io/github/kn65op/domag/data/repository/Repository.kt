package io.github.kn65op.domag.data.repository

import androidx.lifecycle.LiveData
import io.github.kn65op.domag.data.model.Category
import io.github.kn65op.domag.data.model.DataId
import io.github.kn65op.domag.data.model.Depot
import io.github.kn65op.domag.data.model.Item


interface Repository {
    fun getAllCategories(): LiveData<List<Category>>
    fun getCategory(id: DataId): LiveData<Category?>

    fun getAllDepots(): LiveData<List<Depot>>
    fun getDepot(id: DataId): LiveData<Depot?>

    fun getAllItems(): LiveData<List<Item>>
    fun getItem(id: DataId): LiveData<Item?>
}