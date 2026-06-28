package com.josemi.animediary.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.josemi.animediary.R
import com.josemi.animediary.core.ui.CoverImage
import com.josemi.animediary.core.model.AnimePreview
import com.josemi.animediary.core.ui.MangaColors
import com.josemi.animediary.core.ui.MangaPanel
import com.josemi.animediary.core.ui.MangaSectionFont
import com.josemi.animediary.core.ui.MangaTitleFont

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
        contentPadding = PaddingValues(7.dp)
    ) {
        Column {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.70f)
                    .clip(RoundedCornerShape(8.dp))
                    .border(2.dp, MangaColors.Ink, RoundedCornerShape(8.dp))
            ) {
                CoverImage(
                    coverPath = anime.coverPath,
                    fallbackColor = anime.posterColor
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = anime.title,
                color = MangaColors.Ink,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = MangaTitleFont,
                fontWeight = FontWeight.Black,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                RatingBadge(
                    rating = anime.rating,
                    modifier = Modifier.weight(1f)
                )
                StatusBadge(
                    label = anime.status.label,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun RatingBadge(
    rating: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MangaColors.Panel)
            .border(2.dp, MangaColors.Ink, RoundedCornerShape(999.dp))
            .padding(horizontal = 8.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_manga_star),
            contentDescription = null,
            tint = MangaColors.Ink,
            modifier = Modifier.size(14.dp)
        )
        Text(
            text = rating,
            color = MangaColors.Ink,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = MangaSectionFont,
            fontWeight = FontWeight.Black,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(start = 4.dp)
        )
    }
}

@Composable
private fun StatusBadge(
    label: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = label.uppercase(),
        color = MangaColors.Panel,
        style = MaterialTheme.typography.labelSmall,
        fontFamily = MangaSectionFont,
        fontWeight = FontWeight.Black,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MangaColors.Ink)
            .padding(horizontal = 8.dp, vertical = 6.dp)
    )
}
