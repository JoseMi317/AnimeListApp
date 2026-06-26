package com.josemi.animediary.core.data

import androidx.compose.ui.graphics.Color
import com.josemi.animediary.core.database.AnimeEntity
import com.josemi.animediary.core.model.AnimePreview
import com.josemi.animediary.core.model.AnimeStatus
import com.josemi.animediary.core.model.PersonalStatus
import com.josemi.animediary.core.model.ReleaseStatus

fun AnimeEntity.toPreview(): AnimePreview {
    return AnimePreview(
        id = id,
        title = title,
        status = personalStatus.toPreviewStatus(),
        ratingValue = ratingValue,
        rating = ratingValue?.let { "$it/10" } ?: "Sin puntuacion",
        note = review ?: notes ?: "Sin resena todavia.",
        coverPath = coverPath,
        posterColor = Color(0xFF3B82F6)
    )
}

fun createAnimeEntity(
    title: String,
    personalStatus: PersonalStatus,
    releaseStatus: ReleaseStatus,
    now: Long
): AnimeEntity {
    return AnimeEntity(
        title = title.trim(),
        personalStatus = personalStatus,
        releaseStatus = releaseStatus,
        createdAt = now,
        updatedAt = now
    )
}

private fun PersonalStatus.toPreviewStatus(): AnimeStatus {
    return when (this) {
        PersonalStatus.Pending -> AnimeStatus.Pending
        PersonalStatus.Starting -> AnimeStatus.Watching
        PersonalStatus.Watching -> AnimeStatus.Watching
        PersonalStatus.Paused -> AnimeStatus.Paused
        PersonalStatus.Unfinished -> AnimeStatus.Paused
        PersonalStatus.DroppedForNow -> AnimeStatus.Dropped
        PersonalStatus.Abandoned -> AnimeStatus.Dropped
        PersonalStatus.Finished -> AnimeStatus.Finished
    }
}
