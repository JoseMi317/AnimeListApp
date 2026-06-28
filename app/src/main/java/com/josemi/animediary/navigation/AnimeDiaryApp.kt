package com.josemi.animediary.navigation

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.josemi.animediary.R
import com.josemi.animediary.core.data.AnimeRepository
import com.josemi.animediary.core.data.toPreview
import com.josemi.animediary.core.database.AnimeDatabase
import com.josemi.animediary.core.image.deleteInternalCover
import com.josemi.animediary.core.ui.MangaColors
import com.josemi.animediary.core.ui.MangaPanel
import com.josemi.animediary.core.ui.MangaScreenBackground
import com.josemi.animediary.core.ui.MangaSectionFont
import com.josemi.animediary.core.ui.MangaTitleFont
import com.josemi.animediary.feature.detail.AnimeDetailScreen
import com.josemi.animediary.feature.editor.AnimeEditorScreen
import com.josemi.animediary.feature.library.AnimeLibraryScreen
import com.josemi.animediary.feature.settings.SettingsScreen
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

private sealed interface AnimeDiaryScreen {
    data object Library : AnimeDiaryScreen
    data object NewAnime : AnimeDiaryScreen
    data object Stats : AnimeDiaryScreen
    data object Settings : AnimeDiaryScreen
    data class Detail(val animeId: Int) : AnimeDiaryScreen
    data class EditAnime(val animeId: Int) : AnimeDiaryScreen
}

private enum class MainTab(val label: String, val iconRes: Int) {
    Library("Biblioteca", R.drawable.ic_manga_star),
    NewAnime("Nuevo", R.drawable.ic_manga_plus),
    Stats("Stats", R.drawable.ic_manga_stats),
    Settings("Ajustes", R.drawable.ic_manga_settings)
}

