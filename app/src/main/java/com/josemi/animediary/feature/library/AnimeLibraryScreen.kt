package com.josemi.animediary.feature.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josemi.animediary.core.model.AnimeStatus
import com.josemi.animediary.ui.theme.AnimeDiaryTheme

@Composable
fun AnimeLibraryScreen(modifier: Modifier = Modifier) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatus by remember { mutableStateOf<AnimeStatus?>(null) }

    val visibleAnime = sampleAnime.filter { anime ->
        val matchesSearch = anime.title.contains(searchQuery, ignoreCase = true) ||
            anime.note.contains(searchQuery, ignoreCase = true) ||
            anime.status.label.contains(searchQuery, ignoreCase = true)

        val matchesStatus = selectedStatus == null || anime.status == selectedStatus

        matchesSearch && matchesStatus
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = Color(0xFF101116),
        floatingActionButton = {
            FloatingActionButton(
                onClick = { },
                containerColor = Color(0xFFFFD166),
                contentColor = Color(0xFF101116)
            ) {
                Text(
                    text = "+",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = Color(0xFF101116)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 18.dp, vertical = 16.dp)
            ) {
                Text(
                    text = "AnimeDiary",
                    color = Color.White,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Tu biblioteca personal",
                    color = Color(0xFFB7BBC8),
                    style = MaterialTheme.typography.bodyMedium
                )

                Spacer(modifier = Modifier.height(18.dp))

                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { newValue -> searchQuery = newValue },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    label = { Text("Buscar anime") }
                )

                Spacer(modifier = Modifier.height(14.dp))

                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
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

                Text(
                    text = "${visibleAnime.size} entradas encontradas",
                    color = Color(0xFFD7DAE5),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 88.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(visibleAnime) { anime ->
                        AnimeCard(anime = anime)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AnimeLibraryPreview() {
    AnimeDiaryTheme {
        AnimeLibraryScreen()
    }
}
