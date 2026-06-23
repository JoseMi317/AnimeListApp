package com.josemi.animediary.feature.library

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josemi.animediary.core.model.AnimeStatus
import com.josemi.animediary.ui.theme.AnimeDiaryTheme

@Composable
fun AnimeLibraryScreen(modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier.fillMaxSize(),
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

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatusPill(label = "Todos", color = Color(0xFFE5E7EB))
                StatusPill(label = "Viendo", color = AnimeStatus.Watching.color)
                StatusPill(label = "Terminado", color = AnimeStatus.Finished.color)
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text(
                text = "${sampleAnime.size} entradas registradas",
                color = Color(0xFFD7DAE5),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(bottom = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(sampleAnime) { anime ->
                    AnimeCard(anime = anime)
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

