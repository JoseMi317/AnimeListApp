package com.josemi.animediary.core.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

object MangaColors {
    val Paper = Color(0xFFF7F2E8)
    val Panel = Color(0xFFFFFCF4)
    val Ink = Color(0xFF111111)
    val SoftInk = Color(0xFF4A4A4A)
    val MutedInk = Color(0xFF77716A)
    val Highlight = Color(0xFFFFF0A8)
}

@Composable
fun MangaPanel(
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(12.dp),
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = MangaColors.Panel,
        border = BorderStroke(2.dp, MangaColors.Ink),
        shadowElevation = 0.dp
    ) {
        Box(
            modifier = Modifier
                .border(1.dp, MangaColors.Ink.copy(alpha = 0.25f), RoundedCornerShape(6.dp))
                .padding(contentPadding)
        ) {
            content()
        }
    }
}

@Composable
fun MangaChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        color = if (selected) MangaColors.Panel else MangaColors.Ink,
        style = MaterialTheme.typography.labelMedium,
        fontFamily = MangaSectionFont,
        fontWeight = FontWeight.ExtraBold,
        maxLines = 1,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) MangaColors.Ink else MangaColors.Panel)
            .border(2.dp, MangaColors.Ink, RoundedCornerShape(999.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 13.dp, vertical = 8.dp)
    )
}

@Composable
fun MangaButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    filled: Boolean = false,
    iconRes: Int? = null
) {
    val contentColor = if (filled) MangaColors.Panel else MangaColors.Ink

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(if (filled) MangaColors.Ink else MangaColors.Panel)
            .border(2.dp, MangaColors.Ink, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (iconRes != null) {
            Icon(
                painter = painterResource(iconRes),
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier
                    .padding(end = 7.dp)
                    .size(18.dp)
            )
        }
        Text(
            text = label,
            color = contentColor,
            style = MaterialTheme.typography.titleSmall,
            fontFamily = MangaSectionFont,
            fontWeight = FontWeight.ExtraBold,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
fun MangaSectionTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        color = MangaColors.Panel,
        style = MaterialTheme.typography.titleMedium,
        fontFamily = MangaSectionFont,
        fontWeight = FontWeight.Black,
        modifier = modifier
            .clip(RoundedCornerShape(2.dp))
            .background(MangaColors.Ink)
            .padding(horizontal = 12.dp, vertical = 5.dp)
    )
}

@Composable
fun MangaFieldTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(),
        color = MangaColors.Ink,
        style = MaterialTheme.typography.titleMedium,
        fontFamily = MangaSectionFont,
        fontWeight = FontWeight.Black,
        modifier = modifier.padding(horizontal = 2.dp, vertical = 2.dp)
    )
}
