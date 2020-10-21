package io.github.kn65op.domag.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.kn65op.android.lib.type.FixedPointNumber

@Entity
data class CategoryLimit(
    @PrimaryKey
    val uid: Int? = null,

    val categoryId: Int,

    val minimumDesiredAmount: FixedPointNumber,
)
