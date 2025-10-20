package com.pixelro.nenoonkiosk.core.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R

@Composable
fun InstructionItem(
    titleText: String,
    instructionText: String? = null,
    prefix: String = "",
    accent: String = "",
    suffix: String = "",
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                painter = painterResource(R.drawable.check_circle_filled),
                contentDescription = null,
                tint = colorResource(R.color.main),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            StyledText(
                text = titleText,
                style = TextStyle.Message,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Start,
                modifier = Modifier.padding(bottom = 4.dp)
            )
        }
        if (instructionText != null) {
            StyledText(
                text = instructionText,
                textAlign = TextAlign.Start,
            )
        } else {
            AccentedText(
                prefix = prefix,
                accent = accent,
                suffix = suffix,
                textAlign = TextAlign.Start,
            )
        }
    }
}