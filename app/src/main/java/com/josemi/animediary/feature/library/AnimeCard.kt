package com.josemi.animediary.feature.library

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.josemi.animediary.core.ui.CoverImage
import com.josemi.animediary.core.model.AnimePreview
import com.josemi.animediary.core.ui.MangaColors
import com.josemi.animediary.core.ui.MangaPanel

@Composable
fun AnimeCard(
    anime: AnimePreview,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MangaPanel(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f)
                    .clip(RoundedCornerShape(8.dp))
            ) {
                CoverImage(
                    coverPath = anime.coverPath,
                    fallbackColor = MangaColors.Highlight
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = anime.title,
                color = MangaColors.Ink,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(6.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = anime.rating,
                    color = MangaColors.Ink,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = anime.status.label,
                    color = MangaColors.MutedInk,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}
