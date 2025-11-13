package com.pixelro.nenoonkiosk.feature.intro.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.ui.theme.White
import com.pixelro.nenoonkiosk.ui.theme.neNoon_blue

@Composable
fun StartButton(alphaVal: Float, toSurveyScreen: () -> Unit) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.wrapContentSize()
    ) {
        Box(
            modifier =
                Modifier
                    .width(352.dp)
                    .height(164.dp)
                    .alpha(alphaVal)
                    .border(
                        border = BorderStroke(16.dp, neNoon_blue),
                        shape = RoundedCornerShape(20.dp),
                    ),
        )
        Box(
            modifier =
                Modifier
                    .width(296.dp)
                    .height(112.dp)
                    .clip(
                        shape = RoundedCornerShape(8.dp),
                    )
                    .background(
                        color = neNoon_blue,
                        shape = RoundedCornerShape(8.dp),
                    )
                    .clickable {
                        toSurveyScreen()
                    },
            contentAlignment = Alignment.Center,
        ) {
            Text(
                text =
                    stringResource(
                        R.string.intropage_start_button,
                    ),
                fontSize = 52.sp,
                color = White,
                fontWeight = FontWeight.ExtraBold,
            )
        }
    }
}

@Preview(
    showBackground = true,
    widthDp = 1920,
    backgroundColor = 0xFFFFFFFF,
    heightDp = 1080,
    name = "Landscape - 32 inch Full HD"
)
@Composable
fun StartButtonPreview32InchFullHD() {
    StartButton(alphaVal = 1f, toSurveyScreen = {})
}

@Preview(
    showBackground = true,
    widthDp = 1280,
    backgroundColor = 0xFFFFFFFF,
    heightDp = 800,
    name = "Landscape - Standard Tablet"
)
@Composable
fun StartButtonPreviewLandscape() {
    StartButton(alphaVal = 1f, toSurveyScreen = {})
}

@Preview(
    showBackground = true,
    widthDp = 800,
    backgroundColor = 0xFFFFFFFF,
    heightDp = 1280,
    name = "Portrait - Standard Tablet"
)
@Composable
fun StartButtonPreviewPortrait() {
    StartButton(alphaVal = 1f, toSurveyScreen = {})
}
