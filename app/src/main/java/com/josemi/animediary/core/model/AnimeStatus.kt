package com.josemi.animediary.core.model

import androidx.compose.ui.graphics.Color

enum class AnimeStatus(val label: String, val color: Color) {
    Finished("Terminado", Color(0xFF6DD58C)),
    Watching("Viendo", Color(0xFF72A7FF)),
    Pending("Pendiente", Color(0xFFFFD166)),
    Paused("Pausado", Color(0xFFFF9F7A)),
    Dropped("Dropeado", Color(0xFFFF6B6B))
}

