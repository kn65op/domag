package com.example.domag2.matchers

import com.example.domag2.database.entities.Depot
import org.hamcrest.BaseMatcher
import org.hamcrest.Description

class IsDepotEqualRegardlessId(val depot: Depot) : BaseMatcher<Depot>() {
    override fun matches(actual: Any?): Boolean =
        if (actual is Depot)
            depot.name == actual.name
        else
            false

    override fun describeTo(description: Description?) {
        description?.appendText("Should be equal regardless of id")
    }

    override fun describeMismatch(item: Any?, mismatchDescription: Description?) {
        mismatchDescription?.appendText("$item is not equal to $depot")
    }
}

fun isEqualRegardlessId(depot: Depot) = IsDepotEqualRegardlessId(depot)

