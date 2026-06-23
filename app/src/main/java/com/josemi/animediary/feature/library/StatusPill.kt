package com.josemi.animediary.feature.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun StatusPill(label: String, color: Color, modifier: Modifier = Modifier) {
    Text(
        text = label,
        color = Color(0xFF101116),
        style = MaterialTheme.typography.labelMedium,
        fontWeight = FontWeight.Bold,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(color)
            .padding(horizontal = 12.dp, vertical = 7.dp)
    )
}

