package com.josemi.animediary.feature.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josemi.animediary.R
import com.josemi.animediary.core.database.AnimeEntity
import com.josemi.animediary.core.model.PersonalStatus
import com.josemi.animediary.core.model.ReleaseStatus
import com.josemi.animediary.core.ui.MangaChip
import com.josemi.animediary.core.ui.MangaColors
import com.josemi.animediary.core.ui.MangaInputFont
import com.josemi.animediary.core.ui.MangaPanel
import com.josemi.animediary.core.ui.MangaScreenBackground
import com.josemi.animediary.core.ui.MangaSectionFont
import com.josemi.animediary.core.ui.MangaTitleFont
import com.josemi.animediary.ui.theme.AnimeDiaryTheme
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsScreen(
    animeList: List<AnimeEntity>,
    usedGenreNames: List<String>,
    modifier: Modifier = Modifier
) {
    val stats = remember(animeList, usedGenreNames) {
        AnimeStats.from(animeList, usedGenreNames)
    }

    MangaScreenBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .padding(bottom = 120.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Stats",
                color = MangaColors.Ink,
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = MangaTitleFont,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            if (animeList.isEmpty()) {
                EmptyStatsPanel()
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    label = "Total",
                    value = stats.total.toString(),
                    helper = "Animes",
                    iconRes = R.drawable.ic_manga_book,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Promedio",
                    value = stats.averageRating ?: "--",
                    helper = "Nota",
                    iconRes = R.drawable.ic_manga_percent,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    label = "Favoritos",
                    value = stats.favoriteCount.toString(),
                    helper = "Marcados",
                    iconRes = R.drawable.ic_manga_heart,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    label = "Rewatch",
                    value = stats.wouldRewatchCount.toString(),
                    helper = "Volverias",
                    iconRes = R.drawable.ic_manga_eye,
                    modifier = Modifier.weight(1f)
                )
            }

            SectionTitle("Estados", R.drawable.ic_manga_stats)

            MangaPanel {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    PersonalStatus.entries.forEach { status ->
                        val count = stats.statusCounts[status] ?: 0
                        MangaChip(
                            label = "${status.label}: $count",
                            selected = count > 0,
                            onClick = {}
                        )
                    }
                }
            }

            SectionTitle("Top puntuados", R.drawable.ic_manga_trophy)

            RankedList(
                items = stats.topRated.mapIndexed { index, anime ->
                    RankedItem(
                        position = index + 1,
                        title = anime.title,
                        detail = anime.ratingValue?.let { "${it.formatOneDecimal()} / 10" } ?: "Sin nota"
                    )
                },
                emptyText = "Aun no hay animes con puntuacion."
            )

            SectionTitle("Mas vistos", R.drawable.ic_manga_fire)

            RankedList(
                items = stats.mostWatched.mapIndexed { index, anime ->
                    RankedItem(
                        position = index + 1,
                        title = anime.title,
                        detail = "${anime.episodesWatched} capitulos"
                    )
                },
                emptyText = "Cuando agregues episodios vistos, apareceran aqui."
            )

            SectionTitle("Generos", R.drawable.ic_manga_book)

            RankedList(
                items = stats.topGenres.mapIndexed { index, genre ->
                    RankedItem(
                        position = index + 1,
                        title = genre.name,
                        detail = "${genre.count} animes"
                    )
                },
                emptyText = "Aun no hay generos usados."
            )
        }
    }
}

@Composable
private fun EmptyStatsPanel() {
    MangaPanel(contentPadding = PaddingValues(14.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = "Todavia no hay datos",
                color = MangaColors.Ink,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = MangaSectionFont,
                fontWeight = FontWeight.Black
            )
            Text(
                text = "Cuando registres animes, esta pantalla va a mostrar rankings, promedios, estados y generos.",
                color = MangaColors.SoftInk,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = MangaInputFont
            )
        }
    }
}

