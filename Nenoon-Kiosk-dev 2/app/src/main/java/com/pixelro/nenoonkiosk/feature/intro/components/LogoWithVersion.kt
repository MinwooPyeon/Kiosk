package com.pixelro.nenoonkiosk.feature.intro.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.core.constants.AppConstants
import com.pixelro.nenoonkiosk.core.ui.Logo

@Composable
fun LogoWithVersion() {
    Box(modifier = Modifier) {
        /**
         * 로고
         */
        Logo(modifier = Modifier
            .size(600.dp)
            .align(Alignment.Center))
        /**
         * 버전 표시
         */
        Text(
            modifier =
                Modifier
                    .padding(top = 20.dp, end = 20.dp)
                    .align(Alignment.TopEnd),
            text = "${AppConstants.APP_VERSION}",
            fontSize = 20.sp,
            textAlign = TextAlign.End,
            color = Color(0xff848484),
        )
    }
}