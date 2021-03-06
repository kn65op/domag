package io.github.kn65op.domag.dbtests

import io.github.kn65op.domag.data.database.daos.DepotDao
import io.github.kn65op.domag.data.database.entities.Depot
import io.github.kn65op.domag.dbtests.common.DatabaseTest
import io.github.kn65op.domag.dbtests.common.getFromLiveData
import io.github.kn65op.domag.dbtests.data.*
import io.github.kn65op.domag.matchers.isEqualRegardlessId
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.hamcrest.CoreMatchers.*
import org.hamcrest.Matcher
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.collection.IsArrayContainingInAnyOrder.arrayContainingInAnyOrder
import org.junit.Before
import org.junit.Test

class DepotDatabaseTest : DatabaseTest() {
    private lateinit var depotDao: DepotDao
    private val matchDepotStartingWithName: Matcher<Array<Depot>> =
        arrayContainingInAnyOrder(
            isEqualRegardlessId(mainDepot1.depot),
            isEqualRegardlessId(mainDepot2.depot)
        )

    @Before
    fun createDao() {
        depotDao = db.depotDao()
    }

    @Test
    fun findDepotsByExactName() {
        val byName = getFromLiveData(depotDao.findByName(depot2InMainDepot1Name))
        assertThat(
            byName.toTypedArray(),
            arrayContainingInAnyOrder(isEqualRegardlessId(depot2InMainDepot1.depot))
        )
    }

    @Test
    fun findDepotsStartsWith() {
        val byName = getFromLiveData(depotDao.findByName("name*"))
        assertThat(byName.size, equalTo(2))
        assertThat(
            byName.toTypedArray(),
            matchDepotStartingWithName
        )
    }

    @Test
    fun getAllLD() {
        val all = getFromLiveData(depotDao.getAll())
        assertThat(
            all.toTypedArray(),
            arrayContainingInAnyOrder(
                isEqualRegardlessId(mainDepot1.depot),
                isEqualRegardlessId(mainDepot2.depot),
                isEqualRegardlessId(depot1InMainDepot1.depot),
                isEqualRegardlessId(depot2InMainDepot1.depot)
            )
        )
    }

    @Test
    fun getAll() = runBlocking {
        val all = depotDao.getAllFlow().first()
        assertThat(
            all.toTypedArray(),
            arrayContainingInAnyOrder(
                isEqualRegardlessId(mainDepot1.depot),
                isEqualRegardlessId(mainDepot2.depot),
                isEqualRegardlessId(depot1InMainDepot1.depot),
                isEqualRegardlessId(depot2InMainDepot1.depot)
            )
        )
    }

    @Test
    fun findByIdLD() {
        val toFind = getFromLiveData(depotDao.findByName(mainDepot1.depot.name))
        assertThat(toFind.size, equalTo(1))

        val found = getFromLiveData(depotDao.findWithContentsById(toFind[0].uid as Int))

        assertThat(toFind[0], equalTo(found.depot))
    }

    @Test
    fun findById() = runBlocking {
        val toFind = getFromLiveData(depotDao.findByName(mainDepot1.depot.name))
        assertThat(toFind.size, equalTo(1))

        val found = depotDao.findByIdFlow(toFind[0].uid as Int).first()

        assertThat(toFind[0], equalTo(found?.depot))
    }

    @Test
    fun findByIds() {
        val toFind = getFromLiveData(depotDao.findByName("name*"))

        assertThat(toFind.size, greaterThan(1))

        val found =
            getFromLiveData(depotDao.findWithContentsById(toFind.map { it.uid as Int }
                .toIntArray()))

        assertThat(
            found.map { it.depot }.toTypedArray(),
            matchDepotStartingWithName
        )
    }

    @Test
    fun delete() = runBlocking {
        val toRemove = getFromLiveData(depotDao.findByName(mainDepot1.depot.name))
        assertThat(toRemove.size, equalTo(1))

        depotDao.delete(toRemove[0])

        val all = getFromLiveData(depotDao.getAll())

        assertThat(all, not(hasItem(toRemove[0])))
    }

    @Test
    fun depotWithItems() {
        val depotWithItems = getFromLiveData(depotDao.findWithContentsById(3))

        assertThat(
            depotWithItems.items.toTypedArray(),
            arrayContainingInAnyOrder(item2, item5)
        )
    }

    @Test
    fun depotWithChildren() {
        val depotWithChildren = getFromLiveData(depotDao.findWithContentsById(1))

        assertThat(
            depotWithChildren.depots.toTypedArray(), arrayContainingInAnyOrder(
                isEqualRegardlessId(depot1InMainDepot1.depot), isEqualRegardlessId(
                    depot2InMainDepot1.depot
                )
            )
        )
    }

    @Test
    fun rootDepots() {
        val rootDepots = getFromLiveData(depotDao.findRootDepots())

        assertThat(
            rootDepots.toTypedArray(), arrayContainingInAnyOrder(
                isEqualRegardlessId(mainDepot1.depot), isEqualRegardlessId(mainDepot2.depot)
            )
        )
    }

    @Test
    fun getName() = runBlocking {
        assertThat(depotDao.getDepotName(1), equalTo(mainDepot1Name))
    }
}
