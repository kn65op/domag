package io.github.kn65op.domag.data.operations

import android.util.Log
import io.github.kn65op.domag.data.model.Depot
import io.github.kn65op.domag.data.repository.Repository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

fun getRootDepots(dataRepository : Repository): Flow<Depot> = flow {
    Log.i("KOT", "1")
    val rootDepots = dataRepository.getRootDepots()
    Log.i("KOT", "2")
    rootDepots.collect { depots ->
        Log.i("KOT", "3")
        emit(Depot(null, "Items - titile", null, depots, emptyList()))
    }
    Log.i("KOT", "4")
}
