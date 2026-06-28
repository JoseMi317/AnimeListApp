package com.josemi.animediary.feature.settings

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.josemi.animediary.R
import com.josemi.animediary.core.ui.MangaButton
import com.josemi.animediary.core.ui.MangaColors
import com.josemi.animediary.core.ui.MangaInputFont
import com.josemi.animediary.core.ui.MangaPanel
import com.josemi.animediary.core.ui.MangaScreenBackground
import com.josemi.animediary.core.ui.MangaSectionFont
import com.josemi.animediary.core.ui.MangaTitleFont
import com.josemi.animediary.ui.theme.AnimeDiaryTheme

@Composable
fun SettingsScreen(
    animeCount: Int,
    backupMessage: String?,
    onExportClick: () -> Unit,
    onImportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showImportDialog by remember { mutableStateOf(false) }

    if (showImportDialog) {
        AlertDialog(
            onDismissRequest = { showImportDialog = false },
            containerColor = MangaColors.Paper,
            titleContentColor = MangaColors.Ink,
            textContentColor = MangaColors.SoftInk,
            title = {
                Text(
                    text = "Importar respaldo",
                    fontFamily = MangaTitleFont,
                    fontWeight = FontWeight.Black
                )
            },
            text = {
                Text(
                    text = "Esto reemplaza tus animes actuales por los del archivo JSON. Hazlo solo si confias en ese respaldo.",
                    fontFamily = MangaInputFont
                )
            },
            confirmButton = {
                MangaButton(
                    label = "Importar",
                    onClick = {
                        showImportDialog = false
                        onImportClick()
                    },
                    filled = true
                )
            },
            dismissButton = {
                MangaButton(
                    label = "Cancelar",
                    onClick = { showImportDialog = false }
                )
            }
        )
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
                text = "Ajustes",
                color = MangaColors.Ink,
                style = MaterialTheme.typography.headlineLarge,
                fontFamily = MangaTitleFont,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            MangaPanel(contentPadding = PaddingValues(14.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Respaldo",
                        color = MangaColors.Ink,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = MangaSectionFont,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "$animeCount animes registrados",
                        color = MangaColors.SoftInk,
                        style = MaterialTheme.typography.bodyLarge,
                        fontFamily = MangaInputFont,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "El JSON guarda tus datos y generos. Las portadas locales no viajan dentro del respaldo todavia.",
                        color = MangaColors.SoftInk,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = MangaInputFont
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                MangaButton(
                    label = "Exportar",
                    onClick = onExportClick,
                    modifier = Modifier.weight(1f),
                    filled = true,
                    iconRes = R.drawable.ic_manga_settings
                )
                MangaButton(
                    label = "Importar",
                    onClick = { showImportDialog = true },
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.ic_manga_plus
                )
            }

            if (!backupMessage.isNullOrBlank()) {
                MangaPanel(contentPadding = PaddingValues(12.dp)) {
                    Text(
                        text = backupMessage,
                        color = MangaColors.Ink,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = MangaInputFont,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            MangaPanel(contentPadding = PaddingValues(14.dp)) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Pendiente",
                        color = MangaColors.Ink,
                        style = MaterialTheme.typography.titleLarge,
                        fontFamily = MangaSectionFont,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Despues podemos hacer backup completo con portadas, exportar PDF y estadisticas.",
                        color = MangaColors.SoftInk,
                        style = MaterialTheme.typography.bodyMedium,
                        fontFamily = MangaInputFont
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SettingsScreenPreview() {
    AnimeDiaryTheme {
        SettingsScreen(
            animeCount = 12,
            backupMessage = "Respaldo listo.",
            onExportClick = {},
            onImportClick = {}
        )
    }
}
