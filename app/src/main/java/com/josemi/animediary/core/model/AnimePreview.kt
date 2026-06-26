package com.josemi.animediary.core.model

import androidx.compose.ui.graphics.Color

data class AnimePreview(
    val id: Int,
    val title: String,
    val status: AnimeStatus,
    val ratingValue: Double?,
    val rating: String,
    val note: String,
    val coverPath: String?,
    val posterColor: Color
)
