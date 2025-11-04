package com.pixelro.nenoonkiosk.feature.intro.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.isLandscape
import com.pixelro.nenoonkiosk.ui.theme.LightBlue
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@Composable
fun IntroText(modifier: Modifier = Modifier) {

    val description = "${stringResource(R.string.intropage_description1)}${if (isLandscape()) " " else "\n"}${stringResource(R.string.intropage_description2)}"

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(color = LightBlue)
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = description,
            fontSize = 28.sp,
            color = neNoon_blue,
            fontWeight = FontWeight.Bold,
        )
    }
}