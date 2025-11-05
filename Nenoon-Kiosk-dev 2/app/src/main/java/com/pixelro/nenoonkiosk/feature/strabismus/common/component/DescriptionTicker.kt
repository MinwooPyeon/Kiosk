package com.pixelro.nenoonkiosk.feature.strabismus.common.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pixelro.nenoonkiosk.R

@Composable
fun DescriptionTicker(
    visible: Boolean,
    shiftVal: Float,
    savedLanguage: String
) {
    Box(
        modifier = Modifier
            .padding(start = 40.dp, end = 40.dp, bottom = 20.dp)
            .fillMaxWidth()
            .height(80.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        if (visible) {
            Text(
                modifier = Modifier.offset(y = shiftVal.dp),
                text = stringResource(R.string.test_list_description),
                fontSize = if (savedLanguage == "es") 20.sp else 38.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )
        }
    }
}