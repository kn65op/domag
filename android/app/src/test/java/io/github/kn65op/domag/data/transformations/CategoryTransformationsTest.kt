package io.github.kn65op.domag.data.transformations

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.github.kn65op.domag.data.database.relations.CategoryWithContents
import io.github.kn65op.domag.data.model.Category as ModelCategory
import io.github.kn65op.domag.data.database.entities.Category as DbCategory
import org.junit.Test

class CategoryTransformationsTest {
    private val uid = 1
    private val name = "Name"
    private val unit = "Unit"
    private val dbCategoryBase = DbCategory(uid = uid, parentId = null, name = name, unit = unit)
    private val dbCategoryWithContentsBase = CategoryWithContents(category = dbCategoryBase)
    private val modelCategoryBase = ModelCategory(
        uid = uid,
        name = name,
        unit = unit,
        minimumDesiredAmount = null,
        parent = null,
        children = emptyList(),
        items = emptyList(),
    )

    @Test
    fun `should transform category without parent, children and limit`() {

        assertThat(dbCategoryWithContentsBase.toModelCategory(), equalTo(modelCategoryBase))
    }

    @Test
    fun `should transform category without uid`() {
        val db = dbCategoryBase.copy(uid = null)
        val dbCategoryWithContents = CategoryWithContents(category = db)
        val modelCategory = modelCategoryBase.copy(uid = null)

        assertThat(dbCategoryWithContents.toModelCategory(), equalTo(modelCategory))
    }
}