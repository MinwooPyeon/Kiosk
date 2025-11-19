package com.pixelro.nenoonkiosk.feature.inspection.dementia.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@Composable
fun QuestionBox(
    currentIndex: Int,
    totalQuestions: Int,
    questionText: String,
    questionTextSize: TextUnit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Progress
        Box(
            Modifier
                .padding(horizontal = 20.dp, vertical = 20.dp)
                .fillMaxWidth()
                .height(120.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .height(20.dp)
                    .fillMaxWidth(0.66f)
                    .background(White, RoundedCornerShape(8.dp))
            ) {
                val progress = (currentIndex + 1).toFloat() / totalQuestions.toFloat()
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .background(neNoon_blue, RoundedCornerShape(8.dp))
                )
            }
        }

        // Question Text
        Text(
            text = questionText,
            fontSize = questionTextSize,
            color = Color(0xFF000000),
            lineHeight = questionTextSize * 1.2f,
            modifier = Modifier
                .heightIn(min = 120.dp)
                .fillMaxWidth(),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
        )
    }
}