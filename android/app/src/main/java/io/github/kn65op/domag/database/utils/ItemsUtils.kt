package io.github.kn65op.domag.database.utils

import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.database.entities.Item

fun List<Item>.amount() = when (val wholeAmount =
    map { it.amount }.reduceOrNull { acc, itemAmount -> acc + itemAmount }) {
    null -> FixedPointNumber(0)
    else -> wholeAmount
}
