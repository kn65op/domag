package io.github.kn65op.domag.data.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import io.github.kn65op.android.lib.type.FixedPointNumber

@Entity
data class CategoryLimit(
    @PrimaryKey
    val uid: Long? = null,

    val categoryId: Int,

    val minimumDesiredAmount: FixedPointNumber,
)

fun CategoryLimit.withLimit(minimumAmount: FixedPointNumber) =
    CategoryLimit(uid = uid, categoryId = categoryId, minimumDesiredAmount = minimumAmount)
