package com.josemi.animediary.feature.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.josemi.animediary.core.ui.MangaChip

@Composable
fun StatusPill(
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MangaChip(
        label = label,
        selected = selected,
        onClick = onClick,
        modifier = modifier
    )
}
