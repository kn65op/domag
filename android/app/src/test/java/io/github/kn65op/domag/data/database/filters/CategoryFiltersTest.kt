package io.github.kn65op.domag.data.database.filters

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.data.entities.Category
import io.github.kn65op.domag.data.entities.CategoryLimit
import io.github.kn65op.domag.data.entities.Item
import io.github.kn65op.domag.data.database.relations.CategoryWithContents
import org.junit.Test

class CategoryFiltersTest {
    private val categoryId = 2
    private val depotId = 8
    private val amountAboveLimit = FixedPointNumber(1.01)
    private val amountBelowLimit = FixedPointNumber(0.99)
    private val limitAmount = FixedPointNumber(1)
    private val categoryBase = Category(name = "", unit = "")
    private val limit = CategoryLimit(categoryId = categoryId, minimumDesiredAmount = limitAmount)

    private fun getItem(amount: FixedPointNumber) =
        Item(amount = amount, depotId = depotId, categoryId = categoryId)

    @Test
    fun `empty categories should result in empty filtered categories`() =
        assertThat(emptyList<CategoryWithContents>().filterUnderLimit(), equalTo(emptyList()))

    @Test
    fun `category above limit should result be filtered out`() {
        val category = CategoryWithContents(
            limits = limit,
            items = listOf(getItem(amountAboveLimit)),
            category = categoryBase
        )
        assertThat(listOf(category).filterUnderLimit(), equalTo(emptyList()))
    }

    @Test
    fun `category below limit should result be left`() {
        val category = CategoryWithContents(
            limits = limit,
            items = listOf(getItem(amountBelowLimit)),
            category = categoryBase
        )
        assertThat(listOf(category).filterUnderLimit(), equalTo(listOf(category)))
    }

    @Test
    fun `category exactly limit should result be left`() {
        val category = CategoryWithContents(
            limits = limit,
            items = listOf(getItem(limitAmount)),
            category = categoryBase
        )
        assertThat(listOf(category).filterUnderLimit(), equalTo(listOf(category)))
    }

    @Test
    fun `with two item below limit should filter out`() {
        val category = CategoryWithContents(
            limits = limit,
            items = listOf(getItem(amountBelowLimit), getItem(amountBelowLimit)),
            category = categoryBase
        )
        assertThat(listOf(category).filterUnderLimit(), equalTo(emptyList()))
    }

    @Test
    fun `given category with no items shuold be left`() {
        val category = CategoryWithContents(
            limits = limit,
            items = emptyList(),
            category = categoryBase
        )
        assertThat(listOf(category).filterUnderLimit(), equalTo(listOf(category)))
    }
}
