package io.github.kn65op.domag.data.transformations

import io.github.kn65op.domag.data.database.relations.DepotWithContents
import io.github.kn65op.domag.data.database.entities.Depot as DbDepot
import io.github.kn65op.domag.data.model.Depot
import io.github.kn65op.domag.data.model.RawDepot

fun DepotWithContents.toModelDepot() = Depot(
    uid = depot.uid,
    name = depot.name,
    parentId = depot.parentId,
    children = depots.map { it.toModelRawDepot() },
    items = items.map { it.toRawItem() }
)

fun DbDepot.toModelRawDepot() = RawDepot(
    uid = uid,
    name = name,
    parentId = parentId,
)
