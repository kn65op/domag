package io.github.kn65op.domag.data.model

import io.github.kn65op.android.lib.type.FixedPointNumber
import io.github.kn65op.domag.data.repository.DatabaseRepository
import io.github.kn65op.domag.utils.types.HasUid

data class RawCategory(
    override val uid: Int?,

    val name: String,
    val unit: String,
) : HasUid
