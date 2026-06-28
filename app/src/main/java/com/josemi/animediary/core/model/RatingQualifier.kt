package com.josemi.animediary.core.model

import androidx.annotation.DrawableRes
import com.josemi.animediary.R

data class RatingQualifierRange(
    val minInclusive: Double,
    val maxInclusive: Double,
    val label: String,
    @param:DrawableRes val imageRes: Int
)

val ratingQualifierRanges = listOf(
    RatingQualifierRange(0.0, 1.9, "QUE ACABO DE VER?", R.drawable.qualifier_que_acabo_de_ver),
    RatingQualifierRange(2.0, 2.9, "BODRIO", R.drawable.qualifier_bodrio),
    RatingQualifierRange(3.0, 3.9, "REALLY?", R.drawable.qualifier_really),
    RatingQualifierRange(4.0, 4.9, "MEEEH", R.drawable.qualifier_meeeh),
    RatingQualifierRange(5.0, 5.9, "MID", R.drawable.qualifier_mid),
    RatingQualifierRange(6.0, 6.9, "DECENTE", R.drawable.qualifier_decente),
    RatingQualifierRange(7.0, 7.9, "ESTA BUENO", R.drawable.qualifier_esta_bueno),
    RatingQualifierRange(8.0, 8.9, "JOYITA", R.drawable.qualifier_joyita),
    RatingQualifierRange(9.0, 9.7, "ABSOLUT CINEMA", R.drawable.qualifier_cine),
    RatingQualifierRange(9.8, 10.0, "OBRA MAESTRA", R.drawable.qualifier_obra_maestra)
)

fun automaticRatingQualifierRange(score: Double?): RatingQualifierRange? {
    if (score == null) return null

    val clampedScore = score.coerceIn(0.0, 10.0)
    return ratingQualifierRanges
        .firstOrNull { clampedScore in it.minInclusive..it.maxInclusive }
}

fun automaticRatingQualifier(score: Double?): String {
    return automaticRatingQualifierRange(score)
        ?.label
        ?: "SIN NOTA"
}

fun resolvedRatingQualifier(
    score: Double?,
    customLabel: String?,
    mode: RatingLabelMode
): String {
    return if (mode == RatingLabelMode.Custom && !customLabel.isNullOrBlank()) {
        customLabel.trim()
    } else {
        automaticRatingQualifier(score)
    }
}
