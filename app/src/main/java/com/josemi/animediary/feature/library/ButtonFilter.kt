package com.josemi.animediary.feature.library

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.josemi.animediary.core.ui.MangaChip

@Composable
fun ButtonFilter(
    label: String,
    selected: Boolean,
    color: Color,
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
