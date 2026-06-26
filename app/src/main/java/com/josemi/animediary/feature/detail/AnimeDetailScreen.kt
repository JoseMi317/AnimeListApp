package com.josemi.animediary.feature.detail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josemi.animediary.core.database.AnimeEntity
import com.josemi.animediary.core.database.GenreEntity
import com.josemi.animediary.core.model.PersonalStatus
import com.josemi.animediary.core.model.ReleaseStatus
import com.josemi.animediary.core.ui.CoverImage
import com.josemi.animediary.ui.theme.AnimeDiaryTheme

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
            title = { Text("Eliminar anime") },
            text = { Text("Seguro que quieres eliminar ${anime.title}? Esta accion no se puede deshacer.") },
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

    Surface(
        modifier = modifier.fillMaxSize(),
        color = Color(0xFF101116)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp)
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = onBackClick) {
                    Text("Volver")
                }
                Button(onClick = onEditClick) {
                    Text("Editar")
                }
                OutlinedButton(onClick = { showDeleteDialog = true }) {
                    Text("Eliminar")
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                CoverImage(
                    coverPath = anime.coverPath,
                    fallbackColor = Color(0xFF3B82F6)
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = anime.title,
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Personal: ${anime.personalStatus.label}",
                color = Color(0xFFFFD166),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Oficial: ${anime.releaseStatus.label}",
                color = Color(0xFFB7BBC8),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(18.dp))

            DetailLine(label = "Puntuacion", value = anime.ratingValue?.let { "$it/10" })
            DetailLine(label = "Episodios vistos", value = anime.episodesWatched.toString())
            DetailLine(label = "Temporadas vistas", value = anime.seasonsWatched.toString())
            DetailLine(label = "Veces visto", value = anime.rewatchCount.toString())
            DetailLine(label = "Favorito", value = if (anime.isFavorite) "Si" else "No")
            DetailLine(label = "Lo volveria a ver", value = if (anime.wouldRewatch) "Si" else "No")
            DetailLine(
                label = "Generos",
                value = genres.takeIf { it.isNotEmpty() }?.joinToString { it.name }
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Mi reseña",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = anime.review ?: "Sin reseña todavia.",
                color = Color(0xFFB7BBC8),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "Notas",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = anime.notes ?: "Sin notas adicionales.",
                color = Color(0xFFB7BBC8),
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}

@Composable
private fun DetailLine(label: String, value: String?) {
    Text(
        text = "$label: ${value ?: "Pendiente"}",
        color = Color(0xFFD7DAE5),
        style = MaterialTheme.typography.bodyLarge
    )
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
                createdAt = 0L,
                updatedAt = 0L
            ),
            onBackClick = {},
            onEditClick = {},
            onDeleteClick = {}
        )
    }
}
