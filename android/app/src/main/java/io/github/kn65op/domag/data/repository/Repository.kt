package io.github.kn65op.domag.data.repository

import io.github.kn65op.domag.data.model.Category
import io.github.kn65op.domag.data.model.DataId
import io.github.kn65op.domag.data.model.Depot
import io.github.kn65op.domag.data.model.Item
import kotlinx.coroutines.flow.Flow


interface Repository {
    fun getAllCategories(): Flow<List<Category>>
    fun getCategory(id: DataId): Flow<Category?>

    fun getAllDepots(): Flow<List<Depot>>
    fun getDepot(id: DataId): Flow<Depot?>

    fun getAllItems(): Flow<List<Item>>
    fun getItem(id: DataId): Flow<Item?>
}