package com.josemi.animediary.feature.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josemi.animediary.R
import com.josemi.animediary.core.database.AnimeEntity
import com.josemi.animediary.core.database.GenreEntity
import com.josemi.animediary.core.model.PersonalStatus
import com.josemi.animediary.core.model.ReleaseStatus
import com.josemi.animediary.core.ui.CoverImage
import com.josemi.animediary.core.ui.MangaButton
import com.josemi.animediary.core.ui.MangaChip
import com.josemi.animediary.core.ui.MangaColors
import com.josemi.animediary.core.ui.MangaFieldTitle
import com.josemi.animediary.core.ui.MangaInputFont
import com.josemi.animediary.core.ui.MangaPanel
import com.josemi.animediary.core.ui.MangaRatingQualifier
import com.josemi.animediary.core.ui.MangaRatingQualifierVariant
import com.josemi.animediary.core.ui.MangaScreenBackground
import com.josemi.animediary.core.ui.MangaTitleFont
import com.josemi.animediary.ui.theme.AnimeDiaryTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AnimeDetailScreen(
    anime: AnimeEntity,
    onBackClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier,
    genres: List<GenreEntity> = emptyList()
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = MangaColors.Paper,
            title = { Text("Eliminar anime", color = MangaColors.Ink) },
            text = {
                Text(
                    text = "Seguro que quieres eliminar ${anime.title}? Esta accion no se puede deshacer.",
                    color = MangaColors.SoftInk
                )
            },
            confirmButton = {
                Button(onClick = onDeleteClick) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    MangaScreenBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .padding(bottom = 24.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                MangaButton(label = "<", onClick = onBackClick)

                Text(
                    text = "Detalle",
                    color = MangaColors.Ink,
                    style = MaterialTheme.typography.headlineLarge,
                    fontFamily = MangaTitleFont,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(46.dp))
            }

            Spacer(modifier = Modifier.height(18.dp))

            MangaPanel {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(220.dp)
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        CoverImage(
                            coverPath = anime.coverPath,
                            fallbackColor = MangaColors.Highlight
                        )
                    }

                    Column(
                        modifier = Modifier
                            .width(155.dp)
                            .height(220.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = anime.title,
                                color = MangaColors.Ink,
                                style = MaterialTheme.typography.titleLarge,
                                fontFamily = MangaTitleFont,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "Personal: ${anime.personalStatus.label}",
                                color = MangaColors.Ink,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Oficial: ${anime.releaseStatus.label}",
                                color = MangaColors.SoftInk,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        MangaButton(
                            label = "Editar anime",
                            onClick = onEditClick,
                            modifier = Modifier.fillMaxWidth(),
                            iconRes = R.drawable.ic_manga_edit
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            MangaFieldTitle(text = "Estado")

            Spacer(modifier = Modifier.height(8.dp))

            MangaPanel {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MangaChip(label = anime.personalStatus.label, selected = true, onClick = {})
                    MangaChip(label = anime.releaseStatus.label, selected = false, onClick = {})
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                MangaPanel(modifier = Modifier.weight(1f)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Icon(
                            painter = painterResource(R.drawable.ic_manga_star),
                            contentDescription = null,
                            tint = MangaColors.Ink,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = anime.ratingValue?.let { "$it / 10" } ?: "Pendiente",
                            color = MangaColors.Ink,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Black
                        )
                    }
                }

                MangaRatingQualifier(
                    score = anime.ratingValue,
                    customLabel = anime.customRatingLabel,
                    mode = anime.ratingLabelMode,
                    editable = false,
                    onCustomLabelChange = {},
                    onModeChange = {},
                    modifier = Modifier.weight(1f),
                    variant = MangaRatingQualifierVariant.Prominent
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            MangaFieldTitle(text = "Resena")

            Spacer(modifier = Modifier.height(8.dp))

            MangaPanel {
                Text(
                    text = anime.review ?: "Sin resena todavia.",
                    color = MangaColors.SoftInk,
                    style = MaterialTheme.typography.bodyLarge,
                    fontFamily = MangaInputFont
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            MangaFieldTitle(text = "Generos")

            Spacer(modifier = Modifier.height(8.dp))

            MangaPanel {
                if (genres.isEmpty()) {
                    Text(
                        text = "Sin generos seleccionados.",
                        color = MangaColors.SoftInk,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        genres.forEach { genre ->
                            MangaChip(label = genre.name, selected = false, onClick = {})
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            MangaFieldTitle(text = "Progreso")

            Spacer(modifier = Modifier.height(8.dp))

            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProgressStatCard(
                        label = "Vistos",
                        value = anime.episodesWatched.takeIf { it > 0 }?.toString() ?: "Pendiente",
                        helper = "Capitulos",
                        iconRes = R.drawable.ic_manga_star,
                        modifier = Modifier.weight(1f)
                    )
                    ProgressStatCard(
                        label = "Total",
                        value = anime.totalEpisodes?.takeIf { it > 0 }?.toString() ?: "Pendiente",
                        helper = "Capitulos",
                        iconRes = R.drawable.ic_manga_stats,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProgressStatCard(
                        label = "Temporadas",
                        value = anime.seasonsWatched.takeIf { it > 0 }?.toString() ?: "Pendiente",
                        helper = "Vistas",
                        iconRes = R.drawable.ic_manga_seasons,
                        modifier = Modifier.weight(1f)
                    )
                    ProgressStatCard(
                        label = "Rewatch",
                        value = anime.rewatchCount.takeIf { it > 0 }?.toString() ?: "Pendiente",
                        helper = "Veces visto",
                        iconRes = R.drawable.ic_manga_sparkles,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProgressStatCard(
                        label = "Inicio",
                        value = anime.startedAt.toDisplayDateOrNull() ?: "Pendiente",
                        helper = "Fecha",
                        iconRes = R.drawable.ic_manga_calendar,
                        modifier = Modifier.weight(1f)
                    )
                    ProgressStatCard(
                        label = "Final",
                        value = anime.finishedAt.toDisplayDateOrNull()
                            ?: if (anime.releaseStatus == ReleaseStatus.Airing) "En emision" else "Pendiente",
                        helper = "Fecha",
                        iconRes = R.drawable.ic_manga_calendar,
                        modifier = Modifier.weight(1f)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ProgressStatCard(
                        label = "Favorito",
                        value = if (anime.isFavorite) "Si" else "No",
                        helper = "Personal",
                        iconRes = R.drawable.ic_manga_heart,
                        modifier = Modifier.weight(1f)
                    )
                    ProgressStatCard(
                        label = "Repetir",
                        value = if (anime.wouldRewatch) "Si" else "No",
                        helper = "Lo volveria a ver",
                        iconRes = R.drawable.ic_manga_sparkles,
                        modifier = Modifier.weight(1f)
                    )
                }

                MangaButton(
                    label = "Editar progreso",
                    onClick = onEditClick,
                    modifier = Modifier.fillMaxWidth(),
                    filled = true,
                    iconRes = R.drawable.ic_manga_edit
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MangaButton(
                    label = "Eliminar",
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.weight(1f)
                )
                MangaButton(
                    label = "Editar",
                    onClick = onEditClick,
                    modifier = Modifier.weight(1f),
                    filled = true,
                    iconRes = R.drawable.ic_manga_edit
                )
            }
        }
    }
}

private val DetailDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun Long?.toDisplayDateOrNull(): String? {
    return this?.let { millis ->
        Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(DetailDateFormatter)
    }
}

@Composable
private fun ProgressStatCard(
    label: String,
    value: String,
    helper: String,
    iconRes: Int,
    modifier: Modifier = Modifier
) {
    MangaPanel(
        modifier = modifier,
        contentPadding = PaddingValues(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = MangaColors.Ink,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = label.uppercase(),
                    color = MangaColors.Ink,
                    style = MaterialTheme.typography.labelLarge,
                    fontFamily = MangaTitleFont,
                    fontWeight = FontWeight.Black,
                    maxLines = 1
                )
            }

            Text(
                text = value,
                color = MangaColors.Ink,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = MangaInputFont,
                fontWeight = FontWeight.Black,
                maxLines = 1
            )

            Text(
                text = helper,
                color = MangaColors.SoftInk,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = MangaInputFont,
                maxLines = 1
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimeDetailScreenPreview() {
    AnimeDiaryTheme {
        AnimeDetailScreen(
            anime = AnimeEntity(
                id = 1,
                title = "Cowboy Bebop",
                personalStatus = PersonalStatus.Starting,
                releaseStatus = ReleaseStatus.Finished,
                ratingValue = 9.1,
                review = "Una joyita con muchisimo estilo.",
                createdAt = 0L,
                updatedAt = 0L
            ),
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}
