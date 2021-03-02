package io.github.kn65op.domag.data.model

import io.github.kn65op.domag.utils.types.HasUid

data class RawCategory(
    override val uid: Int?,

    val name: String,
    val unit: String,
    val parentId: Int?,
) : HasUid
