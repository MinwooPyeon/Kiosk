package com.pixelro.nenoonkiosk.ui.screen

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
fun TypewriterText(
    text: AnnotatedString,
    typingSpeedMs: Long = 20,
    modifier: Modifier = Modifier
) {
    var displayedText by remember { mutableStateOf(AnnotatedString("")) }

    LaunchedEffect(text) {
        displayedText = AnnotatedString("")
        text.forEachIndexed { index, char ->
            displayedText = text.subSequence(0, index + 1)
            delay(typingSpeedMs)
        }
    }

    Text(
        text = displayedText,
        fontSize = 26.sp,
        fontWeight = FontWeight.Medium,
        modifier = modifier
    )
}