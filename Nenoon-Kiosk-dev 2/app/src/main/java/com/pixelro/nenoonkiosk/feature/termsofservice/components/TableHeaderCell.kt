package com.pixelro.nenoonkiosk.feature.termsofservice.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit

@Composable
fun TableHeaderCell(
    text: String,
    textSize: TextUnit,
    padding: Dp,
    modifier: Modifier
) {
    Text(
        text = text,
        modifier = modifier
            .padding(padding),
        style = TextStyle(
            fontWeight = FontWeight.Bold,
            fontSize = textSize,
            textAlign = TextAlign.Center
        )
    )
}