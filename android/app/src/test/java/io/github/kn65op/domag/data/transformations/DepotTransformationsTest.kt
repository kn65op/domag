package io.github.kn65op.domag.data.transformations

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.github.kn65op.domag.data.database.relations.DepotWithContents
import io.github.kn65op.domag.data.model.RawDepot
import io.github.kn65op.domag.data.model.Depot as ModelDepot
import io.github.kn65op.domag.data.database.entities.Depot as DbDepot
import org.junit.Test

class DepotTransformationsTest {
    private val uid = 1
    private val name = "Name"
    private val dbDepotBase = DbDepot(uid = uid, parentId = null, name = name,)
    private val dbDepotWithContentsBase = DepotWithContents(depot = dbDepotBase)
    private val modelDepotBase = ModelDepot(
        uid = uid,
        name = name,
        children = emptyList(),
        items = emptyList(),
        parentId = null,
    )
    private val modelRawDepotBase = RawDepot(
        uid = uid,
        name = name,
        parentId = null,
    )
    private val parentId = 2323

    @Test
    fun `should transform depot without parent, children and limit`() {

        assertThat(dbDepotWithContentsBase.toModelDepot(), equalTo(modelDepotBase))
    }

    @Test
    fun `should transform depot to raw depot`() {
        assertThat(dbDepotBase.toModelRawDepot(), equalTo(modelRawDepotBase))
    }

    @Test
    fun `should transform depot without uid`() {
        val db = dbDepotBase.copy(uid = null)
        val dbDepotWithContents = DepotWithContents(depot = db)
        val modelDepot = modelDepotBase.copy(uid = null)

        assertThat(dbDepotWithContents.toModelDepot(), equalTo(modelDepot))
    }

    @Test
    fun `should transform children to raw depots`() {
        val childDepot1Id = 22
        val childDepot1Name = "Child 1"
        val childDepot1 = DbDepot(
            uid = childDepot1Id,
            name = childDepot1Name,
            parentId = uid,
        )
        val childDepot2Id = 222
        val childDepot2Name = "Child 2"
        val childDepot2 = DbDepot(
            uid = childDepot2Id,
            name = childDepot2Name,
            parentId = uid,
        )

        val dbDepot =
            dbDepotWithContentsBase.copy(depots = listOf(childDepot1, childDepot2))

        val childModelDepot1 = RawDepot(
            uid = childDepot1Id,
            name = childDepot1Name,
            parentId = uid,
        )

        val childModelDepot2 = RawDepot(
            uid = childDepot2Id,
            name = childDepot2Name,
            parentId = uid,
        )

        val modelDepot =
            modelDepotBase.copy(children = listOf(childModelDepot1, childModelDepot2))

        assertThat(dbDepot.toModelDepot(), equalTo(modelDepot))
    }

    @Test
    fun `should transform parent id`() {
        val dbDepot = dbDepotBase.copy(parentId = parentId)
        val dbDepotWithContents = dbDepotWithContentsBase.copy(depot = dbDepot)

        val modelDepot = modelDepotBase.copy(parentId = parentId)

        assertThat(dbDepotWithContents.toModelDepot(), equalTo(modelDepot))
    }

    @Test
    fun `should transform parent id in RawDepot`() {
        val dbDepot = dbDepotBase.copy(parentId = parentId)

        val rawModelDepot = modelRawDepotBase.copy(parentId = parentId)

        assertThat(dbDepot.toModelRawDepot(), equalTo(rawModelDepot))
    }
}