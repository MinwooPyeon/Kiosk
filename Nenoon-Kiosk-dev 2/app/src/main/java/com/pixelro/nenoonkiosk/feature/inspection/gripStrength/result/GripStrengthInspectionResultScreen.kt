package com.pixelro.nenoonkiosk.feature.inspection.gripStrength.instructions

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.ui.PrimaryButton

@Composable
fun GripStrengthInstructionsScreen(
    ttsSpeaking: Boolean,
    onStartClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = stringResource(R.string.grip_strength_instructions_title),
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            Text(
                text = stringResource(R.string.grip_strength_instructions_step1),
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.grip_strength_instructions_step2),
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = stringResource(R.string.grip_strength_instructions_step3),
                fontSize = 24.sp,
                textAlign = TextAlign.Center
            )
        }

        if (ttsSpeaking) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator()
                Text(
                    text = stringResource(R.string.tts_in_progress),
                    fontSize = 20.sp,
                    color = Color.Gray
                )
            }
        } else {
            PrimaryButton(
                text = stringResource(R.string.start_test),
                onClick = onStartClick
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 800, heightDp = 1280)
@Composable
private fun Preview_GripStrengthInstructions() {
    GripStrengthInstructionsScreen(
        ttsSpeaking = false,
        onStartClick = {}
    )
}
