package com.example.domag2.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Fts4
import androidx.room.PrimaryKey

@Fts4
@Entity
data class Depot(
    @PrimaryKey @ColumnInfo(name = "rowid")
    val uid: Int? = null,

    val name: String,

    val parentId: Int? = null
)