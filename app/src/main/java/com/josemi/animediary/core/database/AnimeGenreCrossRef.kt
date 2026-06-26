package com.josemi.animediary.core.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "anime_genre",
    primaryKeys = ["animeId", "genreId"],
    foreignKeys = [
        ForeignKey(
            entity = AnimeEntity::class,
            parentColumns = ["id"],
            childColumns = ["animeId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = GenreEntity::class,
            parentColumns = ["id"],
            childColumns = ["genreId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index("animeId"),
        Index("genreId")
    ]
)
data class AnimeGenreCrossRef(
    val animeId: Int,
    val genreId: Int
)