@Composable
private fun StatCard(
    label: String,
    value: String,
    helper: String,
    iconRes: Int,
    modifier: Modifier = Modifier
) {
    MangaPanel(
        modifier = modifier,
        contentPadding = PaddingValues(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = MangaColors.Ink,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = label.uppercase(),
                    color = MangaColors.SoftInk,
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = MangaSectionFont,
                    fontWeight = FontWeight.Black
                )
            }
            Text(
                text = value,
                color = MangaColors.Ink,
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = MangaTitleFont,
                fontWeight = FontWeight.Black
            )
            Text(
                text = helper,
                color = MangaColors.SoftInk,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = MangaInputFont,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun SectionTitle(text: String, iconRes: Int) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 2.dp)
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            tint = MangaColors.Ink,
            modifier = Modifier.size(24.dp)
        )
        Text(
            text = text.uppercase(),
            color = MangaColors.Ink,
            style = MaterialTheme.typography.titleLarge,
            fontFamily = MangaSectionFont,
            fontWeight = FontWeight.Black
        )
    }
}

@Composable
private fun RankedList(
    items: List<RankedItem>,
    emptyText: String
) {
    MangaPanel {
        if (items.isEmpty()) {
            Text(
                text = emptyText,
                color = MangaColors.SoftInk,
                style = MaterialTheme.typography.bodyMedium,
                fontFamily = MangaInputFont
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "#${item.position}",
                            color = MangaColors.Ink,
                            style = MaterialTheme.typography.titleMedium,
                            fontFamily = MangaSectionFont,
                            fontWeight = FontWeight.Black
                        )
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = item.title,
                                color = MangaColors.Ink,
                                style = MaterialTheme.typography.titleMedium,
                                fontFamily = MangaTitleFont,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = item.detail,
                                color = MangaColors.SoftInk,
                                style = MaterialTheme.typography.bodySmall,
                                fontFamily = MangaInputFont,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}

private data class RankedItem(
    val position: Int,
    val title: String,
    val detail: String
)

private data class GenreCount(
    val name: String,
    val count: Int
)

private data class AnimeStats(
    val total: Int,
    val averageRating: String?,
    val favoriteCount: Int,
    val wouldRewatchCount: Int,
    val statusCounts: Map<PersonalStatus, Int>,
    val topRated: List<AnimeEntity>,
    val mostWatched: List<AnimeEntity>,
    val topGenres: List<GenreCount>
) {
    companion object {
        fun from(animeList: List<AnimeEntity>, usedGenreNames: List<String>): AnimeStats {
            val ratedAnime = animeList.filter { it.ratingValue != null }
            val average = ratedAnime
                .mapNotNull { it.ratingValue }
                .takeIf { it.isNotEmpty() }
                ?.average()
                ?.formatOneDecimal()

            return AnimeStats(
                total = animeList.size,
                averageRating = average,
                favoriteCount = animeList.count { it.isFavorite },
                wouldRewatchCount = animeList.count { it.wouldRewatch },
                statusCounts = PersonalStatus.entries.associateWith { status ->
                    animeList.count { it.personalStatus == status }
                },
                topRated = ratedAnime
                    .sortedByDescending { it.ratingValue }
                    .take(5),
                mostWatched = animeList
                    .filter { it.episodesWatched > 0 }
                    .sortedByDescending { it.episodesWatched }
                    .take(5),
                topGenres = usedGenreNames
                    .groupingBy { it }
                    .eachCount()
                    .entries
                    .sortedWith(compareByDescending<Map.Entry<String, Int>> { it.value }.thenBy { it.key })
                    .take(5)
                    .map { GenreCount(name = it.key, count = it.value) }
            )
        }
    }
}

private fun Double.formatOneDecimal(): String {
    return String.format(Locale.US, "%.1f", this)
}

@Preview(showBackground = true)
@Composable
fun StatsScreenPreview() {
    AnimeDiaryTheme {
        StatsScreen(
            animeList = listOf(
                AnimeEntity(
                    id = 1,
                    title = "Naruto",
                    personalStatus = PersonalStatus.Finished,
                    releaseStatus = ReleaseStatus.Finished,
                    ratingValue = 10.0,
                    episodesWatched = 220,
                    isFavorite = true,
                    wouldRewatch = true,
                    createdAt = 0L,
                    updatedAt = 0L
                ),
                AnimeEntity(
                    id = 2,
                    title = "Cowboy Bebop",
                    personalStatus = PersonalStatus.Watching,
                    ratingValue = 9.0,
                    episodesWatched = 8,
                    createdAt = 0L,
                    updatedAt = 0L
                )
            ),
            usedGenreNames = listOf("Accion", "Accion", "Drama")
        )
    }
}
