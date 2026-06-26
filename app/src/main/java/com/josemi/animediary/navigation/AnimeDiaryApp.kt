package com.josemi.animediary.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.Modifier
import com.josemi.animediary.core.data.AnimeRepository
import com.josemi.animediary.core.data.toPreview
import com.josemi.animediary.core.database.AnimeDatabase
import com.josemi.animediary.core.image.deleteInternalCover
import com.josemi.animediary.feature.detail.AnimeDetailScreen
import com.josemi.animediary.feature.editor.AnimeEditorScreen
import com.josemi.animediary.feature.library.AnimeLibraryScreen
import kotlinx.coroutines.launch

private sealed interface AnimeDiaryScreen {
    data object Library : AnimeDiaryScreen
    data object NewAnime : AnimeDiaryScreen
    data class Detail(val animeId: Int) : AnimeDiaryScreen
    data class EditAnime(val animeId: Int) : AnimeDiaryScreen
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

    LaunchedEffect(repository) {
        repository.ensureDefaultGenres()
    }

    when (val screen = currentScreen) {
        AnimeDiaryScreen.Library -> {
            AnimeLibraryScreen(
                animeList = animeList,
                onAddAnimeClick = { currentScreen = AnimeDiaryScreen.NewAnime },
                onAnimeClick = { animeId -> currentScreen = AnimeDiaryScreen.Detail(animeId) },
                modifier = modifier
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
                modifier = modifier
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
                    modifier = modifier
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
                    modifier = modifier
                )
            }
        }
    }
}
