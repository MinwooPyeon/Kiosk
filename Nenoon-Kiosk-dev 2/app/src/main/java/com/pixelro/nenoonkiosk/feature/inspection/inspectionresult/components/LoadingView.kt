package com.pixelro.nenoonkiosk.feature.inspection.inspectionresult.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R
import com.pixelro.nenoonkiosk.core.util.StringProvider

@Composable
fun LoadingView(savedLanguage: String?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(color = Color.White),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(
                if (savedLanguage == "ko") R.drawable.loading_icon else R.drawable.loading_icon_en
            ),
            contentDescription = StringProvider.getStringComposable(R.string.tts_wait_for_result),
            modifier = Modifier.size(600.dp),
        )
    }
}