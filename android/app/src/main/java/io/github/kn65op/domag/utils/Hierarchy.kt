package io.github.kn65op.domag.utils

interface HasParent {
    val parentId: Int?
}

interface HasUid {
    val uid: Int?
}

fun <WithParent> getAllButNotDescendants(
    parent: WithParent,
    objects: List<WithParent>
): List<WithParent> where WithParent : HasParent, WithParent : HasUid {
    return objects
    //parent.uid
    //val mappedObjects = objects.map { it.uid to it }.toMap()
    //return objects.filter { isItOrOneOfAncestor(parent.uid, it.uid, mappedObjects) }
}

private fun <WithParent> isItOrOneOfAncestor(
    parent: Int?,
    tested: Int?,
    mappedObjects: Map<Int?, WithParent>
): Boolean where WithParent : HasParent, WithParent : HasUid = when {
    //tested == null -> false
    //parent == tested -> true
    //mappedObjects[tested]?.parentId == null -> false
    //else -> isItOrOneOfAncestor(
        //parent = parent,
        //tested = mappedObjects[tested]?.parentId,
        //mappedObjects = mappedObjects
    //)
    else -> true
}
