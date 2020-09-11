package io.github.kn65op.domag.utils

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import io.github.kn65op.domag.database.entities.Category
import org.junit.Test

class TestUidAndParent(parentId: Int?, uid: Int?) : HasParent, HasUid {
    override val parentId: Int? = parentId
    override val uid: Int? = uid

}

class HierarchyTest {
    private val nullParent = TestUidAndParent(null, null)

    @Test
    fun `Given empty list should return empty list`() {
        assertThat(getAllButNotDescendants(nullParent, emptyList()), equalTo(emptyList()))
    }

    @Test
    fun `Test`(){
        val cat = Category(name = "A", unit = "A")
        getAllButNotDescendants(cat, emptyList())
    }
}
