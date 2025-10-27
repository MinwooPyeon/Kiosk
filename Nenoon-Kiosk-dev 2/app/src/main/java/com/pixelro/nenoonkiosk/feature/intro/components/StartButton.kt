package com.pixelro.nenoonkiosk.feature.intro.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun StartButton(alphaVal: Float, toSurveyScreen: () -> Unit) {
    Box(
        modifier =
            Modifier
                .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier =
                Modifier
                    .width(520.dp)
                    .height(240.dp)
                    .alpha(alphaVal)
                    .border(
                        border = BorderStroke(20.dp, Color(0xFF1D71E1)),
                        shape = RoundedCornerShape(26.dp),
                    ),
        )
        Box(
            modifier =
                Modifier
                    .width(440.dp)
                    .height(160.dp)
                    .clip(
                        shape = RoundedCornerShape(8.dp),
                    )
                    .background(
                        color = Color(0xFF1D71E1),
                        shape = RoundedCornerShape(8.dp),
                    )
                    .clickable {
                        toSurveyScreen()
                    },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text =
                    StringProvider.getStringComposable(
                        R.string.intropage_start_button,
                    ),
                fontSize = 75.sp,
                color = Color(0xFFFFFFFF),
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}