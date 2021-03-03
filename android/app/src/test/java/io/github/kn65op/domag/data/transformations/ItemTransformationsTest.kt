package io.github.kn65op.domag.data.transformations

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.data.database.relations.ItemWithExtra
import io.github.kn65op.domag.data.model.Item
import io.github.kn65op.domag.data.model.RawCategory
import io.github.kn65op.domag.data.model.RawDepot
import io.github.kn65op.domag.data.model.RawItem
import io.github.kn65op.domag.data.database.entities.Item as DbItem
import io.github.kn65op.domag.data.database.entities.Depot as DbDepot
import io.github.kn65op.domag.data.database.entities.Category as DbCategory
import org.junit.Test
import java.time.ZonedDateTime

class ItemTransformationsTest {
    private val uid = 1
    private val amount = FixedPointNumber(23.44)
    private val depotId = 23
    private val categoryId = 83
    private val description = "Description"
    private val categoryName = "Catgegory"
    private val categoryUnit = "Unit"
    private val depotName = "Depot"
    private val dbItemBase = DbItem(
        uid = uid,
        amount = amount,
        depotId = depotId,
        categoryId = categoryId,
        description = description,
        bestBefore = null
    )
    private val category = DbCategory(
        uid = 1,
        parentId = null,
        name = categoryName,
        unit = categoryUnit,
    )
    private val depot = DbDepot(
        uid = 1,
        parentId = null,
        name = depotName,
    )
    private val itemWithExtraBase =
        ItemWithExtra(item = dbItemBase, category = category, depot = depot)

    private val rawItemBase = RawItem(
        uid = uid,
        depotId = depotId,
        categoryId = categoryId,
        amount = amount,
        description = description,
        bestBefore = null,
    )
    private val rawCategory = RawCategory(
        uid = 1,
        name = categoryName,
        unit = categoryUnit,
        parentId = null,
    )
    private val rawDepot = RawDepot(
        uid = 1,
        parentId = null,
        name = depotName,
    )
    private val itemBase = Item(
        uid = uid,
        amount = amount,
        depot = rawDepot,
        category = rawCategory,
        description = description,
        bestBefore = null,
        consumes = emptyList()
    )

    @Test
    fun `should transform Item to RawItem`() {
        assertThat(dbItemBase.toRawItem(), equalTo(rawItemBase))
    }

    @Test
    fun `should transform with best before`() {
        val date = ZonedDateTime.now()

        val item = dbItemBase.copy(bestBefore = date)
        val rawItem = rawItemBase.copy(bestBefore = date)

        assertThat(item.toRawItem(), equalTo(rawItem))
    }

    @Test
    fun `should transform without uid`() {
        val item = dbItemBase.copy(uid = null)
        val rawItem = rawItemBase.copy(uid = null)

        assertThat(item.toRawItem(), equalTo(rawItem))
    }

    @Test
    fun `should transform without description`() {
        val item = dbItemBase.copy(description = null)
        val rawItem = rawItemBase.copy(description = null)

        assertThat(item.toRawItem(), equalTo(rawItem))
    }

    @Test
    fun `should transform ItemWithExtra to Item`() {
        assertThat(itemWithExtraBase.toItem(), equalTo(itemBase))
    }
}

