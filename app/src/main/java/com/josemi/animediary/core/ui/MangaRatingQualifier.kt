package com.josemi.animediary.core.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.josemi.animediary.core.model.RatingLabelMode
import com.josemi.animediary.core.model.automaticRatingQualifierRange
import com.josemi.animediary.core.model.automaticRatingQualifier
import com.josemi.animediary.core.model.resolvedRatingQualifier

enum class MangaRatingQualifierVariant {
    Compact,
    Prominent
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun MangaRatingQualifier(
    score: Double?,
    customLabel: String?,
    mode: RatingLabelMode,
    editable: Boolean,
    onCustomLabelChange: (String) -> Unit,
    onModeChange: (RatingLabelMode) -> Unit,
    modifier: Modifier = Modifier,
    variant: MangaRatingQualifierVariant = MangaRatingQualifierVariant.Compact
) {
    val automaticLabel = automaticRatingQualifier(score)
    val visibleLabel = resolvedRatingQualifier(score, customLabel, mode)
    val automaticRange = automaticRatingQualifierRange(score)
    val imageRes = if (mode == RatingLabelMode.Auto) automaticRange?.imageRes else null

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Mi calificativo",
                color = MangaColors.Ink,
                style = MaterialTheme.typography.titleMedium,
                fontFamily = MangaSectionFont,
                fontWeight = FontWeight.Black
            )

            AnimatedContent(
                targetState = visibleLabel to imageRes,
                transitionSpec = {
                    (fadeIn(tween(140)) + scaleIn(tween(140), initialScale = 0.96f))
                        .togetherWith(fadeOut(tween(90)) + scaleOut(tween(90), targetScale = 1.04f))
                },
                label = "ratingQualifier"
            ) { (label, drawableRes) ->
                if (drawableRes != null) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(drawableRes),
                        contentDescription = label,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(if (variant == MangaRatingQualifierVariant.Prominent) 150.dp else 122.dp)
                    )
                } else {
                    Text(
                        text = label,
                        color = MangaColors.Ink,
                        style = if (variant == MangaRatingQualifierVariant.Prominent) {
                            MaterialTheme.typography.headlineSmall
                        } else {
                            MaterialTheme.typography.titleLarge
                        },
                        fontFamily = MangaTitleFont,
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            if (editable) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    MangaChip(
                        label = "Auto",
                        selected = mode == RatingLabelMode.Auto,
                        onClick = { onModeChange(RatingLabelMode.Auto) }
                    )
                    MangaChip(
                        label = "Custom",
                        selected = mode == RatingLabelMode.Custom,
                        onClick = { onModeChange(RatingLabelMode.Custom) }
                    )
                }

                if (mode == RatingLabelMode.Custom) {
                    MangaInlineTextInput(
                        value = customLabel.orEmpty(),
                        onValueChange = onCustomLabelChange,
                        placeholder = automaticLabel
                    )
                }
            }
        }
    }
}

@Composable
private fun MangaInlineTextInput(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    androidx.compose.foundation.text.BasicTextField(
        value = value,
        onValueChange = onValueChange,
        textStyle = MaterialTheme.typography.bodyLarge.copy(
            color = MangaColors.Ink,
            fontFamily = MangaInputFont
        ),
        modifier = modifier.fillMaxWidth(),
        decorationBox = { innerTextField ->
            if (value.isEmpty()) {
                Text(
                    text = placeholder,
                    color = MangaColors.MutedInk,
                    style = MaterialTheme.typography.bodyLarge.copy(fontFamily = MangaInputFont)
                )
            }
            innerTextField()
        }
    )
}
