package io.github.kn65op.domag.data.transformations

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.github.kn65op.domag.data.database.relations.CategoryWithContents
import io.github.kn65op.domag.data.model.Category as ModelCategory
import io.github.kn65op.domag.data.database.entities.Category as DbCategory
import org.junit.Test

class CategoryTransformationsTest {

    @Test
    fun `should transform category without parent, children and limit`() {
        val uid = 1
        val name = "Name"
        val unit = "Unit"
        val dbCategory = DbCategory(uid = uid, parentId = null, name = name, unit = unit)
        val dbCategoryWithContents = CategoryWithContents(category = dbCategory)
        val modelCategory = ModelCategory(
            uid = uid,
            name = name,
            unit = unit,
            minimumDesiredAmount = null,
            parent = null,
            children = emptyList(),
            items = emptyList(),
        )

        assertThat(dbCategoryWithContents.toModelCategory(), equalTo(modelCategory))
    }

}