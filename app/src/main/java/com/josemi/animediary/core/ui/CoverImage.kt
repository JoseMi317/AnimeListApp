package com.josemi.animediary.core.ui

import android.net.Uri
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext

@Composable
fun CoverImage(
    coverPath: String?,
    coverUri: Uri? = null,
    fallbackColor: Color,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val bitmap = remember(coverPath, coverUri) {
        when {
            coverUri != null -> {
                context.contentResolver.openInputStream(coverUri)?.use { input ->
                    BitmapFactory.decodeStream(input)
                }
            }

            coverPath != null -> BitmapFactory.decodeFile(coverPath)
            else -> null
        }
    }

    if (bitmap == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(fallbackColor)
        )
    } else {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )
    }
}
