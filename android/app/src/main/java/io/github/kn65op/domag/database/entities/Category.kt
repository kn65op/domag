package io.github.kn65op.domag.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Fts4
@Entity
data class Category(
    @PrimaryKey @ColumnInfo(name = "rowid")
    val uid: Int? = null,

    val parentId: Int? = null,

    val name: String,

    val unit: String
)