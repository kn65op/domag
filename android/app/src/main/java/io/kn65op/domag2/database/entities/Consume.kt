package io.kn65op.domag2.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.kn65op.android.lib.type.FixedPointNumber
import java.time.ZonedDateTime

@Entity
data class Consume(
    @PrimaryKey
    val uid: Int? = null,
    val amount: FixedPointNumber,
    val date :ZonedDateTime
)
