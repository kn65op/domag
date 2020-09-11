package io.github.kn65op.domag.utils

import com.natpryce.hamkrest.assertion.assertThat
import com.natpryce.hamkrest.equalTo
import org.junit.Test

data class TestUidAndParent(
    override val parentId: Int?,
    override val uid: Int?,
) :
    HasParent,
    HasUid

class HierarchyTest {
    private val nullObject = TestUidAndParent(null, null)
    private val notInList = TestUidAndParent(100, 200)
    private val notInListWithoutParent = TestUidAndParent(null, 200)
    private val root1 = TestUidAndParent(null, 1)
    private val root2 = TestUidAndParent(null, 2)
    private val root1Object1 = TestUidAndParent(1, 3)
    private val root1Object2 = TestUidAndParent(1, 4)
    private val root1Object3 = TestUidAndParent(1, 5)
    private val root1Object3Object1 = TestUidAndParent(5, 6)
    private val root1Object3Object1Object1 = TestUidAndParent(6, 7)
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
        root2Object2,
        nullObject,
    )

    @Test
    fun `Given empty list should return empty list`() {
        assertThat(getAllButNotItAndDescendants(nullObject, emptyList()), equalTo(emptyList()))
    }

    @Test
    fun `Given object not in list should return same list`() {
        assertThat(getAllButNotItAndDescendants(notInList, objects), equalTo(objects))
    }

    @Test
    fun `Given object wihout parent not in list should return same list`() {
        assertThat(getAllButNotItAndDescendants(notInListWithoutParent, objects), equalTo(objects))
    }

    @Test
    fun `Given last object in hierarchy should return list without this object`() {
        assertThat(
            getAllButNotItAndDescendants(root1Object2, objects),
            equalTo(objects - root1Object2)
        )
    }

    @Test
    fun `Given objects that has objects below should remove it also`() {
        assertThat(
            getAllButNotItAndDescendants(root2Object1, objects),
            equalTo(objects - root2Object1 - root2Object1Object1)
        )
    }

    @Test
    fun `Given root object shuold remove only others root objects`() {
        assertThat(
            getAllButNotItAndDescendants(root1, objects),
            equalTo(listOf(root2, root2Object1, root2Object1Object1, root2Object2, nullObject))
        )
    }

}
