package io.github.kn65op.domag.utils

import io.github.kn65op.domag.utils.types.HasParent
import io.github.kn65op.domag.utils.types.HasUid

fun <WithParent> getAllButNotItAndDescendants(
    parent: WithParent,
    objects: List<WithParent>
): List<WithParent> where WithParent : HasParent, WithParent : HasUid {
    val mappedObjects = objects.map { it.uid to it }.toMap()
    return objects.filter { isItOrOneOfAncestor(parent.uid, it.uid, mappedObjects) }
}

private fun <WithParent> isItOrOneOfAncestor(
    parent: Int?,
    tested: Int?,
    mappedObjects: Map<Int?, WithParent>
): Boolean where WithParent : HasParent, WithParent : HasUid = when {
    parent == tested -> false
    mappedObjects[tested]?.parentId != null ->
        isItOrOneOfAncestor(
            parent = parent,
            tested = mappedObjects[tested]?.parentId,
            mappedObjects = mappedObjects
        )
    else -> true
}
