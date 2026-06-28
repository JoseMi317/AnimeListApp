package com.josemi.animediary.core.data

import com.josemi.animediary.core.database.AnimeDao
import com.josemi.animediary.core.database.AnimeEntity
import com.josemi.animediary.core.database.AnimeGenreCrossRef
import com.josemi.animediary.core.database.GenreEntity
import com.josemi.animediary.core.model.defaultAnimeGenres
import kotlinx.coroutines.flow.Flow

class AnimeRepository(
    private val animeDao: AnimeDao
) {
    fun observeAllAnime(): Flow<List<AnimeEntity>> = animeDao.observeAll()

    fun observeAnimeById(id: Int): Flow<AnimeEntity?> = animeDao.observeById(id)

    fun observeAllGenres(): Flow<List<GenreEntity>> = animeDao.observeAllGenres()

    fun observeGenresForAnime(animeId: Int): Flow<List<GenreEntity>> =
        animeDao.observeGenresForAnime(animeId)

    fun observeGenreIdsForAnime(animeId: Int): Flow<List<Int>> =
        animeDao.observeGenreIdsForAnime(animeId)

    fun observeUsedGenreNames(): Flow<List<String>> = animeDao.observeUsedGenreNames()

    suspend fun ensureDefaultGenres() {
        animeDao.insertGenres(defaultAnimeGenres.map { name -> GenreEntity(name = name) })
    }

    suspend fun addAnime(anime: AnimeEntity): Long {
        return animeDao.insert(anime)
    }

    suspend fun updateAnime(anime: AnimeEntity) {
        animeDao.update(anime)
    }

    suspend fun deleteAnime(anime: AnimeEntity) {
        animeDao.delete(anime)
    }

    suspend fun setGenresForAnime(animeId: Int, genreIds: Set<Int>) {
        animeDao.deleteGenresForAnime(animeId)
        if (genreIds.isEmpty()) return

        animeDao.insertAnimeGenreCrossRefs(
            genreIds.map { genreId ->
                AnimeGenreCrossRef(animeId = animeId, genreId = genreId)
            }
        )
    }

    suspend fun exportBackupJson(now: Long = System.currentTimeMillis()): String {
        val entries = animeDao.getAll().map { anime ->
            AnimeBackupEntry(
                anime = anime,
                genreNames = animeDao.getGenreNamesForAnime(anime.id)
            )
        }

        return buildAnimeBackupJson(entries, exportedAt = now)
    }

    suspend fun importBackupJson(json: String, now: Long = System.currentTimeMillis()): Int {
        val entries = parseAnimeBackupJson(json, importedAt = now)
        val genreNames = entries
            .flatMap { it.genreNames }
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()

        animeDao.insertGenres(genreNames.map { name -> GenreEntity(name = name) })
        val genresByName = animeDao.getAllGenres().associateBy { it.name }

        animeDao.deleteAllAnime()

        entries.forEach { entry ->
            val animeId = animeDao.insert(entry.anime.copy(id = 0)).toInt()
            val genreIds = entry.genreNames
                .mapNotNull { genreName -> genresByName[genreName]?.id }
                .toSet()

            setGenresForAnime(animeId, genreIds)
        }

        return entries.size
    }
}
