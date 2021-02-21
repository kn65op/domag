package io.github.kn65op.domag.data.transformations

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.data.model.RawItem
import io.github.kn65op.domag.data.database.entities.Item as DbItem
import org.junit.Test
import java.time.ZonedDateTime

class ItemTransformationsTest {
    private val uid = 1
    private val amount = FixedPointNumber(23.44)
    private val depotId = 23
    private val categoryId = 83
    private val description = "Description"
    private val itemBase = DbItem(
        uid = uid,
        amount = amount,
        depotId = depotId,
        categoryId = categoryId,
        description = description,
        bestBefore = null
    )

    private val rawItemBase = RawItem(
        uid = uid,
        depotId = depotId,
        categoryId = categoryId,
        amount = amount,
        description = description,
        bestBefore = null,
    )

    @Test
    fun `should transform Item to RawItem`() {
        assertThat(itemBase.toRawItem(), equalTo(rawItemBase))
    }

    @Test
    fun `should transform with best before`() {
        val date = ZonedDateTime.now()

        val item = itemBase.copy(bestBefore = date)
        val rawItem = rawItemBase.copy(bestBefore = date)

        assertThat(item.toRawItem(), equalTo(rawItem))
    }

    @Test
    fun `should transform without uid`() {
        val item = itemBase.copy(uid = null)
        val rawItem = rawItemBase.copy(uid = null)

        assertThat(item.toRawItem(), equalTo(rawItem))
    }

    @Test
    fun `should transform without description`() {
        val item = itemBase.copy(description = null)
        val rawItem = rawItemBase.copy(description = null)

        assertThat(item.toRawItem(), equalTo(rawItem))
    }
}

