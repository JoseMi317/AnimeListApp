package com.josemi.animediary.core.image

import android.content.Context
import android.net.Uri
import java.io.File

fun copyCoverToInternalStorage(
    context: Context,
    sourceUri: Uri,
    animeIdHint: Int,
    now: Long = System.currentTimeMillis()
): String? {
    val coversDir = File(context.filesDir, "covers")
    if (!coversDir.exists()) {
        coversDir.mkdirs()
    }

    val outputFile = File(coversDir, "anime_${animeIdHint}_$now.jpg")

    return runCatching {
        context.contentResolver.openInputStream(sourceUri)?.use { input ->
            outputFile.outputStream().use { output ->
                input.copyTo(output)
            }
        } ?: return null

        outputFile.absolutePath
    }.getOrNull()
}

fun deleteInternalCover(coverPath: String?) {
    if (coverPath == null) return

    runCatching {
        val coverFile = File(coverPath)
        if (coverFile.exists()) {
            coverFile.delete()
        }
    }
}
