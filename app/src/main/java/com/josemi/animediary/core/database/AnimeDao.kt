package com.josemi.animediary.core.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimeDao {
    @Query("SELECT * FROM anime ORDER BY updatedAt DESC")
    fun observeAll(): Flow<List<AnimeEntity>>

    @Query("SELECT * FROM anime ORDER BY updatedAt DESC")
    suspend fun getAll(): List<AnimeEntity>

    @Query("SELECT * FROM anime WHERE id = :id")
    fun observeById(id: Int): Flow<AnimeEntity?>

    @Query("SELECT * FROM genre ORDER BY name ASC")
    fun observeAllGenres(): Flow<List<GenreEntity>>

    @Query("SELECT * FROM genre ORDER BY name ASC")
    suspend fun getAllGenres(): List<GenreEntity>

    @Query(
        """
        SELECT genre.* FROM genre
        INNER JOIN anime_genre ON genre.id = anime_genre.genreId
        WHERE anime_genre.animeId = :animeId
        ORDER BY genre.name ASC
        """
    )
    fun observeGenresForAnime(animeId: Int): Flow<List<GenreEntity>>

    @Query("SELECT genreId FROM anime_genre WHERE animeId = :animeId")
    fun observeGenreIdsForAnime(animeId: Int): Flow<List<Int>>

    @Query(
        """
        SELECT genre.name FROM genre
        INNER JOIN anime_genre ON genre.id = anime_genre.genreId
        WHERE anime_genre.animeId = :animeId
        ORDER BY genre.name ASC
        """
    )
    suspend fun getGenreNamesForAnime(animeId: Int): List<String>

    @Query(
        """
        SELECT genre.name FROM genre
        INNER JOIN anime_genre ON genre.id = anime_genre.genreId
        ORDER BY genre.name ASC
        """
    )
    fun observeUsedGenreNames(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(anime: AnimeEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertGenres(genres: List<GenreEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAnimeGenreCrossRefs(crossRefs: List<AnimeGenreCrossRef>)

    @Update
    suspend fun update(anime: AnimeEntity)

    @Delete
    suspend fun delete(anime: AnimeEntity)

    @Query("DELETE FROM anime_genre WHERE animeId = :animeId")
    suspend fun deleteGenresForAnime(animeId: Int)

    @Query("DELETE FROM anime")
    suspend fun deleteAllAnime()

    @Transaction
    suspend fun replaceAllAnime(anime: List<AnimeEntity>) {
        deleteAllAnime()
        anime.forEach { insert(it) }
    }
}
