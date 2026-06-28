package com.josemi.animediary.core.data

import com.josemi.animediary.core.database.AnimeEntity
import com.josemi.animediary.core.model.PersonalStatus
import com.josemi.animediary.core.model.RatingLabelMode
import com.josemi.animediary.core.model.ReleaseStatus
import org.json.JSONArray
import org.json.JSONObject

data class AnimeBackupEntry(
    val anime: AnimeEntity,
    val genreNames: List<String>
)

fun buildAnimeBackupJson(entries: List<AnimeBackupEntry>, exportedAt: Long): String {
    val root = JSONObject()
        .put("schemaVersion", 1)
        .put("exportedAt", exportedAt)
        .put(
            "animes",
            JSONArray().also { animeArray ->
                entries.forEach { entry ->
                    animeArray.put(entry.toJsonObject())
                }
            }
        )

    return root.toString(2)
}

fun parseAnimeBackupJson(json: String, importedAt: Long): List<AnimeBackupEntry> {
    val root = JSONObject(json)
    val animes = root.optJSONArray("animes") ?: JSONArray()

    return buildList {
        for (index in 0 until animes.length()) {
            val item = animes.optJSONObject(index) ?: continue
            add(item.toBackupEntry(importedAt))
        }
    }
}

private fun AnimeBackupEntry.toJsonObject(): JSONObject {
    val entity = anime

    return JSONObject()
        .put("title", entity.title)
        .putNullable("alternativeTitle", entity.alternativeTitle)
        .put("personalStatus", entity.personalStatus.name)
        .put("releaseStatus", entity.releaseStatus.name)
        .putNullable("ratingValue", entity.ratingValue)
        .putNullable("customRatingLabel", entity.customRatingLabel)
        .put("ratingLabelMode", entity.ratingLabelMode.name)
        .putNullable("review", entity.review)
        .put("episodesWatched", entity.episodesWatched)
        .putNullable("totalEpisodes", entity.totalEpisodes)
        .put("seasonsWatched", entity.seasonsWatched)
        .put("rewatchCount", entity.rewatchCount)
        .putNullable("startedAt", entity.startedAt)
        .putNullable("finishedAt", entity.finishedAt)
        .put("createdAt", entity.createdAt)
        .put("updatedAt", entity.updatedAt)
        .put("isFavorite", entity.isFavorite)
        .put("wouldRewatch", entity.wouldRewatch)
        .putNullable("coverPath", entity.coverPath)
        .putNullable("notes", entity.notes)
        .put(
            "genres",
            JSONArray().also { genreArray ->
                genreNames.forEach { genreArray.put(it) }
            }
        )
}

private fun JSONObject.toBackupEntry(importedAt: Long): AnimeBackupEntry {
    val entity = AnimeEntity(
        title = optString("title").ifBlank { "Sin titulo" },
        alternativeTitle = optNullableString("alternativeTitle"),
        personalStatus = optEnum("personalStatus", PersonalStatus.Starting),
        releaseStatus = optEnum("releaseStatus", ReleaseStatus.Unknown),
        ratingValue = optNullableDouble("ratingValue"),
        customRatingLabel = optNullableString("customRatingLabel"),
        ratingLabelMode = optEnum("ratingLabelMode", RatingLabelMode.Auto),
        review = optNullableString("review"),
        episodesWatched = optInt("episodesWatched", 0),
        totalEpisodes = optNullableInt("totalEpisodes"),
        seasonsWatched = optInt("seasonsWatched", 0),
        rewatchCount = optInt("rewatchCount", 0),
        startedAt = optNullableLong("startedAt"),
        finishedAt = optNullableLong("finishedAt"),
        createdAt = optLong("createdAt", importedAt),
        updatedAt = optLong("updatedAt", importedAt),
        isFavorite = optBoolean("isFavorite", false),
        wouldRewatch = optBoolean("wouldRewatch", false),
        coverPath = optNullableString("coverPath"),
        notes = optNullableString("notes")
    )

    val genreArray = optJSONArray("genres") ?: JSONArray()
    val genreNames = buildList {
        for (index in 0 until genreArray.length()) {
            val name = genreArray.optString(index).trim()
            if (name.isNotEmpty()) add(name)
        }
    }

    return AnimeBackupEntry(entity, genreNames)
}

private fun JSONObject.putNullable(name: String, value: Any?): JSONObject {
    put(name, value ?: JSONObject.NULL)
    return this
}

private fun JSONObject.optNullableString(name: String): String? {
    return if (isNull(name)) null else optString(name).takeIf { it.isNotBlank() }
}

private fun JSONObject.optNullableDouble(name: String): Double? {
    return if (isNull(name)) null else optDouble(name)
}

private fun JSONObject.optNullableInt(name: String): Int? {
    return if (isNull(name)) null else optInt(name)
}

private fun JSONObject.optNullableLong(name: String): Long? {
    return if (isNull(name)) null else optLong(name)
}

private inline fun <reified T : Enum<T>> JSONObject.optEnum(name: String, fallback: T): T {
    return runCatching { enumValueOf<T>(optString(name)) }.getOrDefault(fallback)
}
