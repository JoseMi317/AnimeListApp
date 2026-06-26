package com.josemi.animediary.core.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.josemi.animediary.core.model.PersonalStatus
import com.josemi.animediary.core.model.ReleaseStatus

@Entity(tableName = "anime")
data class AnimeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val alternativeTitle: String? = null,
    val personalStatus: PersonalStatus = PersonalStatus.Starting,
    val releaseStatus: ReleaseStatus = ReleaseStatus.Unknown,
    val ratingValue: Double? = null,
    val review: String? = null,
    val episodesWatched: Int = 0,
    val totalEpisodes: Int? = null,
    val seasonsWatched: Int = 0,
    val rewatchCount: Int = 0,
    val startedAt: Long? = null,
    val finishedAt: Long? = null,
    val createdAt: Long,
    val updatedAt: Long,
    val isFavorite: Boolean = false,
    val wouldRewatch: Boolean = false,
    val coverPath: String? = null,
    val notes: String? = null
)

