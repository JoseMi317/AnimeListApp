package com.josemi.animediary.feature.editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josemi.animediary.core.data.createAnimeEntity
import com.josemi.animediary.core.database.AnimeEntity
import com.josemi.animediary.core.database.GenreEntity
import com.josemi.animediary.core.image.copyCoverToInternalStorage
import com.josemi.animediary.core.model.PersonalStatus
import com.josemi.animediary.core.model.ReleaseStatus
import com.josemi.animediary.core.ui.CoverImage
import com.josemi.animediary.ui.theme.AnimeDiaryTheme

@Composable
fun AnimeEditorScreen(
    onBackClick: () -> Unit,
    onSaveAnime: (AnimeEntity, Set<Int>) -> Unit,
    modifier: Modifier = Modifier,
    animeToEdit: AnimeEntity? = null,
    availableGenres: List<GenreEntity> = emptyList(),
    initialSelectedGenreIds: Set<Int> = emptySet()
) {
    var title by remember(animeToEdit) { mutableStateOf(animeToEdit?.title.orEmpty()) }
    var personalStatus by remember(animeToEdit) {
        mutableStateOf(animeToEdit?.personalStatus ?: PersonalStatus.Starting)
    }
    var releaseStatus by remember(animeToEdit) {
        mutableStateOf(animeToEdit?.releaseStatus ?: ReleaseStatus.Unknown)
    }
    var rating by remember(animeToEdit) {
        mutableStateOf(animeToEdit?.ratingValue?.toString().orEmpty())
    }
    var review by remember(animeToEdit) { mutableStateOf(animeToEdit?.review.orEmpty()) }
    var notes by remember(animeToEdit) { mutableStateOf(animeToEdit?.notes.orEmpty()) }
    var isFavorite by remember(animeToEdit) { mutableStateOf(animeToEdit?.isFavorite ?: false) }
    var wouldRewatch by remember(animeToEdit) { mutableStateOf(animeToEdit?.wouldRewatch ?: false) }
    var selectedGenreIds by remember(animeToEdit?.id, initialSelectedGenreIds) {
        mutableStateOf(initialSelectedGenreIds)
    }
    var selectedCoverUri by remember(animeToEdit) { mutableStateOf<android.net.Uri?>(null) }
    var showMissingTitleDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val coverPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedCoverUri = uri
    }

    if (showMissingTitleDialog) {
        AlertDialog(
            onDismissRequest = { showMissingTitleDialog = false },
            title = { Text("Titulo requerido") },
            text = { Text("Agrega el nombre del anime antes de guardarlo.") },
            confirmButton = {
                Button(onClick = { showMissingTitleDialog = false }) {
                    Text("Entendido")
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
            Button(onClick = onBackClick) {
                Text("Volver")
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = if (animeToEdit == null) "Nuevo anime" else "Editar anime",
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Titulo") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(0.72f)
                    .clip(RoundedCornerShape(14.dp))
            ) {
                CoverImage(
                    coverPath = animeToEdit?.coverPath,
                    coverUri = selectedCoverUri,
                    fallbackColor = Color(0xFF3B82F6)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    coverPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (selectedCoverUri == null && animeToEdit?.coverPath == null) "Elegir portada" else "Cambiar portada")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Estado personal",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(PersonalStatus.entries.size) { index ->
                    val status = PersonalStatus.entries[index]
                    ChoicePill(
                        label = status.label,
                        selected = personalStatus == status,
                        onClick = { personalStatus = status }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Estado oficial",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(ReleaseStatus.entries.size) { index ->
                    val status = ReleaseStatus.entries[index]
                    ChoicePill(
                        label = status.label,
                        selected = releaseStatus == status,
                        onClick = { releaseStatus = status }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Generos",
                color = Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(availableGenres.size) { index ->
                    val genre = availableGenres[index]
                    ChoicePill(
                        label = genre.name,
                        selected = genre.id in selectedGenreIds,
                        onClick = {
                            selectedGenreIds = if (genre.id in selectedGenreIds) {
                                selectedGenreIds - genre.id
                            } else {
                                selectedGenreIds + genre.id
                            }
                        }
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = rating,
                onValueChange = { rating = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Puntuacion 0-10") },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = review,
                onValueChange = { review = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp),
                label = { Text("Reseña") }
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = notes,
                onValueChange = { notes = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(110.dp),
                label = { Text("Notas") }
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    ChoicePill(
                        label = "Favorito",
                        selected = isFavorite,
                        onClick = { isFavorite = !isFavorite }
                    )
                }
                item {
                    ChoicePill(
                        label = "Lo volveria a ver",
                        selected = wouldRewatch,
                        onClick = { wouldRewatch = !wouldRewatch }
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = {
                    if (title.isBlank()) {
                        showMissingTitleDialog = true
                    } else {
                        val now = System.currentTimeMillis()
                        val ratingValue = rating.toRatingValueOrNull()
                        val coverPath = selectedCoverUri?.let { uri ->
                            copyCoverToInternalStorage(
                                context = context,
                                sourceUri = uri,
                                animeIdHint = animeToEdit?.id ?: 0,
                                now = now
                            )
                        } ?: animeToEdit?.coverPath

                        val savedAnime = if (animeToEdit == null) {
                            createAnimeEntity(
                                title = title,
                                personalStatus = personalStatus,
                                releaseStatus = releaseStatus,
                                now = now
                            )
                        } else {
                            animeToEdit.copy(
                                title = title.trim(),
                                personalStatus = personalStatus,
                                releaseStatus = releaseStatus,
                                updatedAt = now
                            )
                        }.copy(
                            ratingValue = ratingValue,
                            review = review.ifBlank { null },
                            notes = notes.ifBlank { null },
                            isFavorite = isFavorite,
                            wouldRewatch = wouldRewatch,
                            coverPath = coverPath
                        )

                        onSaveAnime(savedAnime, selectedGenreIds)
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(if (animeToEdit == null) "Guardar anime" else "Guardar cambios")
            }
        }
    }
}

private fun String.toRatingValueOrNull(): Double? {
    return trim()
        .replace(",", ".")
        .toDoubleOrNull()
        ?.takeIf { it in 0.0..10.0 }
}

@Composable
private fun ChoicePill(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Text(
        text = label,
        color = if (selected) Color(0xFF101116) else Color(0xFFD7DAE5),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        maxLines = 1,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(if (selected) Color(0xFFFFD166) else Color(0xFF262A35))
            .clickable(onClick = onClick)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}

@Preview(showBackground = true)
@Composable
fun AnimeEditorScreenPreview() {
    AnimeDiaryTheme {
        AnimeEditorScreen(
            onBackClick = {},
            onSaveAnime = { _, _ -> }
        )
    }
}
