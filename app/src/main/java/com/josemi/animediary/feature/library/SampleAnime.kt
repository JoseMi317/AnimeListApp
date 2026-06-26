package com.josemi.animediary.feature.library

import androidx.compose.ui.graphics.Color
import com.josemi.animediary.core.model.AnimePreview
import com.josemi.animediary.core.model.AnimeStatus

val sampleAnime = listOf(
    AnimePreview(
        id = 1,
        title = "One Piece",
        status = AnimeStatus.Watching,
        ratingValue = null,
        rating = "100000/10",
        note = "No hay puntuacion para esta obra maestra.",
        coverPath = null,
        posterColor = Color(0xFF3B82F6)
    ),
    AnimePreview(
        id = 2,
        title = "Jujutsu Kaisen",
        status = AnimeStatus.Finished,
        ratingValue = 10.0,
        rating = "10/10",
        note = "No hay palabras. Me transmitio de todo.",
        coverPath = null,
        posterColor = Color(0xFF8B5CF6)
    ),
    AnimePreview(
        id = 3,
        title = "Vinland Saga",
        status = AnimeStatus.Dropped,
        ratingValue = null,
        rating = "Sin terminar",
        note = "No me termina de enganchar por el momento.",
        coverPath = null,
        posterColor = Color(0xFF64748B)
    ),
    AnimePreview(
        id = 4,
        title = "Frieren",
        status = AnimeStatus.Finished,
        ratingValue = 9.6,
        rating = "9.6/10",
        note = "Los personajes, la historia y los mensajes son re lindos.",
        coverPath = null,
        posterColor = Color(0xFF14B8A6)
    ),
    AnimePreview(
        id = 5,
        title = "Fire Force",
        status = AnimeStatus.Pending,
        ratingValue = null,
        rating = "Empezandolo",
        note = "Entrada lista para completar despues.",
        coverPath = null,
        posterColor = Color(0xFFF97316)
    ),
    AnimePreview(
        id = 6,
        title = "Mob Psycho",
        status = AnimeStatus.Dropped,
        ratingValue = 5.0,
        rating = "5/10",
        note = "Sigue sin atraparme.",
        coverPath = null,
        posterColor = Color(0xFFEF4444)
    )
)
