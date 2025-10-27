package com.pixelro.nenoonkiosk.feature.intro.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider
import com.pixelro.nenoonkiosk.ui.theme.LightBlue

@Composable
fun IntroText() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = LightBlue)
            .padding(vertical = 20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier =
                Modifier,
            text =
                StringProvider.getStringComposable(
                    R.string.intropage_description1,
                ),
            fontSize = 28.sp,
            color = Color(0xFF1D71E1),
            fontWeight = FontWeight.Bold,
        )
        Text(
            modifier =
                Modifier
                    .padding(top = 5.dp),
            text =
                StringProvider.getStringComposable(
                    R.string.intropage_description2,
                ),
            fontSize = 28.sp,
            color = Color(0xFF1D71E1),
            fontWeight = FontWeight.Bold,
        )
    }
}