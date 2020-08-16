package io.github.kn65op.domag.matchers

import io.github.kn65op.domag.database.entities.Category
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class IsCategoryEqualRegardlessId(val category: Category) : BaseMatcher<Category>() {
    override fun matches(actual: Any?): Boolean =
        if (actual is Category)
            category.name == actual.name
        else
            false

    override fun describeTo(description: Description?) {
        description?.appendText("Should be equal regardless of id")
    }

    override fun describeMismatch(item: Any?, mismatchDescription: Description?) {
        mismatchDescription?.appendText("${item as Category} his not equal to $category")
    }
}

fun isEqualRegardlessId(category: Category) = IsCategoryEqualRegardlessId(category)

