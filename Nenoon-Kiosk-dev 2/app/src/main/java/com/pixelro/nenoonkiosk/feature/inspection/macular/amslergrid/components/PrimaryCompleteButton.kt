package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R

@Composable
fun PrimaryCompleteButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .padding(start = 40.dp, end = 40.dp, bottom = 40.dp)
            .fillMaxWidth()
            .height(80.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(Color(0xff1d71e1))
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            modifier = Modifier.padding(bottom = 4.dp),
            text = stringResource(R.string.amsler_complete_button),
            fontSize = 40.sp,
            color = Color(0xffffffff),
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Medium,
        )
    }
}