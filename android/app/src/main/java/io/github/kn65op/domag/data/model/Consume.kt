package io.github.kn65op.domag.data.model

import io.github.kn65op.android.lib.type.FixedPointNumber
import java.time.ZonedDateTime

data class Consume(
    val uid: Int? = null,
    val amount: FixedPointNumber,
    val date: ZonedDateTime,
    val item: Item,
)
