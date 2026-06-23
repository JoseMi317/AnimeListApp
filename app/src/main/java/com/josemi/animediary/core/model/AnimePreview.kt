package com.josemi.animediary.core.model

import androidx.compose.ui.graphics.Color

data class AnimePreview(
    val title: String,
    val status: AnimeStatus,
    val rating: String,
    val note: String,
    val posterColor: Color
)

