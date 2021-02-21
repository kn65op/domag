package io.github.kn65op.domag.data.transformations

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.data.model.RawItem
import io.github.kn65op.domag.data.database.entities.Item as DbItem
import org.junit.Test

class ItemTransformationsTest {
    private val uid = 1
    private val amount = FixedPointNumber(23.44)
    private val depotId = 23
    private val categoryId = 83
    private val description = "Description"

    @Test
    fun `should transform Item to RawItem`() {
        val itemBase = DbItem(
            uid = uid,
            amount = amount,
            depotId = depotId,
            categoryId = categoryId,
            description = description,
            bestBefore = null
        )

        val rawItemBase = RawItem(
            uid = uid,
            depotId = depotId,
            categoryId = categoryId,
            amount = amount,
            description = description,
            bestBefore = null,
        )

        assertThat(itemBase.toRawItem(), equalTo(rawItemBase))
    }

}

