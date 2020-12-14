package io.github.kn65op.domag.data.database.utils

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.data.entities.Item
import org.junit.Test

class ItemsUtilsTest {
    private val amount1  = FixedPointNumber(1)
    private val amount2  = FixedPointNumber(2)
    private val amountSum  = FixedPointNumber(3)
    private val categoryId = 2
    private val depotId = 8
    private fun getItem(amount: FixedPointNumber) =
        Item(amount = amount, depotId = depotId, categoryId = categoryId)

    @Test
    fun `amount given empty items should return 0`() {
        val items: List<Item> = emptyList()
        assertThat(items.amount(), equalTo(FixedPointNumber(0)))
    }

    @Test
    fun `amount given one items should return item amount`() {
        val items = listOf(getItem(amount1))
        assertThat(items.amount(), equalTo(amount1))
    }

    @Test
    fun `amount given two items should return items amount sum`() {
        val items = listOf(getItem(amount1), getItem(amount2))
        assertThat(items.amount(), equalTo(amountSum))
    }
}
