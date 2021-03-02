package io.github.kn65op.domag.data.repository

import io.github.kn65op.domag.data.model.*
import kotlinx.coroutines.flow.Flow
import java.time.ZonedDateTime


interface Repository {
    fun getAllCategories(): Flow<List<Category>>
    fun getCategory(id: DataId): Flow<Category?>

    fun getAllDepots(): Flow<List<Depot>>
    fun getDepot(id: DataId): Flow<Depot?>

    fun getAllItems(): Flow<List<RawItem>>
    fun getItem(id: DataId): Flow<RawItem?>
    fun getItemsWithBestBeforeBefore(date: ZonedDateTime): Flow<List<RawItem>>
}