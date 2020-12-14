package io.github.kn65op.domag.data.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.kn65op.android.lib.type.FixedPointNumber
import java.time.ZonedDateTime

@Entity
data class Item(
    @PrimaryKey
    val uid: Int? = null,
    val amount: FixedPointNumber,
    val depotId: Int,
    val categoryId: Int,
    val description: String? = null,
    val bestBefore: ZonedDateTime? = null
)

fun Item.withDescription(newDescription: String) =
    Item(
        uid = uid,
        depotId = depotId,
        categoryId = categoryId,
        description = newDescription,
        amount = amount
    )

