package com.pixelro.nenoonkiosk.feature.inspection.dementia.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import com.pixelro.nenoonkiosk.ui.theme.White

@Composable
fun GuideImageContainer(
    imageRes: Int,
    onClose: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(color = White),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClose() },
            contentAlignment = Alignment.TopCenter,
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = imageRes),
                contentDescription = "",
            )
        }
    }
}