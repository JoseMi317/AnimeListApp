package com.josemi.animediary.core.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [AnimeEntity::class, GenreEntity::class, AnimeGenreCrossRef::class],
    version = 2,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class AnimeDatabase : RoomDatabase() {
    abstract fun animeDao(): AnimeDao

    companion object {
        @Volatile
        private var instance: AnimeDatabase? = null

        fun getDatabase(context: Context): AnimeDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AnimeDatabase::class.java,
                    "anime_diary.db"
                )
                    .addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
        }

        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `genre` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `name` TEXT NOT NULL
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE UNIQUE INDEX IF NOT EXISTS `index_genre_name` ON `genre` (`name`)"
                )
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `anime_genre` (
                        `animeId` INTEGER NOT NULL,
                        `genreId` INTEGER NOT NULL,
                        PRIMARY KEY(`animeId`, `genreId`),
                        FOREIGN KEY(`animeId`) REFERENCES `anime`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE,
                        FOREIGN KEY(`genreId`) REFERENCES `genre`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE
                    )
                    """.trimIndent()
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_anime_genre_animeId` ON `anime_genre` (`animeId`)"
                )
                db.execSQL(
                    "CREATE INDEX IF NOT EXISTS `index_anime_genre_genreId` ON `anime_genre` (`genreId`)"
                )
            }
        }
    }
}
