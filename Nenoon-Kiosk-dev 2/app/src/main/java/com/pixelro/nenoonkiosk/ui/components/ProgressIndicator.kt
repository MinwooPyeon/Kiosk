package com.pixelro.nenoonkiosk.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import com.pixelro.nenoonkiosk.R

@Composable
fun ProgressIndicator() {
    CircularProgressIndicator(
        modifier = Modifier
            .padding(20.dp)
            .size(150.dp),
        color = colorResource(R.color.main),
        strokeWidth = 20.dp,
    )
}