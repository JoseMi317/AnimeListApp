package com.josemi.animediary.core.database

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "genre",
    indices = [Index(value = ["name"], unique = true)]
)
data class GenreEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String
)
