package io.github.kn65op.domag.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey
import io.github.kn65op.domag.utils.types.HasParent
import io.github.kn65op.domag.utils.types.HasUid

@Fts4
@Entity
data class Depot(
    @PrimaryKey @ColumnInfo(name = "rowid")
    override val uid: Int? = null,

    val name: String,

    override val parentId: Int? = null
) : HasParent, HasUid