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
    private val notInList = TestUidAndParent(100, 200)
    private val root1 = TestUidAndParent(null, 1)
    private val root2 = TestUidAndParent(null, 2)
    private val root1Object1 = TestUidAndParent(1, 3)
    private val root1Object2 = TestUidAndParent(1, 4)
    private val root1Object3 = TestUidAndParent(1, 5)
    private val root1Object3Object1 = TestUidAndParent(5, 6)
    private val root1Object3Object1Object1 = TestUidAndParent(7, 7)
    private val root2Object1 = TestUidAndParent(2, 8)
    private val root2Object1Object1 = TestUidAndParent(8, 9)
    private val root2Object2 = TestUidAndParent(2, 10)
    private val objects = listOf(
        root1,
        root1Object1,
        root1Object2,
        root1Object3,
        root1Object3Object1,
        root1Object3Object1Object1,
        root2,
        root2Object1,
        root2Object1Object1,
        root2Object2
    )

    @Test
    fun `Given empty list should return empty list`() {
        assertThat(getAllButNotDescendants(nullParent, emptyList()), equalTo(emptyList()))
    }

    @Test
    fun `Given object not in list should return same list`() {
        assertThat(getAllButNotDescendants(notInList, objects), equalTo(objects))
    }

}
