package com.josemi.animediary.feature.editor

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josemi.animediary.R
import com.josemi.animediary.core.data.createAnimeEntity
import com.josemi.animediary.core.database.AnimeEntity
import com.josemi.animediary.core.database.GenreEntity
import com.josemi.animediary.core.image.copyCoverToInternalStorage
import com.josemi.animediary.core.model.PersonalStatus
import com.josemi.animediary.core.model.RatingLabelMode
import com.josemi.animediary.core.model.ReleaseStatus
import com.josemi.animediary.core.ui.CoverImage
import com.josemi.animediary.core.ui.MangaButton
import com.josemi.animediary.core.ui.MangaChip
import com.josemi.animediary.core.ui.MangaColors
import com.josemi.animediary.core.ui.MangaFieldTitle
import com.josemi.animediary.core.ui.MangaInputFont
import com.josemi.animediary.core.ui.MangaPanel
import com.josemi.animediary.core.ui.MangaRatingQualifier
import com.josemi.animediary.core.ui.MangaScreenBackground
import com.josemi.animediary.core.ui.MangaTitleFont
import com.josemi.animediary.ui.theme.AnimeDiaryTheme
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
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
    var ratingLabelMode by remember(animeToEdit) {
        mutableStateOf(animeToEdit?.ratingLabelMode ?: RatingLabelMode.Auto)
    }
    var customRatingLabel by remember(animeToEdit) {
        mutableStateOf(animeToEdit?.customRatingLabel.orEmpty())
    }
    var review by remember(animeToEdit) { mutableStateOf(animeToEdit?.review.orEmpty()) }
    var isFavorite by remember(animeToEdit) { mutableStateOf(animeToEdit?.isFavorite ?: false) }
    var wouldRewatch by remember(animeToEdit) { mutableStateOf(animeToEdit?.wouldRewatch ?: false) }
    var episodesWatched by remember(animeToEdit) {
        mutableStateOf(animeToEdit?.episodesWatched?.takeIf { it > 0 }?.toString().orEmpty())
    }
    var totalEpisodes by remember(animeToEdit) {
        mutableStateOf(animeToEdit?.totalEpisodes?.takeIf { it > 0 }?.toString().orEmpty())
    }
    var seasonsWatched by remember(animeToEdit) {
        mutableStateOf(animeToEdit?.seasonsWatched?.takeIf { it > 0 }?.toString().orEmpty())
    }
    var rewatchCount by remember(animeToEdit) {
        mutableStateOf(animeToEdit?.rewatchCount?.takeIf { it > 0 }?.toString().orEmpty())
    }
    var startedAt by remember(animeToEdit) { mutableStateOf(animeToEdit?.startedAt) }
    var finishedAt by remember(animeToEdit) { mutableStateOf(animeToEdit?.finishedAt) }
    var activeDateField by remember { mutableStateOf<DateField?>(null) }
    var selectedGenreIds by remember(animeToEdit?.id, initialSelectedGenreIds) {
        mutableStateOf(initialSelectedGenreIds)
    }
    var selectedCoverUri by remember(animeToEdit) { mutableStateOf<android.net.Uri?>(null) }
    var showMissingTitleDialog by remember { mutableStateOf(false) }
    var showGenreDialog by remember { mutableStateOf(false) }
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

    if (showGenreDialog) {
        AlertDialog(
            onDismissRequest = { showGenreDialog = false },
            containerColor = MangaColors.Paper,
            titleContentColor = MangaColors.Ink,
            textContentColor = MangaColors.Ink,
            title = {
                Text(
                    text = "Generos",
                    fontFamily = MangaTitleFont,
                    color = MangaColors.Ink
                )
            },
            text = {
                MangaPanel(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(360.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .verticalScroll(rememberScrollState())
                    ) {
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            availableGenres.forEach { genre ->
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
                    }
                }
            },
            confirmButton = {
                MangaButton(
                    label = "Listo",
                    onClick = { showGenreDialog = false },
                    filled = true
                )
            }
        )
    }

    activeDateField?.let { dateField ->
        MangaDatePickerDialog(
            initialDateMillis = when (dateField) {
                DateField.Start -> startedAt
                DateField.Finish -> finishedAt
            },
            onDismiss = { activeDateField = null },
            onConfirm = { selectedDateMillis ->
                when (dateField) {
                    DateField.Start -> startedAt = selectedDateMillis
                    DateField.Finish -> finishedAt = selectedDateMillis
                }
                activeDateField = null
            }
        )
    }

    MangaScreenBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .padding(bottom = 118.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                MangaButton(label = "<", onClick = onBackClick)

                Text(
                    text = if (animeToEdit == null) "Nuevo anime" else "Editar anime",
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
                            .height(260.dp)
                            .clip(RoundedCornerShape(6.dp))
                    ) {
                        CoverImage(
                            coverPath = animeToEdit?.coverPath,
                            coverUri = selectedCoverUri,
                            fallbackColor = MangaColors.Highlight
                        )
                    }

                    Column(
                        modifier = Modifier
                            .width(145.dp)
                            .height(260.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_manga_image),
                            contentDescription = null,
                            tint = MangaColors.Ink,
                            modifier = Modifier.size(46.dp)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        MangaButton(
                            label = "Elegir portada",
                            onClick = {
                                coverPickerLauncher.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            MangaFieldTitle(text = "Titulo")

            Spacer(modifier = Modifier.height(8.dp))

            MangaTextInput(
                value = title,
                onValueChange = { title = it },
                placeholder = "Escribe el titulo del anime..."
            )

            Spacer(modifier = Modifier.height(16.dp))

            MangaFieldTitle(text = "Estado personal")

            Spacer(modifier = Modifier.height(8.dp))

            MangaPanel {
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            MangaFieldTitle(text = "Estado oficial")

            Spacer(modifier = Modifier.height(8.dp))

            MangaPanel {
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
            }

            Spacer(modifier = Modifier.height(16.dp))

            MangaFieldTitle(text = "Generos")

            Spacer(modifier = Modifier.height(8.dp))

            MangaPanel {
                val selectedGenres = availableGenres.filter { it.id in selectedGenreIds }

                if (selectedGenres.isEmpty()) {
                    Text(
                        text = "Sin generos seleccionados",
                        color = MangaColors.SoftInk,
                        style = MaterialTheme.typography.bodyMedium
                    )
                } else {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(selectedGenres.size) { index ->
                            ChoicePill(
                                label = selectedGenres[index].name,
                                selected = true,
                                onClick = { showGenreDialog = true }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            MangaButton(
                label = "Elegir generos",
                onClick = { showGenreDialog = true },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            MangaFieldTitle(text = "Puntuacion")

            Spacer(modifier = Modifier.height(8.dp))

            MangaTextInput(
                value = rating,
                onValueChange = { newValue ->
                    if (newValue.isValidRatingInput()) {
                        rating = newValue
                    }
                },
                placeholder = "0-10",
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
            )

            Spacer(modifier = Modifier.height(12.dp))

            MangaRatingQualifier(
                score = rating.toRatingValueOrNull(),
                customLabel = customRatingLabel,
                mode = ratingLabelMode,
                editable = true,
                onCustomLabelChange = { customRatingLabel = it },
                onModeChange = { ratingLabelMode = it }
            )

            Spacer(modifier = Modifier.height(12.dp))

            MangaFieldTitle(text = "Resena")

            Spacer(modifier = Modifier.height(8.dp))

            MangaTextInput(
                value = review,
                onValueChange = { review = it },
                placeholder = "Escribe tu resena...",
                minHeight = 150.dp
            )

            if (animeToEdit != null) {
                Spacer(modifier = Modifier.height(16.dp))

                MangaFieldTitle(text = "Progreso")

                Spacer(modifier = Modifier.height(8.dp))

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ProgressNumberCard(
                            label = "Vistos",
                            helper = "Capitulos que ya llevas",
                            iconRes = R.drawable.ic_manga_star,
                            value = episodesWatched,
                            onValueChange = { episodesWatched = it },
                            placeholder = "0",
                            modifier = Modifier.weight(1f)
                        )
                        ProgressNumberCard(
                            label = "Total",
                            helper = "Capitulos de la obra",
                            iconRes = R.drawable.ic_manga_stats,
                            value = totalEpisodes,
                            onValueChange = { totalEpisodes = it },
                            placeholder = "0",
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        ProgressNumberCard(
                            label = "Temporadas",
                            helper = "Temporadas vistas",
                            iconRes = R.drawable.ic_manga_seasons,
                            value = seasonsWatched,
                            onValueChange = { seasonsWatched = it },
                            placeholder = "0",
                            modifier = Modifier.weight(1f)
                        )
                        ProgressNumberCard(
                            label = "Rewatch",
                            helper = "Veces que lo viste",
                            iconRes = R.drawable.ic_manga_sparkles,
                            value = rewatchCount,
                            onValueChange = { rewatchCount = it },
                            placeholder = "0",
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                MangaFieldTitle(text = "Fechas")

                Spacer(modifier = Modifier.height(8.dp))

                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MangaDateField(
                        label = "Fecha inicio",
                        value = startedAt.toDisplayDate(),
                        iconRes = R.drawable.ic_manga_calendar,
                        onClick = { activeDateField = DateField.Start },
                        modifier = Modifier.weight(1f)
                    )
                    MangaDateField(
                        label = "Fecha final",
                        value = if (releaseStatus == ReleaseStatus.Airing) "En emision" else finishedAt.toDisplayDate(),
                        iconRes = R.drawable.ic_manga_calendar,
                        onClick = {
                            if (releaseStatus != ReleaseStatus.Airing) {
                                activeDateField = DateField.Finish
                            }
                        },
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                MangaFieldTitle(text = "Cierre personal")

                Spacer(modifier = Modifier.height(8.dp))

                MangaPanel {
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
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            MangaButton(
                label = if (animeToEdit == null) "Guardar anime" else "Guardar cambios",
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
                            customRatingLabel = customRatingLabel.ifBlank { null },
                            ratingLabelMode = ratingLabelMode,
                            episodesWatched = episodesWatched.toIntOrZero(),
                            totalEpisodes = totalEpisodes.toIntOrNull(),
                            seasonsWatched = seasonsWatched.toIntOrZero(),
                            rewatchCount = rewatchCount.toIntOrZero(),
                            startedAt = startedAt,
                            finishedAt = if (releaseStatus == ReleaseStatus.Airing) null else finishedAt,
                            isFavorite = isFavorite,
                            wouldRewatch = wouldRewatch,
                            coverPath = coverPath
                        )

                        onSaveAnime(savedAnime, selectedGenreIds)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                filled = true
            )
        }
    }
}

private fun String.toRatingValueOrNull(): Double? {
    return trim()
        .replace(",", ".")
        .toDoubleOrNull()
        ?.takeIf { it in 0.0..10.0 }
}

private fun String.isValidRatingInput(): Boolean {
    if (isEmpty()) return true

    val normalized = replace(",", ".")
    if (!Regex("""^\d{0,2}(\.\d?)?$""").matches(normalized)) return false

    return normalized.toDoubleOrNull()?.let { it <= 10.0 } ?: normalized.endsWith(".")
}

private fun String.isValidPositiveIntInput(): Boolean {
    return isEmpty() || Regex("""^\d{0,4}$""").matches(this)
}

private fun String.toIntOrZero(): Int {
    return toIntOrNull() ?: 0
}

private enum class DateField {
    Start,
    Finish
}

private val AnimeDateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")

private fun Long?.toDisplayDate(): String {
    return this?.let { millis ->
        Instant.ofEpochMilli(millis)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(AnimeDateFormatter)
    } ?: "Pendiente"
}

@Composable
private fun MangaTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier,
    minHeight: Dp = 56.dp,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default
) {
    MangaPanel(modifier = modifier.fillMaxWidth()) {
        BasicTextField(
            value = value,
            onValueChange = onValueChange,
            keyboardOptions = keyboardOptions,
            textStyle = MaterialTheme.typography.bodyLarge.copy(
                color = MangaColors.Ink,
                fontFamily = MangaInputFont
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(minHeight),
            decorationBox = { innerTextField ->
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
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

@Composable
private fun ProgressNumberCard(
    label: String,
    helper: String,
    iconRes: Int,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    MangaPanel(
        modifier = modifier,
        contentPadding = PaddingValues(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
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
                text = helper,
                color = MangaColors.SoftInk,
                style = MaterialTheme.typography.bodySmall,
                fontFamily = MangaInputFont,
                minLines = 2
            )

            NumberSlot(
                value = value,
                onValueChange = onValueChange,
                placeholder = placeholder
            )

            MangaButton(
                label = "+1",
                onClick = { onValueChange(((value.toIntOrNull() ?: 0) + 1).toString()) },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun NumberSlot(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String
) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(46.dp),
        shape = RoundedCornerShape(8.dp),
        color = MangaColors.Panel,
        border = BorderStroke(2.dp, MangaColors.Ink)
    ) {
        BasicTextField(
            value = value,
            onValueChange = { newValue ->
                if (newValue.isValidPositiveIntInput()) {
                    onValueChange(newValue)
                }
            },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            textStyle = MaterialTheme.typography.titleLarge.copy(
                color = MangaColors.Ink,
                fontFamily = MangaInputFont,
                textAlign = TextAlign.Center
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 9.dp),
            decorationBox = { innerTextField ->
                Box(modifier = Modifier.fillMaxWidth()) {
                    if (value.isEmpty()) {
                        Text(
                            text = placeholder,
                            color = MangaColors.MutedInk,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = MangaInputFont,
                                textAlign = TextAlign.Center
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    innerTextField()
                }
            }
        )
    }
}

@Composable
private fun MangaDateField(
    label: String,
    value: String,
    iconRes: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    MangaPanel(modifier = modifier) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null,
                    tint = MangaColors.Ink,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = label.uppercase(),
                    color = MangaColors.Ink,
                    style = MaterialTheme.typography.labelMedium,
                    fontFamily = MangaTitleFont,
                    fontWeight = FontWeight.Black,
                    maxLines = 1
                )
            }
            MangaButton(
                label = value,
                onClick = onClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MangaDatePickerDialog(
    initialDateMillis: Long?,
    onDismiss: () -> Unit,
    onConfirm: (Long?) -> Unit
) {
    val datePickerState = androidx.compose.material3.rememberDatePickerState(
        initialSelectedDateMillis = initialDateMillis
    )
    val colors = DatePickerDefaults.colors(
        containerColor = MangaColors.Paper,
        titleContentColor = MangaColors.Ink,
        headlineContentColor = MangaColors.Ink,
        weekdayContentColor = MangaColors.SoftInk,
        subheadContentColor = MangaColors.Ink,
        yearContentColor = MangaColors.Ink,
        currentYearContentColor = MangaColors.Ink,
        selectedYearContainerColor = MangaColors.Ink,
        selectedYearContentColor = MangaColors.Panel,
        dayContentColor = MangaColors.Ink,
        selectedDayContainerColor = MangaColors.Ink,
        selectedDayContentColor = MangaColors.Panel,
        todayContentColor = MangaColors.Ink,
        todayDateBorderColor = MangaColors.Ink
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = colors,
        confirmButton = {
            MangaButton(
                label = "Listo",
                onClick = { onConfirm(datePickerState.selectedDateMillis) },
                filled = true
            )
        },
        dismissButton = {
            MangaButton(
                label = "Cancelar",
                onClick = onDismiss
            )
        }
    ) {
        DatePicker(
            state = datePickerState,
            colors = colors
        )
    }
}

@Composable
private fun ChoicePill(
    label: String,
    selected: Boolean,
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
