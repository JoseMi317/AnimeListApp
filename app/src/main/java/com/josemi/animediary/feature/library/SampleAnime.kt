package com.josemi.animediary.feature.library

import androidx.compose.ui.graphics.Color
import com.josemi.animediary.core.model.AnimePreview
import com.josemi.animediary.core.model.AnimeStatus

val sampleAnime = listOf(
    AnimePreview(
        title = "One Piece",
        status = AnimeStatus.Watching,
        rating = "100000/10",
        note = "No hay puntuacion para esta obra maestra.",
        posterColor = Color(0xFF3B82F6)
    ),
    AnimePreview(
        title = "Jujutsu Kaisen",
        status = AnimeStatus.Finished,
        rating = "10/10",
        note = "No hay palabras. Me transmitio de todo.",
        posterColor = Color(0xFF8B5CF6)
    ),
    AnimePreview(
        title = "Vinland Saga",
        status = AnimeStatus.Dropped,
        rating = "Sin terminar",
        note = "No me termina de enganchar por el momento.",
        posterColor = Color(0xFF64748B)
    ),
    AnimePreview(
        title = "Frieren",
        status = AnimeStatus.Finished,
        rating = "9.6/10",
        note = "Los personajes, la historia y los mensajes son re lindos.",
        posterColor = Color(0xFF14B8A6)
    ),
    AnimePreview(
        title = "Fire Force",
        status = AnimeStatus.Pending,
        rating = "Empezandolo",
        note = "Entrada lista para completar despues.",
        posterColor = Color(0xFFF97316)
    ),
    AnimePreview(
        title = "Mob Psycho",
        status = AnimeStatus.Dropped,
        rating = "5/10",
        note = "Sigue sin atraparme.",
        posterColor = Color(0xFFEF4444)
    )
)

