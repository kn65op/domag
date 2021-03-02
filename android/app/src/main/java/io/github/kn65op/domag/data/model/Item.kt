package io.github.kn65op.domag.data.model

import io.github.kn65op.android.lib.type.FixedPointNumber
import java.time.ZonedDateTime

data class Item(
    val uid: Int? = null,

    val amount: FixedPointNumber,
    val depot: Depot,
    val category: Category,
    val description: String? = null,
    val bestBefore: ZonedDateTime? = null,
    val consumes: List<Consume>,
)

