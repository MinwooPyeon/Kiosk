package com.pixelro.nenoonkiosk.feature.inspection.macular.amslergrid.progress.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R

@Composable
fun TopCenterGuide() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 20.dp),
        contentAlignment = Alignment.TopCenter,
    ) {
        Text(
            text = buildAnnotatedString {
                append(stringResource(R.string.presbyopia_video_guide_1))
                withStyle(
                    style = SpanStyle(color = Color(0xff1d71e1), fontWeight = FontWeight.Bold),
                ) {
                    append(" " + stringResource(R.string.presbyopia_video_guide_2))
                }
                append(stringResource(R.string.presbyopia_video_guide_3))
            },
            fontWeight = FontWeight.Bold,
            fontSize = 40.sp,
            color = Color(0xffffffff),
        )
    }
}