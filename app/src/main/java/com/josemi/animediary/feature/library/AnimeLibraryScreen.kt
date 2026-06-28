package com.josemi.animediary.feature.library

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josemi.animediary.R
import com.josemi.animediary.core.model.AnimePreview
import com.josemi.animediary.core.model.AnimeStatus
import com.josemi.animediary.core.ui.MangaColors
import com.josemi.animediary.core.ui.MangaInputFont
import com.josemi.animediary.core.ui.MangaPanel
import com.josemi.animediary.core.ui.MangaScreenBackground
import com.josemi.animediary.ui.theme.AnimeDiaryTheme

private enum class RatingSort {
    HighestFirst,
    LowestFirst
}

@Composable
fun AnimeLibraryScreen(
    animeList: List<AnimePreview>,
    onAnimeClick: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<AnimeStatus?>(null) }
    var ratingSort by remember { mutableStateOf(RatingSort.HighestFirst) }

    val visibleAnime = animeList
        .filter { anime ->
            val matchesSearch = anime.title.contains(searchQuery, ignoreCase = true) ||
                anime.note.contains(searchQuery, ignoreCase = true) ||
                anime.status.label.contains(searchQuery, ignoreCase = true)

            val matchesStatus = selectedStatus == null || anime.status == selectedStatus

            matchesSearch && matchesStatus
        }
        .let { filteredAnime ->
            when (ratingSort) {
                RatingSort.HighestFirst -> filteredAnime.sortedByDescending { it.ratingValue ?: -1.0 }
                RatingSort.LowestFirst -> filteredAnime.sortedBy { it.ratingValue ?: Double.MAX_VALUE }
            }
        }

    MangaScreenBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
                MangaSearchField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it }
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        StatusPill(
                            label = "Todos",
                            color = Color(0xFFE5E7EB),
                            selected = selectedStatus == null,
                            onClick = { selectedStatus = null }
                        )
                    }
                    item {
                        StatusPill(
                            label = "Viendo",
                            color = AnimeStatus.Watching.color,
                            selected = selectedStatus == AnimeStatus.Watching,
                            onClick = { selectedStatus = AnimeStatus.Watching }
                        )
                    }
                    item {
                        StatusPill(
                            label = "Terminado",
                            color = AnimeStatus.Finished.color,
                            selected = selectedStatus == AnimeStatus.Finished,
                            onClick = { selectedStatus = AnimeStatus.Finished }
                        )
                    }
                    item {
                        StatusPill(
                            label = "Pendiente",
                            color = AnimeStatus.Pending.color,
                            selected = selectedStatus == AnimeStatus.Pending,
                            onClick = { selectedStatus = AnimeStatus.Pending }
                        )
                    }
                    item {
                        StatusPill(
                            label = "Dropeado",
                            color = AnimeStatus.Dropped.color,
                            selected = selectedStatus == AnimeStatus.Dropped,
                            onClick = { selectedStatus = AnimeStatus.Dropped }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                MangaPanel(contentPadding = PaddingValues(10.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ButtonFilter(
                            label = "Mayor nota",
                            selected = ratingSort == RatingSort.HighestFirst,
                            color = Color(0xFFE5E7EB),
                            onClick = { ratingSort = RatingSort.HighestFirst },
                            modifier = Modifier.weight(1f)
                        )
                        ButtonFilter(
                            label = "Menor nota",
                            selected = ratingSort == RatingSort.LowestFirst,
                            color = Color(0xFFE5E7EB),
                            onClick = { ratingSort = RatingSort.LowestFirst },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                if (visibleAnime.isEmpty()) {
                    EmptyLibraryMessage(
                        hasAnyAnime = animeList.isNotEmpty(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .fillMaxHeight()
                    )
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        contentPadding = PaddingValues(bottom = 120.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        items(visibleAnime) { anime ->
                            AnimeCard(
                                anime = anime,
                                onClick = { onAnimeClick(anime.id) }
                            )
                        }
                    }
                }
            }
    }
}

@Composable
private fun MangaSearchField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    MangaPanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_manga_search),
                contentDescription = null,
                tint = MangaColors.Ink,
                modifier = Modifier.size(24.dp)
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    color = MangaColors.Ink,
                    fontFamily = MangaInputFont
                ),
                modifier = Modifier.weight(1f),
                decorationBox = { innerTextField ->
                    Box(modifier = Modifier.fillMaxWidth()) {
                        if (value.isEmpty()) {
                            Text(
                                text = "Buscar anime...",
                                color = MangaColors.MutedInk,
                                style = MaterialTheme.typography.bodyLarge.copy(fontFamily = MangaInputFont)
                            )
                        }
                        innerTextField()
                    }
                }
            )
        }
    }
}

@Composable
private fun EmptyLibraryMessage(
    hasAnyAnime: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.padding(top = 48.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = if (hasAnyAnime) "No encontre coincidencias" else "Aun no tienes animes registrados",
            color = MangaColors.Ink,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Black
        )
        Text(
            text = if (hasAnyAnime) {
                "Prueba cambiar la busqueda o el filtro."
            } else {
                "Toca Nuevo para agregar el anime que estas empezando."
            },
            color = MangaColors.SoftInk,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AnimeLibraryPreview() {
    AnimeDiaryTheme {
        AnimeLibraryScreen(
            animeList = sampleAnime,
            onAnimeClick = {}
        )
    }
}
