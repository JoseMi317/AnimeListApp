package com.josemi.animediary.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.josemi.animediary.core.ui.MangaColors
import com.josemi.animediary.core.ui.MangaSectionFont

@Composable
fun StatusPill(
    label: String,
    color: Color,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        color = if (selected) MangaColors.Ink else MangaColors.Ink,
        style = MaterialTheme.typography.labelMedium,
        fontFamily = MangaSectionFont,
        fontWeight = FontWeight.ExtraBold,
        maxLines = 1,
        modifier = modifier
            .background(
                color = if (selected) color else MangaColors.Panel,
                shape = RoundedCornerShape(999.dp)
            )
            .border(2.dp, MangaColors.Ink, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 8.dp)
    )
}