@Composable
fun AnimeDiaryApp(modifier: Modifier = Modifier) {
    var currentScreen by remember { mutableStateOf<AnimeDiaryScreen>(AnimeDiaryScreen.Library) }
    val context = LocalContext.current
    val repository = remember {
        AnimeRepository(
            AnimeDatabase.getDatabase(context).animeDao()
        )
    }
    val coroutineScope = rememberCoroutineScope()
    val storedAnime by repository.observeAllAnime().collectAsState(initial = emptyList())
    val availableGenres by repository.observeAllGenres().collectAsState(initial = emptyList())
    val animeList = storedAnime.map { it.toPreview() }
    var pendingExportJson by remember { mutableStateOf<String?>(null) }
    var backupMessage by remember { mutableStateOf<String?>(null) }
    val backupFileFormatter = remember { DateTimeFormatter.ofPattern("yyyyMMdd_HHmm") }
    val exportBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        val json = pendingExportJson
        pendingExportJson = null

        if (uri == null || json == null) {
            backupMessage = "Exportacion cancelada."
            return@rememberLauncherForActivityResult
        }

        coroutineScope.launch {
            val result = runCatching {
                withContext(Dispatchers.IO) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        outputStream.write(json.toByteArray(Charsets.UTF_8))
                    } ?: error("No se pudo abrir el archivo.")
                }
            }

            backupMessage = if (result.isSuccess) {
                "Respaldo exportado correctamente."
            } else {
                "No se pudo exportar el respaldo."
            }
        }
    }
    val importBackupLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri == null) {
            backupMessage = "Importacion cancelada."
            return@rememberLauncherForActivityResult
        }

        coroutineScope.launch {
            val result = runCatching {
                val json = withContext(Dispatchers.IO) {
                    context.contentResolver.openInputStream(uri)?.bufferedReader()?.use { it.readText() }
                        ?: error("No se pudo abrir el respaldo.")
                }
                repository.importBackupJson(json)
            }

            backupMessage = result.fold(
                onSuccess = { importedCount -> "Importados $importedCount animes desde el respaldo." },
                onFailure = { "No se pudo importar el respaldo. Revisa que sea un JSON valido de AnimeDiary." }
            )
        }
    }

    LaunchedEffect(repository) {
        repository.ensureDefaultGenres()
    }

    Box(modifier = modifier.fillMaxSize()) {
        when (val screen = currentScreen) {
        AnimeDiaryScreen.Library -> {
            AnimeLibraryScreen(
                animeList = animeList,
                onAnimeClick = { animeId -> currentScreen = AnimeDiaryScreen.Detail(animeId) },
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimeDiaryScreen.NewAnime -> {
            AnimeEditorScreen(
                onBackClick = { currentScreen = AnimeDiaryScreen.Library },
                onSaveAnime = { newAnime, selectedGenreIds ->
                    coroutineScope.launch {
                        val animeId = repository.addAnime(newAnime).toInt()
                        repository.setGenresForAnime(animeId, selectedGenreIds)
                        currentScreen = AnimeDiaryScreen.Library
                    }
                },
                availableGenres = availableGenres,
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimeDiaryScreen.Stats -> {
            PlaceholderScreen(
                title = "Stats",
                message = "Aqui van a vivir tus rankings, promedios y generos mas vistos.",
                modifier = Modifier.fillMaxSize()
            )
        }

        AnimeDiaryScreen.Settings -> {
            SettingsScreen(
                animeCount = storedAnime.size,
                backupMessage = backupMessage,
                onExportClick = {
                    coroutineScope.launch {
                        val result = runCatching { repository.exportBackupJson() }
                        result.fold(
                            onSuccess = { json ->
                                pendingExportJson = json
                                val timestamp = LocalDateTime.now().format(backupFileFormatter)
                                exportBackupLauncher.launch("animediary_backup_$timestamp.json")
                            },
                            onFailure = {
                                backupMessage = "No se pudo preparar el respaldo."
                            }
                        )
                    }
                },
                onImportClick = {
                    importBackupLauncher.launch(arrayOf("application/json", "text/*", "*/*"))
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        is AnimeDiaryScreen.Detail -> {
            val selectedAnime = storedAnime.firstOrNull { it.id == screen.animeId }

            if (selectedAnime == null) {
                currentScreen = AnimeDiaryScreen.Library
            } else {
                val selectedGenres by repository.observeGenresForAnime(selectedAnime.id)
                    .collectAsState(initial = emptyList())

                AnimeDetailScreen(
                    anime = selectedAnime,
                    onBackClick = { currentScreen = AnimeDiaryScreen.Library },
                    onEditClick = { currentScreen = AnimeDiaryScreen.EditAnime(selectedAnime.id) },
                    onDeleteClick = {
                        coroutineScope.launch {
                            repository.deleteAnime(selectedAnime)
                            deleteInternalCover(selectedAnime.coverPath)
                            currentScreen = AnimeDiaryScreen.Library
                        }
                    },
                    genres = selectedGenres,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }

        is AnimeDiaryScreen.EditAnime -> {
            val selectedAnime = storedAnime.firstOrNull { it.id == screen.animeId }

            if (selectedAnime == null) {
                currentScreen = AnimeDiaryScreen.Library
            } else {
                val selectedGenreIds by repository.observeGenreIdsForAnime(selectedAnime.id)
                    .collectAsState(initial = emptyList())

                AnimeEditorScreen(
                    animeToEdit = selectedAnime,
                    onBackClick = { currentScreen = AnimeDiaryScreen.Detail(selectedAnime.id) },
                    onSaveAnime = { updatedAnime, selectedGenres ->
                        coroutineScope.launch {
                            repository.updateAnime(updatedAnime)
                            repository.setGenresForAnime(updatedAnime.id, selectedGenres)
                            currentScreen = AnimeDiaryScreen.Detail(updatedAnime.id)
                        }
                    },
                    availableGenres = availableGenres,
                    initialSelectedGenreIds = selectedGenreIds.toSet(),
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }

        val selectedTab = currentScreen.toMainTab()
        if (selectedTab != null) {
            MangaBottomNav(
                selectedTab = selectedTab,
                onTabSelected = { tab ->
                    currentScreen = when (tab) {
                        MainTab.Library -> AnimeDiaryScreen.Library
                        MainTab.NewAnime -> AnimeDiaryScreen.NewAnime
                        MainTab.Stats -> AnimeDiaryScreen.Stats
                        MainTab.Settings -> AnimeDiaryScreen.Settings
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = 12.dp, vertical = 10.dp)
            )
        }
    }
}

private fun AnimeDiaryScreen.toMainTab(): MainTab? {
    return when (this) {
        AnimeDiaryScreen.Library -> MainTab.Library
        AnimeDiaryScreen.NewAnime -> MainTab.NewAnime
        AnimeDiaryScreen.Stats -> MainTab.Stats
        AnimeDiaryScreen.Settings -> MainTab.Settings
        is AnimeDiaryScreen.Detail -> null
        is AnimeDiaryScreen.EditAnime -> null
    }
}

@Composable
private fun MangaBottomNav(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    MangaPanel(
        modifier = modifier.fillMaxWidth(),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            MainTab.entries.forEach { tab ->
                MangaNavItem(
                    tab = tab,
                    selected = selectedTab == tab,
                    onClick = { onTabSelected(tab) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun MangaNavItem(
    tab: MainTab,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(
                color = if (selected) MangaColors.Ink else MangaColors.Panel,
                shape = RoundedCornerShape(8.dp)
            )
            .border(2.dp, MangaColors.Ink, RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 7.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(tab.iconRes),
            contentDescription = tab.label,
            tint = if (selected) MangaColors.Panel else MangaColors.Ink,
            modifier = Modifier.size(21.dp)
        )
        Text(
            text = tab.label,
            color = if (selected) MangaColors.Panel else MangaColors.Ink,
            style = MaterialTheme.typography.labelSmall,
            fontFamily = MangaSectionFont,
            fontWeight = FontWeight.Black,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun PlaceholderScreen(
    title: String,
    message: String,
    modifier: Modifier = Modifier
) {
    MangaScreenBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .padding(bottom = 110.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            Text(
                text = title,
                color = MangaColors.Ink,
                style = MaterialTheme.typography.headlineMedium,
                fontFamily = MangaTitleFont,
                fontWeight = FontWeight.Black
            )
            MangaPanel {
                Text(
                    text = message,
                    color = MangaColors.SoftInk,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Text(
                text = "Proximamente",
                color = MangaColors.Ink,
                style = MaterialTheme.typography.titleLarge,
                fontFamily = MangaSectionFont,
                fontWeight = FontWeight.Black
            )
        }
    }
}
